package com.android.server.p011pm.permission;

import android.annotation.EnforcePermission;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.content.AttributionSource;
import android.content.AttributionSourceState;
import android.content.Context;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.permission.SplitPermissionInfoParcelable;
import android.health.connect.HealthConnectManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.permission.IOnPermissionsChangeListener;
import android.permission.IPermissionChecker;
import android.permission.IPermissionManager;
import android.permission.PermissionManager;
import android.permission.PermissionManagerInternal;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.Preconditions;
import com.android.internal.util.function.TriFunction;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.LocalServices;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.p011pm.UserManagerService;
import com.android.server.p011pm.permission.PermissionManagerService;
import com.android.server.p011pm.permission.PermissionManagerServiceInternal;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
/* renamed from: com.android.server.pm.permission.PermissionManagerService */
/* loaded from: classes2.dex */
public class PermissionManagerService extends IPermissionManager.Stub {
    public static final String LOG_TAG = PermissionManagerService.class.getSimpleName();
    public static final ConcurrentHashMap<IBinder, RegisteredAttribution> sRunningAttributionSources = new ConcurrentHashMap<>();
    public final AppOpsManager mAppOpsManager;
    public final AttributionSourceRegistry mAttributionSourceRegistry;
    @GuardedBy({"mLock"})
    public CheckPermissionDelegate mCheckPermissionDelegate;
    public final Context mContext;
    public PermissionManagerServiceInternal.HotwordDetectionServiceProvider mHotwordDetectionServiceProvider;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final SparseArray<OneTimePermissionUserManager> mOneTimePermissionUserManagers = new SparseArray<>();
    public final PackageManagerInternal mPackageManagerInt;
    public final PermissionManagerServiceInterface mPermissionManagerServiceImpl;
    public final UserManagerInternal mUserManagerInt;

    /* renamed from: com.android.server.pm.permission.PermissionManagerService$CheckPermissionDelegate */
    /* loaded from: classes2.dex */
    public interface CheckPermissionDelegate {
        int checkPermission(String str, String str2, int i, TriFunction<String, String, Integer, Integer> triFunction);

        int checkUidPermission(int i, String str, BiFunction<Integer, String, Integer> biFunction);

        List<String> getDelegatedPermissionNames();

        int getDelegatedUid();
    }

    public PermissionManagerService(Context context, ArrayMap<String, FeatureInfo> arrayMap) {
        PackageManager.invalidatePackageInfoCache();
        PermissionManager.disablePackageNamePermissionCache();
        this.mContext = context;
        this.mPackageManagerInt = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        this.mUserManagerInt = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
        this.mAppOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        this.mAttributionSourceRegistry = new AttributionSourceRegistry(context);
        PermissionManagerServiceInternalImpl permissionManagerServiceInternalImpl = new PermissionManagerServiceInternalImpl();
        LocalServices.addService(PermissionManagerServiceInternal.class, permissionManagerServiceInternalImpl);
        LocalServices.addService(PermissionManagerInternal.class, permissionManagerServiceInternalImpl);
        this.mPermissionManagerServiceImpl = new PermissionManagerServiceImpl(context, arrayMap);
    }

    public static PermissionManagerServiceInternal create(Context context, ArrayMap<String, FeatureInfo> arrayMap) {
        PermissionManagerServiceInternal permissionManagerServiceInternal = (PermissionManagerServiceInternal) LocalServices.getService(PermissionManagerServiceInternal.class);
        if (permissionManagerServiceInternal != null) {
            return permissionManagerServiceInternal;
        }
        if (((PermissionManagerService) ServiceManager.getService("permissionmgr")) == null) {
            ServiceManager.addService("permissionmgr", new PermissionManagerService(context, arrayMap));
            ServiceManager.addService("permission_checker", new PermissionCheckerService(context));
        }
        return (PermissionManagerServiceInternal) LocalServices.getService(PermissionManagerServiceInternal.class);
    }

