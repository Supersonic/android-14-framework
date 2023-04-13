package com.android.server.p011pm.permission;

import android.app.AppOpsManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.PermissionChecker;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.permission.ILegacyPermissionManager;
import android.util.EventLog;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FunctionalUtils;
import com.android.server.LocalServices;
import com.android.server.p011pm.PackageManagerServiceUtils;
import com.android.server.p011pm.UserManagerService;
import com.android.server.p011pm.permission.LegacyPermissionManagerInternal;
import com.android.server.p011pm.pkg.AndroidPackage;
import java.util.function.Consumer;
/* renamed from: com.android.server.pm.permission.LegacyPermissionManagerService */
/* loaded from: classes2.dex */
public class LegacyPermissionManagerService extends ILegacyPermissionManager.Stub {
    public final Context mContext;
    public final DefaultPermissionGrantPolicy mDefaultPermissionGrantPolicy;
    public final Injector mInjector;

    public static LegacyPermissionManagerInternal create(Context context) {
        LegacyPermissionManagerInternal legacyPermissionManagerInternal = (LegacyPermissionManagerInternal) LocalServices.getService(LegacyPermissionManagerInternal.class);
        if (legacyPermissionManagerInternal == null) {
            new LegacyPermissionManagerService(context);
            return (LegacyPermissionManagerInternal) LocalServices.getService(LegacyPermissionManagerInternal.class);
        }
        return legacyPermissionManagerInternal;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public LegacyPermissionManagerService(Context context) {
        this(context, new Injector(context));
        LocalServices.addService(LegacyPermissionManagerInternal.class, new Internal());
        ServiceManager.addService("legacy_permission", this);
    }

    @VisibleForTesting
    public LegacyPermissionManagerService(Context context, Injector injector) {
        this.mContext = context;
        this.mInjector = injector;
        this.mDefaultPermissionGrantPolicy = new DefaultPermissionGrantPolicy(context);
    }

    public int checkDeviceIdentifierAccess(String str, String str2, String str3, int i, int i2) {
        verifyCallerCanCheckAccess(str, str2, i, i2);
        int appId = UserHandle.getAppId(i2);
        if (appId == 1000 || appId == 0 || this.mInjector.checkPermission("android.permission.READ_PRIVILEGED_PHONE_STATE", i, i2) == 0) {
            return 0;
        }
        if (str != null) {
            long clearCallingIdentity = this.mInjector.clearCallingIdentity();
            try {
                if (((AppOpsManager) this.mInjector.getSystemService("appops")).noteOpNoThrow("android:read_device_identifiers", i2, str, str3, str2) == 0) {
                    return 0;
                }
                this.mInjector.restoreCallingIdentity(clearCallingIdentity);
                DevicePolicyManager devicePolicyManager = (DevicePolicyManager) this.mInjector.getSystemService("device_policy");
                return (devicePolicyManager == null || !devicePolicyManager.hasDeviceIdentifierAccess(str, i, i2)) ? -1 : 0;
            } finally {
                this.mInjector.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return -1;
    }

    public int checkPhoneNumberAccess(String str, String str2, String str3, int i, int i2) {
        boolean z;
        int i3;
        verifyCallerCanCheckAccess(str, str2, i, i2);
        if (this.mInjector.checkPermission("android.permission.READ_PRIVILEGED_PHONE_STATE", i, i2) == 0) {
            return 0;
        }
        int i4 = -1;
        if (str == null) {
            return -1;
        }
        if (this.mInjector.getApplicationInfo(str, i2).targetSdkVersion <= 29) {
            z = true;
            if (z || (i4 = checkPermissionAndAppop(str, "android.permission.READ_PHONE_STATE", "android:read_phone_state", str3, str2, i, i2)) != 0) {
                i3 = i4;
                if (checkPermissionAndAppop(str, null, "android:write_sms", str3, str2, i, i2) != 0 || checkPermissionAndAppop(str, "android.permission.READ_PHONE_NUMBERS", "android:read_phone_numbers", str3, str2, i, i2) == 0 || checkPermissionAndAppop(str, "android.permission.READ_SMS", "android:read_sms", str3, str2, i, i2) == 0) {
                    return 0;
                }
                return i3;
            }
            return i4;
        }
        z = false;
        if (z) {
        }
        i3 = i4;
        if (checkPermissionAndAppop(str, null, "android:write_sms", str3, str2, i, i2) != 0) {
            return 0;
        }
        return i3;
    }

    public final void verifyCallerCanCheckAccess(String str, String str2, int i, int i2) {
        int callingUid = this.mInjector.getCallingUid();
        int callingPid = this.mInjector.getCallingPid();
        boolean z = true;
        boolean z2 = UserHandle.getAppId(callingUid) >= 10000 && !(callingUid == i2 && callingPid == i);
        if (str == null || UserHandle.getAppId(i2) < 10000 || i2 == this.mInjector.getPackageUidForUser(str, UserHandle.getUserId(i2))) {
            z = z2;
        } else {
            Object[] objArr = new Object[3];
            objArr[0] = "193441322";
            objArr[1] = Integer.valueOf(UserHandle.getAppId(callingUid) >= 10000 ? callingUid : i2);
            objArr[2] = "Package uid mismatch";
            EventLog.writeEvent(1397638484, objArr);
        }
        if (z) {
            String format = String.format("Calling uid %d, pid %d cannot access for package %s (uid=%d, pid=%d): %s", Integer.valueOf(callingUid), Integer.valueOf(callingPid), str, Integer.valueOf(i2), Integer.valueOf(i), str2);
            Log.w("PermissionManager", format);
            throw new SecurityException(format);
        }
    }

    public final int checkPermissionAndAppop(String str, String str2, String str3, String str4, String str5, int i, int i2) {
        if (str2 == null || this.mInjector.checkPermission(str2, i, i2) == 0) {
            return ((AppOpsManager) this.mInjector.getSystemService("appops")).noteOpNoThrow(str3, i2, str, str4, str5) != 0 ? 1 : 0;
        }
        return -1;
    }

    public void grantDefaultPermissionsToCarrierServiceApp(final String str, final int i) {
        PackageManagerServiceUtils.enforceSystemOrRoot("grantDefaultPermissionsForCarrierServiceApp");
        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.pm.permission.LegacyPermissionManagerService$$ExternalSyntheticLambda1
            public final void runOrThrow() {
                LegacyPermissionManagerService.this.lambda$grantDefaultPermissionsToCarrierServiceApp$0(str, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$grantDefaultPermissionsToCarrierServiceApp$0(String str, int i) throws Exception {
        this.mDefaultPermissionGrantPolicy.grantDefaultPermissionsToCarrierServiceApp(str, i);
    }

    public void grantDefaultPermissionsToActiveLuiApp(final String str, final int i) {
        PackageManagerServiceUtils.enforceSystemOrPhoneCaller("grantDefaultPermissionsToActiveLuiApp", Binder.getCallingUid());
        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.pm.permission.LegacyPermissionManagerService$$ExternalSyntheticLambda6
            public final void runOrThrow() {
                LegacyPermissionManagerService.this.lambda$grantDefaultPermissionsToActiveLuiApp$1(str, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$grantDefaultPermissionsToActiveLuiApp$1(String str, int i) throws Exception {
        this.mDefaultPermissionGrantPolicy.grantDefaultPermissionsToActiveLuiApp(str, i);
    }

    public void revokeDefaultPermissionsFromLuiApps(final String[] strArr, final int i) {
        PackageManagerServiceUtils.enforceSystemOrPhoneCaller("revokeDefaultPermissionsFromLuiApps", Binder.getCallingUid());
        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.pm.permission.LegacyPermissionManagerService$$ExternalSyntheticLambda4
            public final void runOrThrow() {
                LegacyPermissionManagerService.this.lambda$revokeDefaultPermissionsFromLuiApps$2(strArr, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$revokeDefaultPermissionsFromLuiApps$2(String[] strArr, int i) throws Exception {
        this.mDefaultPermissionGrantPolicy.revokeDefaultPermissionsFromLuiApps(strArr, i);
    }

    public void grantDefaultPermissionsToEnabledImsServices(final String[] strArr, final int i) {
        PackageManagerServiceUtils.enforceSystemOrPhoneCaller("grantDefaultPermissionsToEnabledImsServices", Binder.getCallingUid());
        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.pm.permission.LegacyPermissionManagerService$$ExternalSyntheticLambda3
            public final void runOrThrow() {
                LegacyPermissionManagerService.this.lambda$grantDefaultPermissionsToEnabledImsServices$3(strArr, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$grantDefaultPermissionsToEnabledImsServices$3(String[] strArr, int i) throws Exception {
        this.mDefaultPermissionGrantPolicy.grantDefaultPermissionsToEnabledImsServices(strArr, i);
    }

    public void grantDefaultPermissionsToEnabledTelephonyDataServices(final String[] strArr, final int i) {
        PackageManagerServiceUtils.enforceSystemOrPhoneCaller("grantDefaultPermissionsToEnabledTelephonyDataServices", Binder.getCallingUid());
        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.pm.permission.LegacyPermissionManagerService$$ExternalSyntheticLambda5
            public final void runOrThrow() {
                LegacyPermissionManagerService.this.lambda$grantDefaultPermissionsToEnabledTelephonyDataServices$4(strArr, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$grantDefaultPermissionsToEnabledTelephonyDataServices$4(String[] strArr, int i) throws Exception {
        this.mDefaultPermissionGrantPolicy.grantDefaultPermissionsToEnabledTelephonyDataServices(strArr, i);
    }

    public void revokeDefaultPermissionsFromDisabledTelephonyDataServices(final String[] strArr, final int i) {
        PackageManagerServiceUtils.enforceSystemOrPhoneCaller("revokeDefaultPermissionsFromDisabledTelephonyDataServices", Binder.getCallingUid());
        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.pm.permission.LegacyPermissionManagerService$$ExternalSyntheticLambda0
            public final void runOrThrow() {
                LegacyPermissionManagerService.this.m32x2e609f05(strArr, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: lambda$revokeDefaultPermissionsFromDisabledTelephonyDataServices$5 */
    public /* synthetic */ void m32x2e609f05(String[] strArr, int i) throws Exception {
        this.mDefaultPermissionGrantPolicy.revokeDefaultPermissionsFromDisabledTelephonyDataServices(strArr, i);
    }

    public void grantDefaultPermissionsToEnabledCarrierApps(final String[] strArr, final int i) {
        PackageManagerServiceUtils.enforceSystemOrPhoneCaller("grantPermissionsToEnabledCarrierApps", Binder.getCallingUid());
        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.pm.permission.LegacyPermissionManagerService$$ExternalSyntheticLambda2
            public final void runOrThrow() {
                LegacyPermissionManagerService.this.lambda$grantDefaultPermissionsToEnabledCarrierApps$6(strArr, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$grantDefaultPermissionsToEnabledCarrierApps$6(String[] strArr, int i) throws Exception {
        this.mDefaultPermissionGrantPolicy.grantDefaultPermissionsToEnabledCarrierApps(strArr, i);
    }

    /* renamed from: com.android.server.pm.permission.LegacyPermissionManagerService$Internal */
    /* loaded from: classes2.dex */
    public class Internal implements LegacyPermissionManagerInternal {
        public Internal() {
        }

        @Override // com.android.server.p011pm.permission.LegacyPermissionManagerInternal
        public void resetRuntimePermissions() {
            int[] userIds;
            LegacyPermissionManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.REVOKE_RUNTIME_PERMISSIONS", "revokeRuntimePermission");
            int callingUid = Binder.getCallingUid();
            if (callingUid != 1000 && callingUid != 0) {
                LegacyPermissionManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL", "resetRuntimePermissions");
            }
            PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
            final PermissionManagerServiceInternal permissionManagerServiceInternal = (PermissionManagerServiceInternal) LocalServices.getService(PermissionManagerServiceInternal.class);
            for (final int i : UserManagerService.getInstance().getUserIds()) {
                packageManagerInternal.forEachPackage(new Consumer() { // from class: com.android.server.pm.permission.LegacyPermissionManagerService$Internal$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        PermissionManagerServiceInternal.this.resetRuntimePermissions((AndroidPackage) obj, i);
                    }
                });
            }
        }

        @Override // com.android.server.p011pm.permission.LegacyPermissionManagerInternal
        public void setDialerAppPackagesProvider(LegacyPermissionManagerInternal.PackagesProvider packagesProvider) {
            LegacyPermissionManagerService.this.mDefaultPermissionGrantPolicy.setDialerAppPackagesProvider(packagesProvider);
        }

        @Override // com.android.server.p011pm.permission.LegacyPermissionManagerInternal
        public void setLocationExtraPackagesProvider(LegacyPermissionManagerInternal.PackagesProvider packagesProvider) {
            LegacyPermissionManagerService.this.mDefaultPermissionGrantPolicy.setLocationExtraPackagesProvider(packagesProvider);
        }

        @Override // com.android.server.p011pm.permission.LegacyPermissionManagerInternal
        public void setLocationPackagesProvider(LegacyPermissionManagerInternal.PackagesProvider packagesProvider) {
            LegacyPermissionManagerService.this.mDefaultPermissionGrantPolicy.setLocationPackagesProvider(packagesProvider);
        }

        @Override // com.android.server.p011pm.permission.LegacyPermissionManagerInternal
        public void setSimCallManagerPackagesProvider(LegacyPermissionManagerInternal.PackagesProvider packagesProvider) {
            LegacyPermissionManagerService.this.mDefaultPermissionGrantPolicy.setSimCallManagerPackagesProvider(packagesProvider);
        }

        @Override // com.android.server.p011pm.permission.LegacyPermissionManagerInternal
        public void setSmsAppPackagesProvider(LegacyPermissionManagerInternal.PackagesProvider packagesProvider) {
            LegacyPermissionManagerService.this.mDefaultPermissionGrantPolicy.setSmsAppPackagesProvider(packagesProvider);
        }

        @Override // com.android.server.p011pm.permission.LegacyPermissionManagerInternal
        public void setSyncAdapterPackagesProvider(LegacyPermissionManagerInternal.SyncAdapterPackagesProvider syncAdapterPackagesProvider) {
            LegacyPermissionManagerService.this.mDefaultPermissionGrantPolicy.setSyncAdapterPackagesProvider(syncAdapterPackagesProvider);
        }

        @Override // com.android.server.p011pm.permission.LegacyPermissionManagerInternal
        public void setUseOpenWifiAppPackagesProvider(LegacyPermissionManagerInternal.PackagesProvider packagesProvider) {
            LegacyPermissionManagerService.this.mDefaultPermissionGrantPolicy.setUseOpenWifiAppPackagesProvider(packagesProvider);
        }

        @Override // com.android.server.p011pm.permission.LegacyPermissionManagerInternal
        public void setVoiceInteractionPackagesProvider(LegacyPermissionManagerInternal.PackagesProvider packagesProvider) {
            LegacyPermissionManagerService.this.mDefaultPermissionGrantPolicy.setVoiceInteractionPackagesProvider(packagesProvider);
        }

        @Override // com.android.server.p011pm.permission.LegacyPermissionManagerInternal
        public void grantDefaultPermissionsToDefaultSimCallManager(String str, int i) {
            LegacyPermissionManagerService.this.mDefaultPermissionGrantPolicy.grantDefaultPermissionsToDefaultSimCallManager(str, i);
        }

        @Override // com.android.server.p011pm.permission.LegacyPermissionManagerInternal
        public void grantDefaultPermissionsToDefaultUseOpenWifiApp(String str, int i) {
            LegacyPermissionManagerService.this.mDefaultPermissionGrantPolicy.grantDefaultPermissionsToDefaultUseOpenWifiApp(str, i);
        }

        @Override // com.android.server.p011pm.permission.LegacyPermissionManagerInternal
        public void grantDefaultPermissions(int i) {
            LegacyPermissionManagerService.this.mDefaultPermissionGrantPolicy.grantDefaultPermissions(i);
        }

        @Override // com.android.server.p011pm.permission.LegacyPermissionManagerInternal
        public void scheduleReadDefaultPermissionExceptions() {
            LegacyPermissionManagerService.this.mDefaultPermissionGrantPolicy.scheduleReadDefaultPermissionExceptions();
        }

        @Override // com.android.server.p011pm.permission.LegacyPermissionManagerInternal
        public int checkSoundTriggerRecordAudioPermissionForDataDelivery(int i, String str, String str2, String str3) {
            int checkPermissionForPreflight = PermissionChecker.checkPermissionForPreflight(LegacyPermissionManagerService.this.mContext, "android.permission.RECORD_AUDIO", -1, i, str);
            if (checkPermissionForPreflight != 0) {
                return checkPermissionForPreflight;
            }
            ((AppOpsManager) LegacyPermissionManagerService.this.mContext.getSystemService(AppOpsManager.class)).noteOpNoThrow(120, i, str, str2, str3);
            return checkPermissionForPreflight;
        }
    }

    @VisibleForTesting
    /* renamed from: com.android.server.pm.permission.LegacyPermissionManagerService$Injector */
    /* loaded from: classes2.dex */
    public static class Injector {
        public final Context mContext;
        public final PackageManagerInternal mPackageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);

        public Injector(Context context) {
            this.mContext = context;
        }

        public int getCallingUid() {
            return Binder.getCallingUid();
        }

        public int getCallingPid() {
            return Binder.getCallingPid();
        }

        public int checkPermission(String str, int i, int i2) {
            return this.mContext.checkPermission(str, i, i2);
        }

        public long clearCallingIdentity() {
            return Binder.clearCallingIdentity();
        }

        public void restoreCallingIdentity(long j) {
            Binder.restoreCallingIdentity(j);
        }

        public Object getSystemService(String str) {
            return this.mContext.getSystemService(str);
        }

        public ApplicationInfo getApplicationInfo(String str, int i) throws PackageManager.NameNotFoundException {
            return this.mContext.getPackageManager().getApplicationInfoAsUser(str, 0, UserHandle.getUserHandleForUid(i));
        }

        public int getPackageUidForUser(String str, int i) {
            return this.mPackageManagerInternal.getPackageUid(str, 0L, i);
        }
    }
}