    public static void killUid(int i, int i2, String str) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            IActivityManager service = ActivityManager.getService();
            if (service != null) {
                try {
                    service.killUidForPermissionChange(i, i2, str);
                } catch (RemoteException unused) {
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final int checkPermission(String str, String str2, int i) {
        CheckPermissionDelegate checkPermissionDelegate;
        if (str == null || str2 == null) {
            return -1;
        }
        synchronized (this.mLock) {
            checkPermissionDelegate = this.mCheckPermissionDelegate;
        }
        if (checkPermissionDelegate == null) {
            return this.mPermissionManagerServiceImpl.checkPermission(str, str2, i);
        }
        final PermissionManagerServiceInterface permissionManagerServiceInterface = this.mPermissionManagerServiceImpl;
        Objects.requireNonNull(permissionManagerServiceInterface);
        return checkPermissionDelegate.checkPermission(str, str2, i, new TriFunction() { // from class: com.android.server.pm.permission.PermissionManagerService$$ExternalSyntheticLambda1
            public final Object apply(Object obj, Object obj2, Object obj3) {
                return Integer.valueOf(PermissionManagerServiceInterface.this.checkPermission((String) obj, (String) obj2, ((Integer) obj3).intValue()));
            }
        });
    }

    public final int checkUidPermission(int i, String str) {
        CheckPermissionDelegate checkPermissionDelegate;
        if (str == null) {
            return -1;
        }
        synchronized (this.mLock) {
            checkPermissionDelegate = this.mCheckPermissionDelegate;
        }
        if (checkPermissionDelegate == null) {
            return this.mPermissionManagerServiceImpl.checkUidPermission(i, str);
        }
        final PermissionManagerServiceInterface permissionManagerServiceInterface = this.mPermissionManagerServiceImpl;
        Objects.requireNonNull(permissionManagerServiceInterface);
        return checkPermissionDelegate.checkUidPermission(i, str, new BiFunction() { // from class: com.android.server.pm.permission.PermissionManagerService$$ExternalSyntheticLambda2
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return Integer.valueOf(PermissionManagerServiceInterface.this.checkUidPermission(((Integer) obj).intValue(), (String) obj2));
            }
        });
    }

    public boolean setAutoRevokeExempted(String str, boolean z, int i) {
        Objects.requireNonNull(str);
        AndroidPackage androidPackage = this.mPackageManagerInt.getPackage(str);
        if (checkAutoRevokeAccess(androidPackage, Binder.getCallingUid())) {
            return setAutoRevokeExemptedInternal(androidPackage, z, i);
        }
        return false;
    }

    public final boolean setAutoRevokeExemptedInternal(AndroidPackage androidPackage, boolean z, int i) {
        int uid = UserHandle.getUid(i, androidPackage.getUid());
        if (this.mAppOpsManager.checkOpNoThrow(98, uid, androidPackage.getPackageName()) != 0) {
            return false;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mAppOpsManager.setMode(97, uid, androidPackage.getPackageName(), z ? 1 : 0);
            return true;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final boolean checkAutoRevokeAccess(AndroidPackage androidPackage, int i) {
        boolean z = this.mContext.checkCallingOrSelfPermission("android.permission.WHITELIST_AUTO_REVOKE_PERMISSIONS") == 0;
        boolean isCallerInstallerOfRecord = this.mPackageManagerInt.isCallerInstallerOfRecord(androidPackage, i);
        if (z || isCallerInstallerOfRecord) {
            return (androidPackage == null || this.mPackageManagerInt.filterAppAccess(androidPackage, i, UserHandle.getUserId(i))) ? false : true;
        }
        throw new SecurityException("Caller must either hold android.permission.WHITELIST_AUTO_REVOKE_PERMISSIONS or be the installer on record");
    }

    public boolean isAutoRevokeExempted(String str, int i) {
        Objects.requireNonNull(str);
        AndroidPackage androidPackage = this.mPackageManagerInt.getPackage(str);
        if (checkAutoRevokeAccess(androidPackage, Binder.getCallingUid())) {
            int uid = UserHandle.getUid(i, androidPackage.getUid());
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return this.mAppOpsManager.checkOpNoThrow(97, uid, str) == 1;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return false;
    }

    public final void startShellPermissionIdentityDelegationInternal(int i, String str, List<String> list) {
        synchronized (this.mLock) {
            CheckPermissionDelegate checkPermissionDelegate = this.mCheckPermissionDelegate;
            if (checkPermissionDelegate != null && checkPermissionDelegate.getDelegatedUid() != i) {
                throw new SecurityException("Shell can delegate permissions only to one UID at a time");
            }
            setCheckPermissionDelegateLocked(new ShellDelegate(i, str, list));
        }
    }

    public final void stopShellPermissionIdentityDelegationInternal() {
        synchronized (this.mLock) {
            setCheckPermissionDelegateLocked(null);
        }
    }

    public final List<String> getDelegatedShellPermissionsInternal() {
        synchronized (this.mLock) {
            CheckPermissionDelegate checkPermissionDelegate = this.mCheckPermissionDelegate;
            if (checkPermissionDelegate == null) {
                return Collections.EMPTY_LIST;
            }
            return checkPermissionDelegate.getDelegatedPermissionNames();
        }
    }

    public final void setCheckPermissionDelegateLocked(CheckPermissionDelegate checkPermissionDelegate) {
        if (checkPermissionDelegate != null || this.mCheckPermissionDelegate != null) {
            PackageManager.invalidatePackageInfoCache();
        }
        this.mCheckPermissionDelegate = checkPermissionDelegate;
    }

    public final OneTimePermissionUserManager getOneTimePermissionUserManager(int i) {
        synchronized (this.mLock) {
            OneTimePermissionUserManager oneTimePermissionUserManager = this.mOneTimePermissionUserManagers.get(i);
            if (oneTimePermissionUserManager != null) {
                return oneTimePermissionUserManager;
            }
            OneTimePermissionUserManager oneTimePermissionUserManager2 = new OneTimePermissionUserManager(this.mContext.createContextAsUser(UserHandle.of(i), 0));
            synchronized (this.mLock) {
                OneTimePermissionUserManager oneTimePermissionUserManager3 = this.mOneTimePermissionUserManagers.get(i);
                if (oneTimePermissionUserManager3 != null) {
                    return oneTimePermissionUserManager3;
                }
                this.mOneTimePermissionUserManagers.put(i, oneTimePermissionUserManager2);
                oneTimePermissionUserManager2.registerUninstallListener();
                return oneTimePermissionUserManager2;
            }
        }
    }

    public void startOneTimePermissionSession(String str, int i, long j, long j2) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_ONE_TIME_PERMISSION_SESSIONS", "Must hold android.permission.MANAGE_ONE_TIME_PERMISSION_SESSIONS to register permissions as one time.");
        Objects.requireNonNull(str);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            getOneTimePermissionUserManager(i).startPackageOneTimeSession(str, j, j2);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @EnforcePermission("android.permission.MANAGE_ONE_TIME_PERMISSION_SESSIONS")
    public void stopOneTimePermissionSession(String str, int i) {
        super.stopOneTimePermissionSession_enforcePermission();
        Objects.requireNonNull(str);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            getOneTimePermissionUserManager(i).stopPackageOneTimeSession(str);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void registerAttributionSource(AttributionSourceState attributionSourceState) {
        this.mAttributionSourceRegistry.registerAttributionSource(new AttributionSource(attributionSourceState));
    }

    public boolean isRegisteredAttributionSource(AttributionSourceState attributionSourceState) {
        return this.mAttributionSourceRegistry.isRegisteredAttributionSource(new AttributionSource(attributionSourceState));
    }

    public List<String> getAutoRevokeExemptionRequestedPackages(int i) {
        return getPackagesWithAutoRevokePolicy(1, i);
    }

    public List<String> getAutoRevokeExemptionGrantedPackages(int i) {
        return getPackagesWithAutoRevokePolicy(2, i);
    }

    public final List<String> getPackagesWithAutoRevokePolicy(final int i, int i2) {
        this.mContext.enforceCallingPermission("android.permission.ADJUST_RUNTIME_PERMISSIONS_POLICY", "Must hold android.permission.ADJUST_RUNTIME_PERMISSIONS_POLICY");
        final ArrayList arrayList = new ArrayList();
        this.mPackageManagerInt.forEachInstalledPackage(new Consumer() { // from class: com.android.server.pm.permission.PermissionManagerService$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PermissionManagerService.lambda$getPackagesWithAutoRevokePolicy$0(i, arrayList, (AndroidPackage) obj);
            }
        }, i2);
        return arrayList;
    }

    public static /* synthetic */ void lambda$getPackagesWithAutoRevokePolicy$0(int i, List list, AndroidPackage androidPackage) {
        if (androidPackage.getAutoRevokePermissions() == i) {
            list.add(androidPackage.getPackageName());
        }
    }

    public ParceledListSlice<PermissionGroupInfo> getAllPermissionGroups(int i) {
        return new ParceledListSlice<>(this.mPermissionManagerServiceImpl.getAllPermissionGroups(i));
    }

    public PermissionGroupInfo getPermissionGroupInfo(String str, int i) {
        return this.mPermissionManagerServiceImpl.getPermissionGroupInfo(str, i);
    }

    public PermissionInfo getPermissionInfo(String str, String str2, int i) {
        return this.mPermissionManagerServiceImpl.getPermissionInfo(str, i, str2);
    }

    public ParceledListSlice<PermissionInfo> queryPermissionsByGroup(String str, int i) {
        List<PermissionInfo> queryPermissionsByGroup = this.mPermissionManagerServiceImpl.queryPermissionsByGroup(str, i);
        if (queryPermissionsByGroup == null) {
            return null;
        }
        return new ParceledListSlice<>(queryPermissionsByGroup);
    }

    public boolean addPermission(PermissionInfo permissionInfo, boolean z) {
        return this.mPermissionManagerServiceImpl.addPermission(permissionInfo, z);
    }

    public void removePermission(String str) {
        this.mPermissionManagerServiceImpl.removePermission(str);
    }

    public int getPermissionFlags(String str, String str2, int i) {
        return this.mPermissionManagerServiceImpl.getPermissionFlags(str, str2, i);
    }

    public void updatePermissionFlags(String str, String str2, int i, int i2, boolean z, int i3) {
        this.mPermissionManagerServiceImpl.updatePermissionFlags(str, str2, i, i2, z, i3);
    }

    public void updatePermissionFlagsForAllApps(int i, int i2, int i3) {
        this.mPermissionManagerServiceImpl.updatePermissionFlagsForAllApps(i, i2, i3);
    }

    public void addOnPermissionsChangeListener(IOnPermissionsChangeListener iOnPermissionsChangeListener) {
        this.mPermissionManagerServiceImpl.addOnPermissionsChangeListener(iOnPermissionsChangeListener);
    }

    public void removeOnPermissionsChangeListener(IOnPermissionsChangeListener iOnPermissionsChangeListener) {
        this.mPermissionManagerServiceImpl.removeOnPermissionsChangeListener(iOnPermissionsChangeListener);
    }

    public List<String> getAllowlistedRestrictedPermissions(String str, int i, int i2) {
        return this.mPermissionManagerServiceImpl.getAllowlistedRestrictedPermissions(str, i, i2);
    }

    public boolean addAllowlistedRestrictedPermission(String str, String str2, int i, int i2) {
        return this.mPermissionManagerServiceImpl.addAllowlistedRestrictedPermission(str, str2, i, i2);
    }

    public boolean removeAllowlistedRestrictedPermission(String str, String str2, int i, int i2) {
        return this.mPermissionManagerServiceImpl.removeAllowlistedRestrictedPermission(str, str2, i, i2);
    }

    public void grantRuntimePermission(String str, String str2, int i) {
        this.mPermissionManagerServiceImpl.grantRuntimePermission(str, str2, i);
    }

    public void revokeRuntimePermission(String str, String str2, int i, String str3) {
        this.mPermissionManagerServiceImpl.revokeRuntimePermission(str, str2, i, str3);
    }

    public void revokePostNotificationPermissionWithoutKillForTest(String str, int i) {
        this.mPermissionManagerServiceImpl.revokePostNotificationPermissionWithoutKillForTest(str, i);
    }

    public boolean shouldShowRequestPermissionRationale(String str, String str2, int i) {
        return this.mPermissionManagerServiceImpl.shouldShowRequestPermissionRationale(str, str2, i);
    }

    public boolean isPermissionRevokedByPolicy(String str, String str2, int i) {
        return this.mPermissionManagerServiceImpl.isPermissionRevokedByPolicy(str, str2, i);
    }

    public List<SplitPermissionInfoParcelable> getSplitPermissions() {
        return this.mPermissionManagerServiceImpl.getSplitPermissions();
    }

    /* renamed from: com.android.server.pm.permission.PermissionManagerService$PermissionManagerServiceInternalImpl */
    /* loaded from: classes2.dex */
    public class PermissionManagerServiceInternalImpl implements PermissionManagerServiceInternal {
        public PermissionManagerServiceInternalImpl() {
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public int checkPermission(String str, String str2, int i) {
            return PermissionManagerService.this.checkPermission(str, str2, i);
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public int checkUidPermission(int i, String str) {
            return PermissionManagerService.this.checkUidPermission(i, str);
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public void startShellPermissionIdentityDelegation(int i, String str, List<String> list) {
            Objects.requireNonNull(str, "packageName");
            PermissionManagerService.this.startShellPermissionIdentityDelegationInternal(i, str, list);
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public void stopShellPermissionIdentityDelegation() {
            PermissionManagerService.this.stopShellPermissionIdentityDelegationInternal();
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public List<String> getDelegatedShellPermissions() {
            return PermissionManagerService.this.getDelegatedShellPermissionsInternal();
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public void setHotwordDetectionServiceProvider(PermissionManagerServiceInternal.HotwordDetectionServiceProvider hotwordDetectionServiceProvider) {
            PermissionManagerService.this.mHotwordDetectionServiceProvider = hotwordDetectionServiceProvider;
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public PermissionManagerServiceInternal.HotwordDetectionServiceProvider getHotwordDetectionServiceProvider() {
            return PermissionManagerService.this.mHotwordDetectionServiceProvider;
        }

        @Override // com.android.server.p011pm.permission.LegacyPermissionDataProvider
        public int[] getGidsForUid(int i) {
            return PermissionManagerService.this.mPermissionManagerServiceImpl.getGidsForUid(i);
        }

        @Override // com.android.server.p011pm.permission.LegacyPermissionDataProvider
        public Map<String, Set<String>> getAllAppOpPermissionPackages() {
            return PermissionManagerService.this.mPermissionManagerServiceImpl.getAllAppOpPermissionPackages();
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public void onUserCreated(int i) {
            PermissionManagerService.this.mPermissionManagerServiceImpl.onUserCreated(i);
        }

        @Override // com.android.server.p011pm.permission.LegacyPermissionDataProvider
        public List<LegacyPermission> getLegacyPermissions() {
            return PermissionManagerService.this.mPermissionManagerServiceImpl.getLegacyPermissions();
        }

        @Override // com.android.server.p011pm.permission.LegacyPermissionDataProvider
        public LegacyPermissionState getLegacyPermissionState(int i) {
            return PermissionManagerService.this.mPermissionManagerServiceImpl.getLegacyPermissionState(i);
        }

        public byte[] backupRuntimePermissions(int i) {
            return PermissionManagerService.this.mPermissionManagerServiceImpl.backupRuntimePermissions(i);
        }

        public void restoreRuntimePermissions(byte[] bArr, int i) {
            PermissionManagerService.this.mPermissionManagerServiceImpl.restoreRuntimePermissions(bArr, i);
        }

        public void restoreDelayedRuntimePermissions(String str, int i) {
            PermissionManagerService.this.mPermissionManagerServiceImpl.restoreDelayedRuntimePermissions(str, i);
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public void readLegacyPermissionsTEMP(LegacyPermissionSettings legacyPermissionSettings) {
            PermissionManagerService.this.mPermissionManagerServiceImpl.readLegacyPermissionsTEMP(legacyPermissionSettings);
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public void writeLegacyPermissionsTEMP(LegacyPermissionSettings legacyPermissionSettings) {
            PermissionManagerService.this.mPermissionManagerServiceImpl.writeLegacyPermissionsTEMP(legacyPermissionSettings);
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public void onPackageAdded(PackageState packageState, boolean z, AndroidPackage androidPackage) {
            PermissionManagerService.this.mPermissionManagerServiceImpl.onPackageAdded(packageState, z, androidPackage);
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public void onPackageInstalled(AndroidPackage androidPackage, int i, PermissionManagerServiceInternal.PackageInstalledParams packageInstalledParams, int i2) {
            int[] iArr;
            Objects.requireNonNull(androidPackage, "pkg");
            Objects.requireNonNull(packageInstalledParams, "params");
            Preconditions.checkArgument(i2 >= 0 || i2 == -1, "userId");
            PermissionManagerService.this.mPermissionManagerServiceImpl.onPackageInstalled(androidPackage, i, packageInstalledParams, i2);
            if (i2 == -1) {
                iArr = PermissionManagerService.this.getAllUserIds();
            } else {
                iArr = new int[]{i2};
            }
            for (int i3 : iArr) {
                int autoRevokePermissionsMode = packageInstalledParams.getAutoRevokePermissionsMode();
                if (autoRevokePermissionsMode == 0 || autoRevokePermissionsMode == 1) {
                    PermissionManagerService.this.setAutoRevokeExemptedInternal(androidPackage, autoRevokePermissionsMode == 1, i3);
                }
            }
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public void onPackageRemoved(AndroidPackage androidPackage) {
            PermissionManagerService.this.mPermissionManagerServiceImpl.onPackageRemoved(androidPackage);
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public void onPackageUninstalled(String str, int i, PackageState packageState, AndroidPackage androidPackage, List<AndroidPackage> list, int i2) {
            PermissionManagerService.this.mPermissionManagerServiceImpl.onPackageUninstalled(str, i, packageState, androidPackage, list, i2);
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public void addOnRuntimePermissionStateChangedListener(PermissionManagerServiceInternal.OnRuntimePermissionStateChangedListener onRuntimePermissionStateChangedListener) {
            PermissionManagerService.this.mPermissionManagerServiceImpl.addOnRuntimePermissionStateChangedListener(onRuntimePermissionStateChangedListener);
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public void onSystemReady() {
            PermissionManagerService.this.mPermissionManagerServiceImpl.onSystemReady();
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public boolean isPermissionsReviewRequired(String str, int i) {
            return PermissionManagerService.this.mPermissionManagerServiceImpl.isPermissionsReviewRequired(str, i);
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public void readLegacyPermissionStateTEMP() {
            PermissionManagerService.this.mPermissionManagerServiceImpl.readLegacyPermissionStateTEMP();
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal, com.android.server.p011pm.permission.LegacyPermissionDataProvider
        public void writeLegacyPermissionStateTEMP() {
            PermissionManagerService.this.mPermissionManagerServiceImpl.writeLegacyPermissionStateTEMP();
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public void onUserRemoved(int i) {
            PermissionManagerService.this.mPermissionManagerServiceImpl.onUserRemoved(i);
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public Set<String> getInstalledPermissions(String str) {
            return PermissionManagerService.this.mPermissionManagerServiceImpl.getInstalledPermissions(str);
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public Set<String> getGrantedPermissions(String str, int i) {
            return PermissionManagerService.this.mPermissionManagerServiceImpl.getGrantedPermissions(str, i);
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public int[] getPermissionGids(String str, int i) {
            return PermissionManagerService.this.mPermissionManagerServiceImpl.getPermissionGids(str, i);
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public String[] getAppOpPermissionPackages(String str) {
            return PermissionManagerService.this.mPermissionManagerServiceImpl.getAppOpPermissionPackages(str);
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public void onStorageVolumeMounted(String str, boolean z) {
            PermissionManagerService.this.mPermissionManagerServiceImpl.onStorageVolumeMounted(str, z);
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public void resetRuntimePermissions(AndroidPackage androidPackage, int i) {
            PermissionManagerService.this.mPermissionManagerServiceImpl.resetRuntimePermissions(androidPackage, i);
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public void resetRuntimePermissionsForUser(int i) {
            PermissionManagerService.this.mPermissionManagerServiceImpl.resetRuntimePermissionsForUser(i);
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public Permission getPermissionTEMP(String str) {
            return PermissionManagerService.this.mPermissionManagerServiceImpl.getPermissionTEMP(str);
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public List<PermissionInfo> getAllPermissionsWithProtection(int i) {
            return PermissionManagerService.this.mPermissionManagerServiceImpl.getAllPermissionsWithProtection(i);
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal
        public List<PermissionInfo> getAllPermissionsWithProtectionFlags(int i) {
            return PermissionManagerService.this.mPermissionManagerServiceImpl.getAllPermissionsWithProtectionFlags(i);
        }
    }

    public final int[] getAllUserIds() {
        return UserManagerService.getInstance().getUserIdsIncludingPreCreated();
    }

    /* renamed from: com.android.server.pm.permission.PermissionManagerService$ShellDelegate */
    /* loaded from: classes2.dex */
    public class ShellDelegate implements CheckPermissionDelegate {
        public final String mDelegatedPackageName;
        public final List<String> mDelegatedPermissionNames;
        public final int mDelegatedUid;

        public ShellDelegate(int i, String str, List<String> list) {
            this.mDelegatedUid = i;
            this.mDelegatedPackageName = str;
            this.mDelegatedPermissionNames = list;
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerService.CheckPermissionDelegate
        public int getDelegatedUid() {
            return this.mDelegatedUid;
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerService.CheckPermissionDelegate
        public int checkPermission(String str, String str2, int i, TriFunction<String, String, Integer, Integer> triFunction) {
            if (this.mDelegatedPackageName.equals(str) && isDelegatedPermission(str2)) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    return ((Integer) triFunction.apply("com.android.shell", str2, Integer.valueOf(i))).intValue();
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return ((Integer) triFunction.apply(str, str2, Integer.valueOf(i))).intValue();
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerService.CheckPermissionDelegate
        public int checkUidPermission(int i, String str, BiFunction<Integer, String, Integer> biFunction) {
            if (i == this.mDelegatedUid && isDelegatedPermission(str)) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    return biFunction.apply(2000, str).intValue();
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return biFunction.apply(Integer.valueOf(i), str).intValue();
        }

        @Override // com.android.server.p011pm.permission.PermissionManagerService.CheckPermissionDelegate
        public List<String> getDelegatedPermissionNames() {
            if (this.mDelegatedPermissionNames == null) {
                return null;
            }
            return new ArrayList(this.mDelegatedPermissionNames);
        }

        public final boolean isDelegatedPermission(String str) {
            List<String> list = this.mDelegatedPermissionNames;
            return list == null || list.contains(str);
        }
    }

    /* renamed from: com.android.server.pm.permission.PermissionManagerService$AttributionSourceRegistry */
    /* loaded from: classes2.dex */
    public static final class AttributionSourceRegistry {
        public final Context mContext;
        public final Object mLock = new Object();
        public final WeakHashMap<IBinder, AttributionSource> mAttributions = new WeakHashMap<>();

        public AttributionSourceRegistry(Context context) {
            this.mContext = context;
        }

        public void registerAttributionSource(AttributionSource attributionSource) {
            int callingUid = Binder.getCallingUid();
            if (attributionSource.getUid() != callingUid && this.mContext.checkPermission("android.permission.UPDATE_APP_OPS_STATS", -1, callingUid) != 0) {
                throw new SecurityException("Cannot register attribution source for uid:" + attributionSource.getUid() + " from uid:" + callingUid);
            }
            if (((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).getPackageUid(attributionSource.getPackageName(), 0L, UserHandle.getUserId(callingUid == 1000 ? attributionSource.getUid() : callingUid)) != attributionSource.getUid()) {
                throw new SecurityException("Cannot register attribution source for package:" + attributionSource.getPackageName() + " from uid:" + callingUid);
            }
            AttributionSource next = attributionSource.getNext();
            if (next != null && next.getNext() != null && !isRegisteredAttributionSource(next)) {
                throw new SecurityException("Cannot register forged attribution source:" + attributionSource);
            }
            synchronized (this.mLock) {
                this.mAttributions.put(attributionSource.getToken(), attributionSource);
            }
        }

        public boolean isRegisteredAttributionSource(AttributionSource attributionSource) {
            synchronized (this.mLock) {
                AttributionSource attributionSource2 = this.mAttributions.get(attributionSource.getToken());
                if (attributionSource2 != null) {
                    return attributionSource2.equals(attributionSource);
                }
                return false;
            }
        }
    }

    /* renamed from: com.android.server.pm.permission.PermissionManagerService$PermissionCheckerService */
    /* loaded from: classes2.dex */
    public static final class PermissionCheckerService extends IPermissionChecker.Stub {
        public final AppOpsManager mAppOpsManager;
        public final Context mContext;
        public final PermissionManagerServiceInternal mPermissionManagerServiceInternal = (PermissionManagerServiceInternal) LocalServices.getService(PermissionManagerServiceInternal.class);
        public static final ConcurrentHashMap<String, PermissionInfo> sPlatformPermissions = new ConcurrentHashMap<>();
        public static final AtomicInteger sAttributionChainIds = new AtomicInteger(0);

        public PermissionCheckerService(Context context) {
            this.mContext = context;
            this.mAppOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        }

        public int checkPermission(String str, AttributionSourceState attributionSourceState, String str2, boolean z, boolean z2, boolean z3, int i) {
            Objects.requireNonNull(str);
            Objects.requireNonNull(attributionSourceState);
            AttributionSource attributionSource = new AttributionSource(attributionSourceState);
            int checkPermission = checkPermission(this.mContext, this.mPermissionManagerServiceInternal, str, attributionSource, str2, z, z2, z3, i);
            if (z2 && checkPermission != 0 && checkPermission != 1) {
                if (i == -1) {
                    finishDataDelivery(AppOpsManager.permissionToOpCode(str), attributionSource.asState(), z3);
                } else {
                    finishDataDelivery(i, attributionSource.asState(), z3);
                }
            }
            return checkPermission;
        }

        public void finishDataDelivery(int i, AttributionSourceState attributionSourceState, boolean z) {
            finishDataDelivery(this.mContext, i, attributionSourceState, z);
        }

        /* JADX WARN: Code restructure failed: missing block: B:51:0x009f, code lost:
            if (r8 == null) goto L43;
         */
        /* JADX WARN: Code restructure failed: missing block: B:52:0x00a1, code lost:
            r9 = (com.android.server.p011pm.permission.PermissionManagerService.RegisteredAttribution) com.android.server.p011pm.permission.PermissionManagerService.sRunningAttributionSources.remove(r8.getToken());
         */
        /* JADX WARN: Code restructure failed: missing block: B:53:0x00af, code lost:
            if (r9 == null) goto L42;
         */
        /* JADX WARN: Code restructure failed: missing block: B:54:0x00b1, code lost:
            r9.unregister();
         */
        /* JADX WARN: Code restructure failed: missing block: B:55:0x00b4, code lost:
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:61:?, code lost:
            return;
         */
        /* JADX WARN: Code restructure failed: missing block: B:62:?, code lost:
            return;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public static void finishDataDelivery(Context context, int i, AttributionSourceState attributionSourceState, boolean z) {
            Objects.requireNonNull(attributionSourceState);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
            if (i == -1) {
                return;
            }
            AttributionSource attributionSource = null;
            AttributionSource attributionSource2 = new AttributionSource(attributionSourceState);
            while (true) {
                boolean z2 = false;
                boolean z3 = z || attributionSource != null;
                AttributionSource next = attributionSource2.getNext();
                if ((!z || attributionSource2.asState() != attributionSourceState) && next != null && !attributionSource2.isTrusted(context)) {
                    return;
                }
                boolean z4 = z && attributionSource2.asState() == attributionSourceState && next != null && next.getNext() == null;
                if (z4 || next == null) {
                    z2 = true;
                }
                AttributionSource attributionSource3 = !z4 ? attributionSource2 : next;
                if (z2) {
                    String resolvePackageName = resolvePackageName(context, attributionSource3);
                    if (resolvePackageName == null) {
                        return;
                    }
                    appOpsManager.finishOp(attributionSourceState.token, i, attributionSource3.getUid(), resolvePackageName, attributionSource3.getAttributionTag());
                } else {
                    AttributionSource resolveAttributionSource = resolveAttributionSource(context, attributionSource3);
                    if (resolveAttributionSource.getPackageName() == null) {
                        return;
                    }
                    appOpsManager.finishProxyOp(attributionSourceState.token, AppOpsManager.opToPublicName(i), resolveAttributionSource, z3);
                }
                RegisteredAttribution registeredAttribution = (RegisteredAttribution) PermissionManagerService.sRunningAttributionSources.remove(attributionSource2.getToken());
                if (registeredAttribution != null) {
                    registeredAttribution.unregister();
                }
                if (next == null || next.getNext() == null) {
                    break;
                }
                attributionSource = next;
                attributionSource2 = attributionSource;
            }
        }

        public int checkOp(int i, AttributionSourceState attributionSourceState, String str, boolean z, boolean z2) {
            int checkOp = checkOp(this.mContext, i, this.mPermissionManagerServiceInternal, new AttributionSource(attributionSourceState), str, z, z2);
            if (checkOp != 0 && z2) {
                finishDataDelivery(i, attributionSourceState, false);
            }
            return checkOp;
        }

        public static int checkPermission(Context context, PermissionManagerServiceInternal permissionManagerServiceInternal, String str, AttributionSource attributionSource, String str2, boolean z, boolean z2, boolean z3, int i) {
            ConcurrentHashMap<String, PermissionInfo> concurrentHashMap = sPlatformPermissions;
            PermissionInfo permissionInfo = concurrentHashMap.get(str);
            if (permissionInfo == null) {
                try {
                    permissionInfo = context.getPackageManager().getPermissionInfo(str, 0);
                    if (PackageManagerShellCommandDataLoader.PACKAGE.equals(permissionInfo.packageName) || HealthConnectManager.isHealthPermission(context, str)) {
                        concurrentHashMap.put(str, permissionInfo);
                    }
                } catch (PackageManager.NameNotFoundException unused) {
                    return 2;
                }
            }
            if (permissionInfo.isAppOp()) {
                return checkAppOpPermission(context, permissionManagerServiceInternal, str, attributionSource, str2, z, z3);
            }
            if (permissionInfo.isRuntime()) {
                return checkRuntimePermission(context, permissionManagerServiceInternal, str, attributionSource, str2, z, z2, z3, i);
            }
            if (!z3 && !checkPermission(context, permissionManagerServiceInternal, str, attributionSource.getUid(), attributionSource.getRenouncedPermissions())) {
                return 2;
            }
            if (attributionSource.getNext() != null) {
                return checkPermission(context, permissionManagerServiceInternal, str, attributionSource.getNext(), str2, z, z2, false, i);
            }
            return 0;
        }

        /* JADX WARN: Code restructure failed: missing block: B:55:0x00d5, code lost:
            return 0;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public static int checkAppOpPermission(Context context, PermissionManagerServiceInternal permissionManagerServiceInternal, String str, AttributionSource attributionSource, String str2, boolean z, boolean z2) {
            String str3;
            AttributionSource attributionSource2 = attributionSource;
            int permissionToOpCode = AppOpsManager.permissionToOpCode(str);
            int i = 2;
            if (permissionToOpCode < 0) {
                Slog.wtf(PermissionManagerService.LOG_TAG, "Appop permission " + str + " with no app op defined!");
                return 2;
            }
            AttributionSource attributionSource3 = null;
            AttributionSource attributionSource4 = attributionSource2;
            while (true) {
                boolean z3 = z2 || attributionSource3 != null;
                AttributionSource next = attributionSource4.getNext();
                if ((!z2 || !attributionSource4.equals(attributionSource2)) && next != null && !attributionSource4.isTrusted(context)) {
                    return i;
                }
                boolean z4 = z2 && attributionSource4.equals(attributionSource2) && next != null && next.getNext() == null;
                int performOpTransaction = performOpTransaction(context, attributionSource.getToken(), permissionToOpCode, attributionSource4, str2, z, false, z3, z4 || next == null, z4, -1, 0, 0, -1);
                if (performOpTransaction == 1 || performOpTransaction == 2) {
                    return 2;
                }
                if (performOpTransaction != 3) {
                    str3 = str;
                } else {
                    if (z3) {
                        str3 = str;
                    } else {
                        str3 = str;
                        if (!checkPermission(context, permissionManagerServiceInternal, str3, attributionSource.getUid(), attributionSource.getRenouncedPermissions())) {
                            return 2;
                        }
                    }
                    if (next != null && !checkPermission(context, permissionManagerServiceInternal, str3, next.getUid(), next.getRenouncedPermissions())) {
                        return 2;
                    }
                }
                if (next == null || next.getNext() == null) {
                    break;
                }
                attributionSource2 = attributionSource;
                i = 2;
                attributionSource3 = next;
                attributionSource4 = attributionSource3;
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        public static int checkRuntimePermission(Context context, PermissionManagerServiceInternal permissionManagerServiceInternal, String str, AttributionSource attributionSource, String str2, boolean z, boolean z2, boolean z3, int i) {
            AttributionSource attributionSource2;
            int i2;
            int i3;
            int i4;
            boolean z4;
            int i5;
            PermissionManagerServiceInternal permissionManagerServiceInternal2 = permissionManagerServiceInternal;
            String str3 = str;
            AttributionSource attributionSource3 = attributionSource;
            boolean z5 = z3;
            int permissionToOpCode = AppOpsManager.permissionToOpCode(str);
            int attributionChainId = getAttributionChainId(z2, attributionSource3);
            int i6 = 1;
            boolean z6 = attributionChainId != -1;
            boolean z7 = !z6 || checkPermission(context, permissionManagerServiceInternal2, "android.permission.UPDATE_APP_OPS_STATS", attributionSource.getUid(), attributionSource.getRenouncedPermissions());
            AttributionSource attributionSource4 = null;
            AttributionSource attributionSource5 = attributionSource3;
            while (true) {
                int i7 = (z5 || attributionSource4 != null) ? i6 : 0;
                AttributionSource next = attributionSource5.getNext();
                if ((!z5 || !attributionSource5.equals(attributionSource3)) && next != null && !attributionSource5.isTrusted(context)) {
                    return 2;
                }
                if (i7 == 0 && !checkPermission(context, permissionManagerServiceInternal2, str3, attributionSource5.getUid(), attributionSource5.getRenouncedPermissions())) {
                    return 2;
                }
                if (next != null && !checkPermission(context, permissionManagerServiceInternal2, str3, next.getUid(), next.getRenouncedPermissions())) {
                    return 2;
                }
                if (permissionToOpCode < 0) {
                    if (sPlatformPermissions.containsKey(str3) && !"android.permission.ACCESS_BACKGROUND_LOCATION".equals(str3) && !"android.permission.BODY_SENSORS_BACKGROUND".equals(str3) && !"android.permission.BODY_SENSORS_WRIST_TEMPERATURE_BACKGROUND".equals(str3)) {
                        Slog.wtf(PermissionManagerService.LOG_TAG, "Platform runtime permission " + str3 + " with no app op defined!");
                    }
                    if (next == null) {
                        return 0;
                    }
                    attributionSource2 = next;
                    i3 = i6;
                    i4 = attributionChainId;
                    i5 = permissionToOpCode;
                    z4 = z5;
                } else {
                    int i8 = (z5 && attributionSource5.equals(attributionSource3) && next != null && next.getNext() == null) ? i6 : 0;
                    int i9 = (i8 != 0 || next == null) ? i6 : 0;
                    int i10 = (z7 && (attributionSource5.isTrusted(context) || attributionSource5.equals(attributionSource3)) && (next == null || next.isTrusted(context))) ? i6 : 0;
                    if (i7 == 0 && z6) {
                        attributionSource2 = next;
                        i2 = resolveProxyAttributionFlags(attributionSource, attributionSource5, z3, z2, i9, i10);
                    } else {
                        attributionSource2 = next;
                        i2 = 0;
                    }
                    AttributionSource attributionSource6 = attributionSource5;
                    i3 = i6;
                    boolean z8 = i7;
                    i4 = attributionChainId;
                    int i11 = permissionToOpCode;
                    int performOpTransaction = performOpTransaction(context, attributionSource.getToken(), permissionToOpCode, attributionSource5, str2, z, z2, z8, i9, i8, i, i2, z6 ? resolveProxiedAttributionFlags(attributionSource, attributionSource2, z3, z2, i9, i10) : 0, i4);
                    if (performOpTransaction == i3) {
                        return i3;
                    }
                    if (performOpTransaction == 2) {
                        return 2;
                    }
                    if (z2) {
                        z4 = z3;
                        i5 = i11;
                        PermissionManagerService.sRunningAttributionSources.put(attributionSource6.getToken(), new RegisteredAttribution(context, i5, attributionSource6, z4));
                    } else {
                        z4 = z3;
                        i5 = i11;
                    }
                    if (attributionSource2 == null || attributionSource2.getNext() == null) {
                        break;
                    }
                }
                str3 = str;
                attributionSource3 = attributionSource;
                z5 = z4;
                permissionToOpCode = i5;
                i6 = i3;
                attributionChainId = i4;
                attributionSource4 = attributionSource2;
                attributionSource5 = attributionSource4;
                permissionManagerServiceInternal2 = permissionManagerServiceInternal;
            }
            return 0;
        }

        public static boolean checkPermission(Context context, PermissionManagerServiceInternal permissionManagerServiceInternal, String str, int i, Set<String> set) {
            boolean z = true;
            boolean z2 = context.checkPermission(str, -1, i) == 0;
            if (!z2 && Process.isIsolated(i) && (str.equals("android.permission.RECORD_AUDIO") || str.equals("android.permission.CAPTURE_AUDIO_HOTWORD") || str.equals("android.permission.CAPTURE_AUDIO_OUTPUT") || str.equals("android.permission.CAMERA"))) {
                PermissionManagerServiceInternal.HotwordDetectionServiceProvider hotwordDetectionServiceProvider = permissionManagerServiceInternal.getHotwordDetectionServiceProvider();
                if (hotwordDetectionServiceProvider == null || i != hotwordDetectionServiceProvider.getUid()) {
                    z = false;
                }
                z2 = z;
            }
            if (z2 && set.contains(str) && context.checkPermission("android.permission.RENOUNCE_PERMISSIONS", -1, i) == 0) {
                return false;
            }
            return z2;
        }

        public static int resolveProxyAttributionFlags(AttributionSource attributionSource, AttributionSource attributionSource2, boolean z, boolean z2, boolean z3, boolean z4) {
            return resolveAttributionFlags(attributionSource, attributionSource2, z, z2, z3, z4, true);
        }

        public static int resolveProxiedAttributionFlags(AttributionSource attributionSource, AttributionSource attributionSource2, boolean z, boolean z2, boolean z3, boolean z4) {
            return resolveAttributionFlags(attributionSource, attributionSource2, z, z2, z3, z4, false);
        }

        public static int resolveAttributionFlags(AttributionSource attributionSource, AttributionSource attributionSource2, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
            if (attributionSource2 == null || !z2) {
                return 0;
            }
            int i = z4 ? 8 : 0;
            if (z5) {
                if (z3) {
                    return i | 1;
                }
                if (!z && attributionSource2.equals(attributionSource)) {
                    return i | 1;
                }
            } else if (z3) {
                return i | 4;
            } else {
                if (z && attributionSource2.equals(attributionSource.getNext())) {
                    return i | 1;
                }
                if (attributionSource2.getNext() == null) {
                    return i | 4;
                }
            }
            if (z && attributionSource2.equals(attributionSource)) {
                return 0;
            }
            return i | 2;
        }

        /* JADX WARN: Code restructure failed: missing block: B:54:0x00db, code lost:
            return 0;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public static int checkOp(Context context, int i, PermissionManagerServiceInternal permissionManagerServiceInternal, AttributionSource attributionSource, String str, boolean z, boolean z2) {
            Context context2 = context;
            int i2 = 2;
            if (i < 0 || attributionSource.getPackageName() == null) {
                return 2;
            }
            int attributionChainId = getAttributionChainId(z2, attributionSource);
            boolean z3 = true;
            boolean z4 = attributionChainId != -1;
            boolean z5 = !z4 || checkPermission(context2, permissionManagerServiceInternal, "android.permission.UPDATE_APP_OPS_STATS", attributionSource.getUid(), attributionSource.getRenouncedPermissions());
            AttributionSource attributionSource2 = null;
            AttributionSource attributionSource3 = attributionSource;
            while (true) {
                boolean z6 = attributionSource2 != null ? z3 : false;
                AttributionSource next = attributionSource3.getNext();
                if (next != null && !attributionSource3.isTrusted(context2)) {
                    return i2;
                }
                boolean z7 = next == null ? z3 : false;
                boolean z8 = (z5 && (attributionSource3.isTrusted(context2) || attributionSource3.equals(attributionSource)) && (next == null || next.isTrusted(context2))) ? z3 : false;
                int i3 = attributionChainId;
                int i4 = i2;
                int performOpTransaction = performOpTransaction(context, attributionSource3.getToken(), i, attributionSource3, str, z, z2, z6, z7, false, -1, (z6 || !z4) ? 0 : resolveProxyAttributionFlags(attributionSource, attributionSource3, false, z2, z7, z8), z4 ? resolveProxiedAttributionFlags(attributionSource, next, false, z2, z7, z8) : 0, i3);
                if (performOpTransaction == 1) {
                    return 1;
                }
                if (performOpTransaction == i4) {
                    return i4;
                }
                if (next == null || next.getNext() == null) {
                    break;
                }
                z3 = true;
                i2 = i4;
                attributionChainId = i3;
                attributionSource2 = next;
                attributionSource3 = attributionSource2;
                context2 = context;
            }
        }

        public static int performOpTransaction(Context context, IBinder iBinder, int i, AttributionSource attributionSource, String str, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, int i2, int i3, int i4, int i5) {
            AppOpsManager appOpsManager;
            int i6;
            int i7;
            int noteProxyOpNoThrow;
            String str2;
            int i8;
            int i9;
            int i10;
            String str3;
            String str4;
            String str5;
            String str6;
            AppOpsManager appOpsManager2;
            int startProxyOpNoThrow;
            AppOpsManager appOpsManager3 = (AppOpsManager) context.getSystemService(AppOpsManager.class);
            AttributionSource next = !z5 ? attributionSource : attributionSource.getNext();
            if (!z) {
                String resolvePackageName = resolvePackageName(context, next);
                if (resolvePackageName == null) {
                    return 2;
                }
                int unsafeCheckOpRawNoThrow = appOpsManager3.unsafeCheckOpRawNoThrow(i, next.getUid(), resolvePackageName);
                AttributionSource next2 = next.getNext();
                if (z4 || unsafeCheckOpRawNoThrow != 0 || next2 == null) {
                    return unsafeCheckOpRawNoThrow;
                }
                String resolvePackageName2 = resolvePackageName(context, next2);
                if (resolvePackageName2 == null) {
                    return 2;
                }
                return appOpsManager3.unsafeCheckOpRawNoThrow(i, next2.getUid(), resolvePackageName2);
            } else if (z2) {
                AttributionSource resolveAttributionSource = resolveAttributionSource(context, next);
                if (resolveAttributionSource.getPackageName() == null) {
                    return 2;
                }
                if (i2 == -1 || i2 == i) {
                    str2 = XmlUtils.STRING_ARRAY_SEPARATOR;
                    i8 = i;
                    i9 = 0;
                } else {
                    int uid = resolveAttributionSource.getUid();
                    str2 = XmlUtils.STRING_ARRAY_SEPARATOR;
                    i9 = appOpsManager3.checkOpNoThrow(i, uid, resolveAttributionSource.getPackageName());
                    if (i9 == 2) {
                        return i9;
                    }
                    i8 = i2;
                }
                if (z4) {
                    try {
                        int uid2 = resolveAttributionSource.getUid();
                        String packageName = resolveAttributionSource.getPackageName();
                        String attributionTag = resolveAttributionSource.getAttributionTag();
                        i10 = i9;
                        str3 = "Datasource ";
                        str4 = " protecting data with platform defined runtime permission ";
                        str5 = " while not having ";
                        str6 = "android.permission.UPDATE_APP_OPS_STATS";
                        appOpsManager2 = appOpsManager3;
                        try {
                            startProxyOpNoThrow = appOpsManager3.startOpNoThrow(iBinder, i8, uid2, packageName, false, attributionTag, str, i3, i5);
                        } catch (SecurityException unused) {
                            Slog.w(PermissionManagerService.LOG_TAG, str3 + attributionSource + str4 + AppOpsManager.opToPermission(i) + str5 + str6);
                            startProxyOpNoThrow = appOpsManager2.startProxyOpNoThrow(iBinder, i2, attributionSource, str, z3, i3, i4, i5);
                            return Math.max(i10, startProxyOpNoThrow);
                        }
                    } catch (SecurityException unused2) {
                        i10 = i9;
                        str3 = "Datasource ";
                        str4 = " protecting data with platform defined runtime permission ";
                        str5 = " while not having ";
                        str6 = "android.permission.UPDATE_APP_OPS_STATS";
                        appOpsManager2 = appOpsManager3;
                    }
                } else {
                    i10 = i9;
                    int i11 = i8;
                    String str7 = str2;
                    try {
                        startProxyOpNoThrow = appOpsManager3.startProxyOpNoThrow(iBinder, i11, resolveAttributionSource, str, z3, i3, i4, i5);
                    } catch (SecurityException e) {
                        String str8 = "Security exception for op " + i11 + " with source " + attributionSource.getUid() + str7 + attributionSource.getPackageName() + ", " + attributionSource.getNextUid() + str7 + attributionSource.getNextPackageName();
                        if (attributionSource.getNext() != null) {
                            AttributionSource next3 = attributionSource.getNext();
                            str8 = str8 + ", " + next3.getNextPackageName() + str7 + next3.getNextUid();
                        }
                        throw new SecurityException(str8 + str7 + e.getMessage());
                    }
                }
                return Math.max(i10, startProxyOpNoThrow);
            } else {
                AttributionSource resolveAttributionSource2 = resolveAttributionSource(context, next);
                if (resolveAttributionSource2.getPackageName() == null) {
                    return 2;
                }
                if (i2 == -1 || i2 == i) {
                    appOpsManager = appOpsManager3;
                    i6 = i;
                    i7 = 0;
                } else {
                    appOpsManager = appOpsManager3;
                    i7 = appOpsManager.checkOpNoThrow(i, resolveAttributionSource2.getUid(), resolveAttributionSource2.getPackageName());
                    if (i7 == 2) {
                        return i7;
                    }
                    i6 = i2;
                }
                if (z4) {
                    try {
                        noteProxyOpNoThrow = appOpsManager.noteOpNoThrow(i6, resolveAttributionSource2.getUid(), resolveAttributionSource2.getPackageName(), resolveAttributionSource2.getAttributionTag(), str);
                    } catch (SecurityException unused3) {
                        Slog.w(PermissionManagerService.LOG_TAG, "Datasource " + attributionSource + " protecting data with platform defined runtime permission " + AppOpsManager.opToPermission(i) + " while not having android.permission.UPDATE_APP_OPS_STATS");
                        noteProxyOpNoThrow = appOpsManager.noteProxyOpNoThrow(i6, attributionSource, str, z3);
                    }
                } else {
                    try {
                        noteProxyOpNoThrow = appOpsManager.noteProxyOpNoThrow(i6, resolveAttributionSource2, str, z3);
                    } catch (SecurityException e2) {
                        String str9 = "Security exception for op " + i6 + " with source " + attributionSource.getUid() + XmlUtils.STRING_ARRAY_SEPARATOR + attributionSource.getPackageName() + ", " + attributionSource.getNextUid() + XmlUtils.STRING_ARRAY_SEPARATOR + attributionSource.getNextPackageName();
                        if (attributionSource.getNext() != null) {
                            AttributionSource next4 = attributionSource.getNext();
                            str9 = str9 + ", " + next4.getNextPackageName() + XmlUtils.STRING_ARRAY_SEPARATOR + next4.getNextUid();
                        }
                        throw new SecurityException(str9 + XmlUtils.STRING_ARRAY_SEPARATOR + e2.getMessage());
                    }
                }
                return Math.max(i7, noteProxyOpNoThrow);
            }
        }

        public static int getAttributionChainId(boolean z, AttributionSource attributionSource) {
            if (attributionSource == null || attributionSource.getNext() == null || !z) {
                return -1;
            }
            AtomicInteger atomicInteger = sAttributionChainIds;
            int incrementAndGet = atomicInteger.incrementAndGet();
            if (incrementAndGet < 0) {
                atomicInteger.set(0);
                return 0;
            }
            return incrementAndGet;
        }

        public static String resolvePackageName(Context context, AttributionSource attributionSource) {
            if (attributionSource.getPackageName() != null) {
                return attributionSource.getPackageName();
            }
            String[] packagesForUid = context.getPackageManager().getPackagesForUid(attributionSource.getUid());
            if (packagesForUid != null) {
                return packagesForUid[0];
            }
            return AppOpsManager.resolvePackageName(attributionSource.getUid(), attributionSource.getPackageName());
        }

        public static AttributionSource resolveAttributionSource(Context context, AttributionSource attributionSource) {
            return attributionSource.getPackageName() != null ? attributionSource : attributionSource.withPackageName(resolvePackageName(context, attributionSource));
        }
    }

    /* renamed from: com.android.server.pm.permission.PermissionManagerService$RegisteredAttribution */
    /* loaded from: classes2.dex */
    public static final class RegisteredAttribution {
        public final IBinder.DeathRecipient mDeathRecipient;
        public final AtomicBoolean mFinished = new AtomicBoolean(false);
        public final IBinder mToken;

        public RegisteredAttribution(final Context context, final int i, final AttributionSource attributionSource, final boolean z) {
            IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() { // from class: com.android.server.pm.permission.PermissionManagerService$RegisteredAttribution$$ExternalSyntheticLambda0
                @Override // android.os.IBinder.DeathRecipient
                public final void binderDied() {
                    PermissionManagerService.RegisteredAttribution.this.lambda$new$0(context, i, attributionSource, z);
                }
            };
            this.mDeathRecipient = deathRecipient;
            IBinder token = attributionSource.getToken();
            this.mToken = token;
            if (token != null) {
                try {
                    token.linkToDeath(deathRecipient, 0);
                } catch (RemoteException unused) {
                    this.mDeathRecipient.binderDied();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(Context context, int i, AttributionSource attributionSource, boolean z) {
            if (unregister()) {
                PermissionCheckerService.finishDataDelivery(context, i, attributionSource.asState(), z);
            }
        }

        public boolean unregister() {
            if (this.mFinished.compareAndSet(false, true)) {
                try {
                    IBinder iBinder = this.mToken;
                    if (iBinder != null) {
                        iBinder.unlinkToDeath(this.mDeathRecipient, 0);
                    }
                } catch (NoSuchElementException unused) {
                }
                return true;
            }
            return false;
        }
    }
}
