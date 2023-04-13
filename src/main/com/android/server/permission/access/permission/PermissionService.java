package com.android.server.permission.access.permission;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.permission.SplitPermissionInfoParcelable;
import android.metrics.LogMaker;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.p005os.IInstalld;
import android.permission.IOnPermissionsChangeListener;
import android.permission.PermissionControllerManager;
import android.permission.PermissionManager;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IntArray;
import android.util.Log;
import android.util.SparseBooleanArray;
import com.android.internal.compat.IPlatformCompat;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.Preconditions;
import com.android.server.FgThread;
import com.android.server.LocalManagerRegistry;
import com.android.server.LocalServices;
import com.android.server.PermissionThread;
import com.android.server.ServiceThread;
import com.android.server.SystemConfig;
import com.android.server.p011pm.PackageInstallerService;
import com.android.server.p011pm.PackageManagerLocal;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.p011pm.UserManagerService;
import com.android.server.p011pm.parsing.pkg.AndroidPackageUtils;
import com.android.server.p011pm.permission.LegacyPermission;
import com.android.server.p011pm.permission.LegacyPermissionSettings;
import com.android.server.p011pm.permission.LegacyPermissionState;
import com.android.server.p011pm.permission.PermissionManagerServiceInterface;
import com.android.server.p011pm.permission.PermissionManagerServiceInternal;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageState;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.permission.access.AccessCheckingService;
import com.android.server.permission.access.AccessState;
import com.android.server.permission.access.GetStateScope;
import com.android.server.permission.access.MutateStateScope;
import com.android.server.permission.access.SchemePolicy;
import com.android.server.permission.access.appop.UidAppOpPolicy;
import com.android.server.permission.access.collection.IntSet;
import com.android.server.permission.access.permission.UidPermissionPolicy;
import com.android.server.permission.access.util.IntExtensionsKt;
import com.android.server.permission.jarjar.kotlin.Unit;
import com.android.server.permission.jarjar.kotlin.collections.ArraysKt___ArraysJvmKt;
import com.android.server.permission.jarjar.kotlin.collections.ArraysKt___ArraysKt;
import com.android.server.permission.jarjar.kotlin.collections.CollectionsKt__CollectionsKt;
import com.android.server.permission.jarjar.kotlin.collections.CollectionsKt__MutableCollectionsKt;
import com.android.server.permission.jarjar.kotlin.collections.CollectionsKt___CollectionsKt;
import com.android.server.permission.jarjar.kotlin.collections.SetsKt__SetsKt;
import com.android.server.permission.jarjar.kotlin.jdk7.AutoCloseableKt;
import com.android.server.permission.jarjar.kotlin.jvm.internal.DefaultConstructorMarker;
import com.android.server.permission.jarjar.kotlin.jvm.internal.Intrinsics;
import com.android.server.permission.jarjar.kotlin.jvm.internal.Ref$IntRef;
import com.android.server.permission.jarjar.kotlin.jvm.internal.Ref$ObjectRef;
import com.android.server.policy.SoftRestrictedPermissionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import libcore.util.EmptyArray;
/* compiled from: PermissionService.kt */
/* loaded from: classes2.dex */
public final class PermissionService implements PermissionManagerServiceInterface {
    public static final long BACKUP_TIMEOUT_MILLIS;
    public static final ArrayMap<String, String> FULLER_PERMISSIONS;
    public static final ArraySet<String> NOTIFICATIONS_PERMISSIONS;
    public final Context context;
    public Handler handler;
    public HandlerThread handlerThread;
    public final SparseBooleanArray isDelayedPermissionBackupFinished;
    public MetricsLogger metricsLogger;
    public final ArraySet<String> mountedStorageVolumes;
    public OnPermissionFlagsChangedListener onPermissionFlagsChangedListener;
    public OnPermissionsChangeListeners onPermissionsChangeListeners;
    public PackageManagerInternal packageManagerInternal;
    public PackageManagerLocal packageManagerLocal;
    public PermissionControllerManager permissionControllerManager;
    public IPlatformCompat platformCompat;
    public final UidPermissionPolicy policy;
    public final AccessCheckingService service;
    public SystemConfig systemConfig;
    public UserManagerInternal userManagerInternal;
    public UserManagerService userManagerService;
    public static final Companion Companion = new Companion(null);
    public static final String LOG_TAG = PermissionService.class.getSimpleName();
    public static final long BACKGROUND_RATIONALE_CHANGE_ID = 147316723;

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public void addOnRuntimePermissionStateChangedListener(PermissionManagerServiceInternal.OnRuntimePermissionStateChangedListener onRuntimePermissionStateChangedListener) {
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public void onPackageRemoved(AndroidPackage androidPackage) {
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public void readLegacyPermissionStateTEMP() {
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public void resetRuntimePermissions(AndroidPackage androidPackage, int i) {
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public void resetRuntimePermissionsForUser(int i) {
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public void writeLegacyPermissionStateTEMP() {
    }

    public PermissionService(AccessCheckingService accessCheckingService) {
        this.service = accessCheckingService;
        SchemePolicy m45x318e5b8b = accessCheckingService.m45x318e5b8b("uid", "permission");
        Intrinsics.checkNotNull(m45x318e5b8b, "null cannot be cast to non-null type com.android.server.permission.access.permission.UidPermissionPolicy");
        this.policy = (UidPermissionPolicy) m45x318e5b8b;
        this.context = accessCheckingService.getContext();
        this.mountedStorageVolumes = new ArraySet<>();
        this.isDelayedPermissionBackupFinished = new SparseBooleanArray();
    }

    public final void initialize() {
        this.metricsLogger = new MetricsLogger();
        this.packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        this.packageManagerLocal = (PackageManagerLocal) LocalManagerRegistry.getManagerOrThrow(PackageManagerLocal.class);
        this.platformCompat = IPlatformCompat.Stub.asInterface(ServiceManager.getService("platform_compat"));
        this.systemConfig = SystemConfig.getInstance();
        this.userManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
        this.userManagerService = UserManagerService.getInstance();
        ServiceThread serviceThread = new ServiceThread(LOG_TAG, 10, true);
        serviceThread.start();
        this.handlerThread = serviceThread;
        HandlerThread handlerThread = this.handlerThread;
        if (handlerThread == null) {
            Intrinsics.throwUninitializedPropertyAccessException("handlerThread");
            handlerThread = null;
        }
        this.handler = new Handler(handlerThread.getLooper());
        this.onPermissionsChangeListeners = new OnPermissionsChangeListeners(FgThread.get().getLooper());
        OnPermissionFlagsChangedListener onPermissionFlagsChangedListener = new OnPermissionFlagsChangedListener();
        this.onPermissionFlagsChangedListener = onPermissionFlagsChangedListener;
        this.policy.addOnPermissionFlagsChangedListener(onPermissionFlagsChangedListener);
    }

    public final List<LegacyPermission> toLegacyPermissions(ArrayMap<String, Permission> arrayMap) {
        ArrayList arrayList = new ArrayList();
        int size = arrayMap.size();
        for (int i = 0; i < size; i++) {
            arrayMap.keyAt(i);
            Permission valueAt = arrayMap.valueAt(i);
            arrayList.add(new LegacyPermission(valueAt.getPermissionInfo(), valueAt.getType(), 0, EmptyArray.INT));
        }
        return arrayList;
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public List<PermissionGroupInfo> getAllPermissionGroups(int i) {
        PackageManagerLocal packageManagerLocal = this.packageManagerLocal;
        if (packageManagerLocal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("packageManagerLocal");
            packageManagerLocal = null;
        }
        PackageManagerLocal.UnfilteredSnapshot withUnfilteredSnapshot = packageManagerLocal.withUnfilteredSnapshot();
        try {
            int callingUid = Binder.getCallingUid();
            if (!isUidInstantApp(withUnfilteredSnapshot, callingUid)) {
                AccessState accessState = this.service.state;
                if (accessState == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("state");
                    accessState = null;
                }
                ArrayMap<String, PermissionGroupInfo> permissionGroups = this.policy.getPermissionGroups(new GetStateScope(accessState));
                ArrayList arrayList = new ArrayList();
                int size = permissionGroups.size();
                for (int i2 = 0; i2 < size; i2++) {
                    String keyAt = permissionGroups.keyAt(i2);
                    PermissionGroupInfo valueAt = permissionGroups.valueAt(i2);
                    String str = keyAt;
                    PermissionGroupInfo generatePermissionGroupInfo = isPackageVisibleToUid(withUnfilteredSnapshot, valueAt.packageName, callingUid) ? generatePermissionGroupInfo(valueAt, i) : null;
                    if (generatePermissionGroupInfo != null) {
                        arrayList.add(generatePermissionGroupInfo);
                    }
                }
                AutoCloseableKt.closeFinally(withUnfilteredSnapshot, null);
                return arrayList;
            }
            List<PermissionGroupInfo> emptyList = CollectionsKt__CollectionsKt.emptyList();
            AutoCloseableKt.closeFinally(withUnfilteredSnapshot, null);
            return emptyList;
        } finally {
        }
    }

    /* JADX WARN: Type inference failed for: r7v3, types: [android.content.pm.PermissionGroupInfo, T] */
    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public PermissionGroupInfo getPermissionGroupInfo(String str, int i) {
        Ref$ObjectRef ref$ObjectRef = new Ref$ObjectRef();
        PackageManagerLocal packageManagerLocal = this.packageManagerLocal;
        if (packageManagerLocal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("packageManagerLocal");
            packageManagerLocal = null;
        }
        PackageManagerLocal.UnfilteredSnapshot withUnfilteredSnapshot = packageManagerLocal.withUnfilteredSnapshot();
        try {
            int callingUid = Binder.getCallingUid();
            if (!isUidInstantApp(withUnfilteredSnapshot, callingUid)) {
                AccessState accessState = this.service.state;
                if (accessState == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("state");
                    accessState = null;
                }
                PermissionGroupInfo permissionGroupInfo = this.policy.getPermissionGroups(new GetStateScope(accessState)).get(str);
                if (permissionGroupInfo != 0) {
                    ref$ObjectRef.element = permissionGroupInfo;
                    PermissionGroupInfo permissionGroupInfo2 = permissionGroupInfo;
                    if (!isPackageVisibleToUid(withUnfilteredSnapshot, permissionGroupInfo.packageName, callingUid)) {
                        AutoCloseableKt.closeFinally(withUnfilteredSnapshot, null);
                        return null;
                    }
                    Unit unit = Unit.INSTANCE;
                    AutoCloseableKt.closeFinally(withUnfilteredSnapshot, null);
                    return generatePermissionGroupInfo((PermissionGroupInfo) ref$ObjectRef.element, i);
                }
                AutoCloseableKt.closeFinally(withUnfilteredSnapshot, null);
                return null;
            }
            AutoCloseableKt.closeFinally(withUnfilteredSnapshot, null);
            return null;
        } finally {
        }
    }

    public final PermissionGroupInfo generatePermissionGroupInfo(PermissionGroupInfo permissionGroupInfo, int i) {
        PermissionGroupInfo permissionGroupInfo2 = new PermissionGroupInfo(permissionGroupInfo);
        if (!IntExtensionsKt.hasBits(i, 128)) {
            permissionGroupInfo2.metaData = null;
        }
        return permissionGroupInfo2;
    }

    /* JADX WARN: Type inference failed for: r7v3, types: [T, com.android.server.permission.access.permission.Permission] */
    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public PermissionInfo getPermissionInfo(String str, int i, String str2) {
        Ref$ObjectRef ref$ObjectRef = new Ref$ObjectRef();
        PackageManagerLocal packageManagerLocal = this.packageManagerLocal;
        if (packageManagerLocal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("packageManagerLocal");
            packageManagerLocal = null;
        }
        PackageManagerLocal.UnfilteredSnapshot withUnfilteredSnapshot = packageManagerLocal.withUnfilteredSnapshot();
        try {
            int callingUid = Binder.getCallingUid();
            if (!isUidInstantApp(withUnfilteredSnapshot, callingUid)) {
                AccessState accessState = this.service.state;
                if (accessState == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("state");
                    accessState = null;
                }
                Permission permission = this.policy.getPermissions(new GetStateScope(accessState)).get(str);
                if (permission != 0) {
                    ref$ObjectRef.element = permission;
                    if (!isPackageVisibleToUid(withUnfilteredSnapshot, permission.getPermissionInfo().packageName, callingUid)) {
                        AutoCloseableKt.closeFinally(withUnfilteredSnapshot, null);
                        return null;
                    }
                    PackageState packageState = getPackageState(withUnfilteredSnapshot, str2);
                    AndroidPackage androidPackage = packageState != null ? packageState.getAndroidPackage() : null;
                    boolean isRootOrSystemOrShell = isRootOrSystemOrShell(callingUid);
                    int i2 = FrameworkStatsLog.WIFI_BYTES_TRANSFER;
                    if (!isRootOrSystemOrShell && androidPackage != null) {
                        i2 = androidPackage.getTargetSdkVersion();
                    }
                    Unit unit = Unit.INSTANCE;
                    AutoCloseableKt.closeFinally(withUnfilteredSnapshot, null);
                    return generatePermissionInfo((Permission) ref$ObjectRef.element, i, i2);
                }
                AutoCloseableKt.closeFinally(withUnfilteredSnapshot, null);
                return null;
            }
            AutoCloseableKt.closeFinally(withUnfilteredSnapshot, null);
            return null;
        } finally {
        }
    }

    public static /* synthetic */ PermissionInfo generatePermissionInfo$default(PermissionService permissionService, Permission permission, int i, int i2, int i3, Object obj) {
        if ((i3 & 2) != 0) {
            i2 = FrameworkStatsLog.WIFI_BYTES_TRANSFER;
        }
        return permissionService.generatePermissionInfo(permission, i, i2);
    }

    public final PermissionInfo generatePermissionInfo(Permission permission, int i, int i2) {
        int protection;
        PermissionInfo permissionInfo = new PermissionInfo(permission.getPermissionInfo());
        permissionInfo.flags |= 1073741824;
        if (!IntExtensionsKt.hasBits(i, 128)) {
            permissionInfo.metaData = null;
        }
        if (i2 < 26 && (protection = permissionInfo.getProtection()) != 2) {
            permissionInfo.protectionLevel = protection;
        }
        return permissionInfo;
    }

    /* JADX WARN: Type inference failed for: r11v0, types: [T, android.util.ArrayMap] */
    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public List<PermissionInfo> queryPermissionsByGroup(String str, int i) {
        PackageManagerLocal packageManagerLocal = this.packageManagerLocal;
        if (packageManagerLocal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("packageManagerLocal");
            packageManagerLocal = null;
        }
        PackageManagerLocal.UnfilteredSnapshot withUnfilteredSnapshot = packageManagerLocal.withUnfilteredSnapshot();
        try {
            int callingUid = Binder.getCallingUid();
            if (isUidInstantApp(withUnfilteredSnapshot, callingUid)) {
                AutoCloseableKt.closeFinally(withUnfilteredSnapshot, null);
                return null;
            }
            Ref$ObjectRef ref$ObjectRef = new Ref$ObjectRef();
            AccessState accessState = this.service.state;
            if (accessState == null) {
                Intrinsics.throwUninitializedPropertyAccessException("state");
                accessState = null;
            }
            GetStateScope getStateScope = new GetStateScope(accessState);
            if (str != null) {
                PermissionGroupInfo permissionGroupInfo = this.policy.getPermissionGroups(getStateScope).get(str);
                if (permissionGroupInfo == null) {
                    AutoCloseableKt.closeFinally(withUnfilteredSnapshot, null);
                    return null;
                } else if (!isPackageVisibleToUid(withUnfilteredSnapshot, permissionGroupInfo.packageName, callingUid)) {
                    AutoCloseableKt.closeFinally(withUnfilteredSnapshot, null);
                    return null;
                }
            }
            ?? permissions = this.policy.getPermissions(getStateScope);
            ref$ObjectRef.element = permissions;
            ArrayMap arrayMap = (ArrayMap) permissions;
            ArrayList arrayList = new ArrayList();
            int size = permissions.size();
            for (int i2 = 0; i2 < size; i2++) {
                Object keyAt = permissions.keyAt(i2);
                Permission permission = (Permission) permissions.valueAt(i2);
                String str2 = (String) keyAt;
                PermissionInfo generatePermissionInfo$default = (Intrinsics.areEqual(permission.getPermissionInfo().group, str) && isPackageVisibleToUid(withUnfilteredSnapshot, permission.getPermissionInfo().packageName, callingUid)) ? generatePermissionInfo$default(this, permission, i, 0, 2, null) : null;
                if (generatePermissionInfo$default != null) {
                    arrayList.add(generatePermissionInfo$default);
                }
            }
            AutoCloseableKt.closeFinally(withUnfilteredSnapshot, null);
            return arrayList;
        } finally {
        }
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public List<PermissionInfo> getAllPermissionsWithProtection(int i) {
        AccessState accessState = this.service.state;
        if (accessState == null) {
            Intrinsics.throwUninitializedPropertyAccessException("state");
            accessState = null;
        }
        ArrayMap<String, Permission> permissions = this.policy.getPermissions(new GetStateScope(accessState));
        ArrayList arrayList = new ArrayList();
        int size = permissions.size();
        for (int i2 = 0; i2 < size; i2++) {
            permissions.keyAt(i2);
            Permission valueAt = permissions.valueAt(i2);
            PermissionInfo generatePermissionInfo$default = valueAt.getPermissionInfo().getProtection() == i ? generatePermissionInfo$default(this, valueAt, 0, 0, 2, null) : null;
            if (generatePermissionInfo$default != null) {
                arrayList.add(generatePermissionInfo$default);
            }
        }
        return arrayList;
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public List<PermissionInfo> getAllPermissionsWithProtectionFlags(int i) {
        AccessState accessState = this.service.state;
        if (accessState == null) {
            Intrinsics.throwUninitializedPropertyAccessException("state");
            accessState = null;
        }
        ArrayMap<String, Permission> permissions = this.policy.getPermissions(new GetStateScope(accessState));
        ArrayList arrayList = new ArrayList();
        int size = permissions.size();
        for (int i2 = 0; i2 < size; i2++) {
            permissions.keyAt(i2);
            Permission valueAt = permissions.valueAt(i2);
            PermissionInfo generatePermissionInfo$default = IntExtensionsKt.hasBits(valueAt.getPermissionInfo().getProtectionFlags(), i) ? generatePermissionInfo$default(this, valueAt, 0, 0, 2, null) : null;
            if (generatePermissionInfo$default != null) {
                arrayList.add(generatePermissionInfo$default);
            }
        }
        return arrayList;
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public int[] getPermissionGids(String str, int i) {
        AccessState accessState = this.service.state;
        if (accessState == null) {
            Intrinsics.throwUninitializedPropertyAccessException("state");
            accessState = null;
        }
        Permission permission = this.policy.getPermissions(new GetStateScope(accessState)).get(str);
        if (permission == null) {
            return EmptyArray.INT;
        }
        return permission.getGidsForUser(i);
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public Set<String> getInstalledPermissions(String str) {
        if (str != null) {
            AccessState accessState = this.service.state;
            if (accessState == null) {
                Intrinsics.throwUninitializedPropertyAccessException("state");
                accessState = null;
            }
            ArrayMap<String, Permission> permissions = this.policy.getPermissions(new GetStateScope(accessState));
            ArraySet arraySet = new ArraySet();
            int size = permissions.size();
            for (int i = 0; i < size; i++) {
                permissions.keyAt(i);
                Permission valueAt = permissions.valueAt(i);
                String str2 = Intrinsics.areEqual(valueAt.getPermissionInfo().packageName, str) ? valueAt.getPermissionInfo().name : null;
                if (str2 != null) {
                    arraySet.add(str2);
                }
            }
            return arraySet;
        }
        throw new IllegalArgumentException("packageName cannot be null".toString());
    }

    /* JADX WARN: Type inference failed for: r4v6, types: [T, com.android.server.permission.access.permission.Permission] */
    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public boolean addPermission(PermissionInfo permissionInfo, boolean z) {
        String str = permissionInfo.name;
        if (str == null) {
            throw new IllegalArgumentException("permissionName cannot be null".toString());
        }
        int callingUid = Binder.getCallingUid();
        PackageManagerLocal packageManagerLocal = this.packageManagerLocal;
        AccessState accessState = null;
        if (packageManagerLocal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("packageManagerLocal");
            packageManagerLocal = null;
        }
        PackageManagerLocal.UnfilteredSnapshot withUnfilteredSnapshot = packageManagerLocal.withUnfilteredSnapshot();
        try {
            boolean isUidInstantApp = isUidInstantApp(withUnfilteredSnapshot, callingUid);
            AutoCloseableKt.closeFinally(withUnfilteredSnapshot, null);
            if (isUidInstantApp) {
                throw new SecurityException("Instant apps cannot add permissions");
            }
            if (permissionInfo.labelRes == 0 && permissionInfo.nonLocalizedLabel == null) {
                throw new SecurityException("Label must be specified in permission");
            }
            Ref$ObjectRef ref$ObjectRef = new Ref$ObjectRef();
            AccessCheckingService accessCheckingService = this.service;
            synchronized (accessCheckingService.stateLock) {
                AccessState accessState2 = accessCheckingService.state;
                if (accessState2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("state");
                } else {
                    accessState = accessState2;
                }
                AccessState copy = accessState.copy();
                MutateStateScope mutateStateScope = new MutateStateScope(accessState, copy);
                Permission andEnforcePermissionTree = getAndEnforcePermissionTree(mutateStateScope, str);
                enforcePermissionTreeSize(mutateStateScope, permissionInfo, andEnforcePermissionTree);
                Permission permission = this.policy.getPermissions(mutateStateScope).get(str);
                ref$ObjectRef.element = permission;
                if (permission != 0) {
                    if (!(permission.getType() == 2)) {
                        throw new SecurityException("Not allowed to modify non-dynamic permission " + str);
                    }
                }
                permissionInfo.packageName = andEnforcePermissionTree.getPermissionInfo().packageName;
                permissionInfo.protectionLevel = PermissionInfo.fixProtectionLevel(permissionInfo.protectionLevel);
                this.policy.addPermission(mutateStateScope, new Permission(permissionInfo, true, 2, andEnforcePermissionTree.getAppId(), null, false, 48, null), !z);
                accessCheckingService.persistence.write(copy);
                accessCheckingService.state = copy;
                accessCheckingService.policy.onStateMutated(new GetStateScope(copy));
                Unit unit = Unit.INSTANCE;
            }
            return ref$ObjectRef.element == 0;
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                AutoCloseableKt.closeFinally(withUnfilteredSnapshot, th);
                throw th2;
            }
        }
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public void removePermission(String str) {
        int callingUid = Binder.getCallingUid();
        PackageManagerLocal packageManagerLocal = this.packageManagerLocal;
        AccessState accessState = null;
        if (packageManagerLocal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("packageManagerLocal");
            packageManagerLocal = null;
        }
        PackageManagerLocal.UnfilteredSnapshot withUnfilteredSnapshot = packageManagerLocal.withUnfilteredSnapshot();
        try {
            boolean isUidInstantApp = isUidInstantApp(withUnfilteredSnapshot, callingUid);
            AutoCloseableKt.closeFinally(withUnfilteredSnapshot, null);
            if (isUidInstantApp) {
                throw new SecurityException("Instant applications don't have access to this method");
            }
            AccessCheckingService accessCheckingService = this.service;
            synchronized (accessCheckingService.stateLock) {
                AccessState accessState2 = accessCheckingService.state;
                if (accessState2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("state");
                } else {
                    accessState = accessState2;
                }
                AccessState copy = accessState.copy();
                MutateStateScope mutateStateScope = new MutateStateScope(accessState, copy);
                getAndEnforcePermissionTree(mutateStateScope, str);
                Permission permission = this.policy.getPermissions(mutateStateScope).get(str);
                if (permission != null) {
                    if (permission.getType() == 2) {
                        this.policy.removePermission(mutateStateScope, permission);
                    } else {
                        throw new SecurityException("Not allowed to modify non-dynamic permission " + str);
                    }
                }
                accessCheckingService.persistence.write(copy);
                accessCheckingService.state = copy;
                accessCheckingService.policy.onStateMutated(new GetStateScope(copy));
                Unit unit = Unit.INSTANCE;
            }
        } finally {
        }
    }

    public final Permission getAndEnforcePermissionTree(GetStateScope getStateScope, String str) {
        int callingUid = Binder.getCallingUid();
        Permission findPermissionTree = this.policy.findPermissionTree(getStateScope, str);
        if (findPermissionTree == null || findPermissionTree.getAppId() != UserHandle.getAppId(callingUid)) {
            throw new SecurityException("Calling UID " + callingUid + " is not allowed to add to or remove from the permission tree");
        }
        return findPermissionTree;
    }

    public final void enforcePermissionTreeSize(GetStateScope getStateScope, PermissionInfo permissionInfo, Permission permission) {
        if (permission.getAppId() != 1000 && calculatePermissionTreeFootprint(getStateScope, permission) + permissionInfo.calculateFootprint() > 32768) {
            throw new SecurityException("Permission tree size cap exceeded");
        }
    }

    public final int calculatePermissionTreeFootprint(GetStateScope getStateScope, Permission permission) {
        ArrayMap<String, Permission> permissions = this.policy.getPermissions(getStateScope);
        int size = permissions.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            Permission valueAt = permissions.valueAt(i2);
            if (permission.getAppId() == valueAt.getAppId()) {
                i += valueAt.getPermissionInfo().name.length() + valueAt.getPermissionInfo().calculateFootprint();
            }
        }
        return i;
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public int checkUidPermission(int i, String str) {
        int userId = UserHandle.getUserId(i);
        UserManagerInternal userManagerInternal = this.userManagerInternal;
        AccessState accessState = null;
        if (userManagerInternal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("userManagerInternal");
            userManagerInternal = null;
        }
        if (userManagerInternal.exists(userId)) {
            PackageManagerInternal packageManagerInternal = this.packageManagerInternal;
            if (packageManagerInternal == null) {
                Intrinsics.throwUninitializedPropertyAccessException("packageManagerInternal");
                packageManagerInternal = null;
            }
            AndroidPackage androidPackage = packageManagerInternal.getPackage(i);
            if (androidPackage == null) {
                return isSystemUidPermissionGranted(i, str) ? 0 : -1;
            }
            PackageManagerInternal packageManagerInternal2 = this.packageManagerInternal;
            if (packageManagerInternal2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("packageManagerInternal");
                packageManagerInternal2 = null;
            }
            PackageStateInternal packageStateInternal = packageManagerInternal2.getPackageStateInternal(androidPackage.getPackageName());
            if (packageStateInternal != null) {
                AccessState accessState2 = this.service.state;
                if (accessState2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("state");
                } else {
                    accessState = accessState2;
                }
                return isPermissionGranted(new GetStateScope(accessState), packageStateInternal, userId, str) ? 0 : -1;
            }
            Log.e(LOG_TAG, "checkUidPermission: PackageState not found for AndroidPackage " + androidPackage);
            return -1;
        }
        return -1;
    }

    public final boolean isSystemUidPermissionGranted(int i, String str) {
        SystemConfig systemConfig = this.systemConfig;
        if (systemConfig == null) {
            Intrinsics.throwUninitializedPropertyAccessException("systemConfig");
            systemConfig = null;
        }
        ArraySet<String> arraySet = systemConfig.getSystemPermissions().get(i);
        if (arraySet == null) {
            return false;
        }
        if (arraySet.contains(str)) {
            return true;
        }
        String str2 = FULLER_PERMISSIONS.get(str);
        return str2 != null && arraySet.contains(str2);
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public int checkPermission(String str, String str2, int i) {
        UserManagerInternal userManagerInternal = this.userManagerInternal;
        AccessState accessState = null;
        if (userManagerInternal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("userManagerInternal");
            userManagerInternal = null;
        }
        if (userManagerInternal.exists(i)) {
            PackageManagerLocal packageManagerLocal = this.packageManagerLocal;
            if (packageManagerLocal == null) {
                Intrinsics.throwUninitializedPropertyAccessException("packageManagerLocal");
                packageManagerLocal = null;
            }
            PackageManagerLocal.FilteredSnapshot withFilteredSnapshot = withFilteredSnapshot(packageManagerLocal, Binder.getCallingUid(), i);
            try {
                PackageState packageState = withFilteredSnapshot.getPackageState(str);
                AutoCloseableKt.closeFinally(withFilteredSnapshot, null);
                if (packageState == null) {
                    return -1;
                }
                AccessState accessState2 = this.service.state;
                if (accessState2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("state");
                } else {
                    accessState = accessState2;
                }
                return isPermissionGranted(new GetStateScope(accessState), packageState, i, str2) ? 0 : -1;
            } catch (Throwable th) {
                try {
                    throw th;
                } catch (Throwable th2) {
                    AutoCloseableKt.closeFinally(withFilteredSnapshot, th);
                    throw th2;
                }
            }
        }
        return -1;
    }

    public final boolean isPermissionGranted(GetStateScope getStateScope, PackageState packageState, int i, String str) {
        int appId = packageState.getAppId();
        boolean isInstantApp = packageState.getUserStateOrDefault(i).isInstantApp();
        if (isSinglePermissionGranted(getStateScope, appId, i, isInstantApp, str)) {
            return true;
        }
        String str2 = FULLER_PERMISSIONS.get(str);
        return str2 != null && isSinglePermissionGranted(getStateScope, appId, i, isInstantApp, str2);
    }

    public final boolean isSinglePermissionGranted(GetStateScope getStateScope, int i, int i2, boolean z, String str) {
        if (PermissionFlags.INSTANCE.isPermissionGranted(this.policy.getPermissionFlags(getStateScope, i, i2, str))) {
            if (z) {
                Permission permission = this.policy.getPermissions(getStateScope).get(str);
                return permission != null && IntExtensionsKt.hasBits(permission.getPermissionInfo().getProtectionFlags(), IInstalld.FLAG_USE_QUOTA);
            }
            return true;
        }
        return false;
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public Set<String> getGrantedPermissions(String str, int i) {
        if (str == null) {
            throw new IllegalArgumentException("packageName cannot be null".toString());
        }
        Preconditions.checkArgumentNonnegative(i, "userId");
        PackageManagerLocal packageManagerLocal = this.packageManagerLocal;
        if (packageManagerLocal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("packageManagerLocal");
            packageManagerLocal = null;
        }
        PackageManagerLocal.UnfilteredSnapshot withUnfilteredSnapshot = packageManagerLocal.withUnfilteredSnapshot();
        try {
            PackageState packageState = getPackageState(withUnfilteredSnapshot, str);
            AutoCloseableKt.closeFinally(withUnfilteredSnapshot, null);
            if (packageState != null) {
                AccessState accessState = this.service.state;
                if (accessState == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("state");
                    accessState = null;
                }
                GetStateScope getStateScope = new GetStateScope(accessState);
                ArrayMap<String, Integer> uidPermissionFlags = this.policy.getUidPermissionFlags(getStateScope, packageState.getAppId(), i);
                if (uidPermissionFlags != null) {
                    ArraySet arraySet = new ArraySet();
                    int size = uidPermissionFlags.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        String keyAt = uidPermissionFlags.keyAt(i2);
                        uidPermissionFlags.valueAt(i2).intValue();
                        String str2 = keyAt;
                        if (!isPermissionGranted(getStateScope, packageState, i, str2)) {
                            str2 = null;
                        }
                        if (str2 != null) {
                            arraySet.add(str2);
                        }
                    }
                    return arraySet;
                }
                return SetsKt__SetsKt.emptySet();
            }
            Log.w(LOG_TAG, "getGrantedPermissions: Unknown package " + str);
            return SetsKt__SetsKt.emptySet();
        } finally {
        }
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public int[] getGidsForUid(int i) {
        Permission permission;
        int appId = UserHandle.getAppId(i);
        int userId = UserHandle.getUserId(i);
        SystemConfig systemConfig = this.systemConfig;
        AccessState accessState = null;
        if (systemConfig == null) {
            Intrinsics.throwUninitializedPropertyAccessException("systemConfig");
            systemConfig = null;
        }
        int[] globalGids = systemConfig.getGlobalGids();
        AccessState accessState2 = this.service.state;
        if (accessState2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("state");
        } else {
            accessState = accessState2;
        }
        GetStateScope getStateScope = new GetStateScope(accessState);
        ArrayMap<String, Integer> uidPermissionFlags = this.policy.getUidPermissionFlags(getStateScope, appId, userId);
        if (uidPermissionFlags == null) {
            int[] copyOf = Arrays.copyOf(globalGids, globalGids.length);
            Intrinsics.checkNotNullExpressionValue(copyOf, "copyOf(this, size)");
            return copyOf;
        }
        IntArray wrap = IntArray.wrap(globalGids);
        int size = uidPermissionFlags.size();
        for (int i2 = 0; i2 < size; i2++) {
            String keyAt = uidPermissionFlags.keyAt(i2);
            if (PermissionFlags.INSTANCE.isPermissionGranted(uidPermissionFlags.valueAt(i2).intValue()) && (permission = this.policy.getPermissions(getStateScope).get(keyAt)) != null) {
                int[] gidsForUser = permission.getGidsForUser(userId);
                if (!(gidsForUser.length == 0)) {
                    wrap.addAll(gidsForUser);
                }
            }
        }
        return wrap.toArray();
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public void grantRuntimePermission(String str, String str2, int i) {
        setRuntimePermissionGranted$default(this, str, i, str2, true, false, null, 48, null);
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public void revokeRuntimePermission(String str, String str2, int i, String str3) {
        setRuntimePermissionGranted$default(this, str, i, str2, false, false, str3, 16, null);
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public void revokePostNotificationPermissionWithoutKillForTest(String str, int i) {
        setRuntimePermissionGranted$default(this, str, i, "android.permission.POST_NOTIFICATIONS", false, true, null, 32, null);
    }

    public static /* synthetic */ void setRuntimePermissionGranted$default(PermissionService permissionService, String str, int i, String str2, boolean z, boolean z2, String str3, int i2, Object obj) {
        if ((i2 & 16) != 0) {
            z2 = false;
        }
        boolean z3 = z2;
        if ((i2 & 32) != 0) {
            str3 = null;
        }
        permissionService.setRuntimePermissionGranted(str, i, str2, z, z3, str3);
    }

    /* JADX WARN: Removed duplicated region for block: B:43:0x00da  */
    /* JADX WARN: Removed duplicated region for block: B:78:0x00e2 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r13v0, types: [com.android.server.pm.pkg.PackageState, T] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void setRuntimePermissionGranted(String str, int i, String str2, boolean z, boolean z2, String str3) {
        boolean z3;
        AccessCheckingService accessCheckingService;
        String str4 = z ? "grantRuntimePermission" : "revokeRuntimePermission";
        int callingUid = Binder.getCallingUid();
        UserManagerInternal userManagerInternal = this.userManagerInternal;
        OnPermissionFlagsChangedListener onPermissionFlagsChangedListener = null;
        if (userManagerInternal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("userManagerInternal");
            userManagerInternal = null;
        }
        if (!userManagerInternal.exists(i)) {
            Log.w(LOG_TAG, str4 + ": Unknown user " + i);
            return;
        }
        enforceCallingOrSelfCrossUserPermission(i, true, true, str4);
        this.context.enforceCallingOrSelfPermission(z ? "android.permission.GRANT_RUNTIME_PERMISSIONS" : "android.permission.REVOKE_RUNTIME_PERMISSIONS", str4);
        Ref$ObjectRef ref$ObjectRef = new Ref$ObjectRef();
        PackageManagerInternal packageManagerInternal = this.packageManagerInternal;
        if (packageManagerInternal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("packageManagerInternal");
            packageManagerInternal = null;
        }
        String str5 = (String) ArraysKt___ArraysKt.first(packageManagerInternal.getKnownPackageNames(7, 0));
        PackageManagerLocal packageManagerLocal = this.packageManagerLocal;
        if (packageManagerLocal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("packageManagerLocal");
            packageManagerLocal = null;
        }
        PackageManagerLocal.UnfilteredSnapshot withUnfilteredSnapshot = packageManagerLocal.withUnfilteredSnapshot();
        try {
            PackageManagerLocal.FilteredSnapshot filtered = filtered(withUnfilteredSnapshot, callingUid, i);
            ?? packageState = filtered.getPackageState(str);
            AutoCloseableKt.closeFinally(filtered, null);
            ref$ObjectRef.element = packageState;
            PackageState packageState2 = getPackageState(withUnfilteredSnapshot, str5);
            Unit unit = Unit.INSTANCE;
            AutoCloseableKt.closeFinally(withUnfilteredSnapshot, null);
            PackageState packageState3 = (PackageState) ref$ObjectRef.element;
            if ((packageState3 != null ? packageState3.getAndroidPackage() : null) == null) {
                Log.w(LOG_TAG, str4 + ": Unknown package " + str);
                return;
            }
            if (!isRootOrSystem(callingUid)) {
                int appId = UserHandle.getAppId(callingUid);
                Intrinsics.checkNotNull(packageState2);
                if (appId != packageState2.getAppId()) {
                    z3 = false;
                    boolean z4 = this.context.checkCallingOrSelfPermission("android.permission.ADJUST_RUNTIME_PERMISSIONS_POLICY") == 0;
                    accessCheckingService = this.service;
                    synchronized (accessCheckingService.stateLock) {
                        AccessState accessState = accessCheckingService.state;
                        if (accessState == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("state");
                            accessState = null;
                        }
                        AccessState copy = accessState.copy();
                        MutateStateScope mutateStateScope = new MutateStateScope(accessState, copy);
                        OnPermissionFlagsChangedListener onPermissionFlagsChangedListener2 = this.onPermissionFlagsChangedListener;
                        if (onPermissionFlagsChangedListener2 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("onPermissionFlagsChangedListener");
                        } else {
                            onPermissionFlagsChangedListener = onPermissionFlagsChangedListener2;
                        }
                        if (z2) {
                            onPermissionFlagsChangedListener.skipKillRuntimePermissionRevokedUids(mutateStateScope);
                        }
                        if (str3 != null) {
                            onPermissionFlagsChangedListener.addKillRuntimePermissionRevokedUidsReason(mutateStateScope, str3);
                        }
                        setRuntimePermissionGranted(mutateStateScope, (PackageState) ref$ObjectRef.element, i, str2, z, z3, z4, true, str4);
                        accessCheckingService.persistence.write(copy);
                        accessCheckingService.state = copy;
                        accessCheckingService.policy.onStateMutated(new GetStateScope(copy));
                    }
                    return;
                }
            }
            z3 = true;
            if (this.context.checkCallingOrSelfPermission("android.permission.ADJUST_RUNTIME_PERMISSIONS_POLICY") == 0) {
            }
            accessCheckingService = this.service;
            synchronized (accessCheckingService.stateLock) {
            }
        } finally {
        }
    }

    public final void setRequestedPermissionStates(PackageState packageState, int i, ArrayMap<String, Integer> arrayMap) {
        Permission permission;
        int i2;
        int i3;
        AccessCheckingService accessCheckingService = this.service;
        synchronized (accessCheckingService.stateLock) {
            AccessState accessState = accessCheckingService.state;
            if (accessState == null) {
                Intrinsics.throwUninitializedPropertyAccessException("state");
                accessState = null;
            }
            AccessState copy = accessState.copy();
            MutateStateScope mutateStateScope = new MutateStateScope(accessState, copy);
            int size = arrayMap.size();
            int i4 = 0;
            while (i4 < size) {
                String keyAt = arrayMap.keyAt(i4);
                int intValue = arrayMap.valueAt(i4).intValue();
                String str = keyAt;
                if (intValue != 1 && intValue != 2) {
                    Log.w(LOG_TAG, "setRequestedPermissionStates: Unknown permission state " + intValue + " for permission " + str);
                } else {
                    AndroidPackage androidPackage = packageState.getAndroidPackage();
                    Intrinsics.checkNotNull(androidPackage);
                    if (androidPackage.getRequestedPermissions().contains(str) && (permission = this.policy.getPermissions(mutateStateScope).get(str)) != null) {
                        if (!IntExtensionsKt.hasBits(permission.getPermissionInfo().getProtectionFlags(), 32)) {
                            if (!(permission.getPermissionInfo().getProtection() == 1)) {
                                if (IntExtensionsKt.hasBits(permission.getPermissionInfo().getProtectionFlags(), 64) && PackageInstallerService.INSTALLER_CHANGEABLE_APP_OP_PERMISSIONS.contains(str)) {
                                    setAppOpPermissionGranted(mutateStateScope, packageState, i, str, intValue == 1);
                                }
                            }
                        }
                        if (intValue == 1) {
                            i2 = i4;
                            i3 = size;
                            setRuntimePermissionGranted(mutateStateScope, packageState, i, str, true, false, false, false, "setRequestedPermissionStates");
                            i4 = i2 + 1;
                            size = i3;
                        }
                    }
                }
                i2 = i4;
                i3 = size;
                i4 = i2 + 1;
                size = i3;
            }
            accessCheckingService.persistence.write(copy);
            accessCheckingService.state = copy;
            accessCheckingService.policy.onStateMutated(new GetStateScope(copy));
            Unit unit = Unit.INSTANCE;
        }
    }

    public final void setRuntimePermissionGranted(MutateStateScope mutateStateScope, PackageState packageState, int i, String str, boolean z, boolean z2, boolean z3, boolean z4, String str2) {
        Permission permission = this.policy.getPermissions(mutateStateScope).get(str);
        if (permission == null) {
            if (z4) {
                throw new IllegalArgumentException("Unknown permission " + str);
            }
            return;
        }
        AndroidPackage androidPackage = packageState.getAndroidPackage();
        Intrinsics.checkNotNull(androidPackage);
        String packageName = packageState.getPackageName();
        if (!IntExtensionsKt.hasBits(permission.getPermissionInfo().getProtectionFlags(), 32)) {
            if (!IntExtensionsKt.hasBits(permission.getPermissionInfo().getProtectionFlags(), 67108864)) {
                if (!(permission.getPermissionInfo().getProtection() == 1)) {
                    if (z4) {
                        throw new SecurityException("Permission " + str + " requested by package " + packageName + " is not a changeable permission type");
                    }
                    return;
                } else if (androidPackage.getTargetSdkVersion() < 23) {
                    return;
                } else {
                    if (z && packageState.getUserStateOrDefault(i).isInstantApp() && !IntExtensionsKt.hasBits(permission.getPermissionInfo().getProtectionFlags(), IInstalld.FLAG_USE_QUOTA)) {
                        if (z4) {
                            throw new SecurityException("Cannot grant non-instant permission " + str + " to package " + packageName);
                        }
                        return;
                    }
                }
            } else if (!z2) {
                if (z4) {
                    throw new SecurityException("Permission " + str + " is managed by role");
                }
                return;
            }
        }
        int appId = packageState.getAppId();
        int permissionFlags = this.policy.getPermissionFlags(mutateStateScope, appId, i, str);
        if (!androidPackage.getRequestedPermissions().contains(str) && permissionFlags == 0) {
            if (z4) {
                throw new SecurityException("Permission " + str + " isn't requested by package " + packageName);
            }
        } else if (IntExtensionsKt.hasBits(permissionFlags, 256)) {
            if (z4) {
                String str3 = LOG_TAG;
                Log.e(str3, str2 + ": Cannot change system fixed permission " + str + " for package " + packageName);
            }
        } else if (IntExtensionsKt.hasBits(permissionFlags, 128) && !z3) {
            if (z4) {
                String str4 = LOG_TAG;
                Log.e(str4, str2 + ": Cannot change policy fixed permission " + str + " for package " + packageName);
            }
        } else if (z && IntExtensionsKt.hasBits(permissionFlags, 262144)) {
            if (z4) {
                String str5 = LOG_TAG;
                Log.e(str5, str2 + ": Cannot grant hard-restricted non-exempt permission " + str + " to package " + packageName);
            }
        } else if (z && IntExtensionsKt.hasBits(permissionFlags, 524288) && !SoftRestrictedPermissionPolicy.forPermission(this.context, AndroidPackageUtils.generateAppInfoWithoutState(androidPackage), androidPackage, UserHandle.of(i), str).mayGrantPermission()) {
            if (z4) {
                String str6 = LOG_TAG;
                Log.e(str6, str2 + ": Cannot grant soft-restricted non-exempt permission " + str + " to package " + packageName);
            }
        } else {
            int updateRuntimePermissionGranted = PermissionFlags.INSTANCE.updateRuntimePermissionGranted(permissionFlags, z);
            if (permissionFlags == updateRuntimePermissionGranted) {
                return;
            }
            this.policy.setPermissionFlags(mutateStateScope, appId, i, str, updateRuntimePermissionGranted);
            if (permission.getPermissionInfo().getProtection() == 1) {
                LogMaker logMaker = new LogMaker(z ? 1243 : 1245);
                logMaker.setPackageName(packageName);
                logMaker.addTaggedData(1241, str);
                MetricsLogger metricsLogger = this.metricsLogger;
                if (metricsLogger == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("metricsLogger");
                    metricsLogger = null;
                }
                metricsLogger.write(logMaker);
            }
        }
    }

    public final void setAppOpPermissionGranted(MutateStateScope mutateStateScope, PackageState packageState, int i, String str, boolean z) {
        SchemePolicy m45x318e5b8b = this.service.m45x318e5b8b("uid", "app-op");
        Intrinsics.checkNotNull(m45x318e5b8b, "null cannot be cast to non-null type com.android.server.permission.access.appop.UidAppOpPolicy");
        ((UidAppOpPolicy) m45x318e5b8b).setAppOpMode(mutateStateScope, packageState.getAppId(), i, AppOpsManager.permissionToOp(str), z ? 0 : 2);
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public int getPermissionFlags(String str, String str2, int i) {
        UserManagerInternal userManagerInternal = this.userManagerInternal;
        AccessState accessState = null;
        if (userManagerInternal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("userManagerInternal");
            userManagerInternal = null;
        }
        if (!userManagerInternal.exists(i)) {
            Log.w(LOG_TAG, "getPermissionFlags: Unknown user " + i);
            return 0;
        }
        enforceCallingOrSelfCrossUserPermission(i, true, false, "getPermissionFlags");
        enforceCallingOrSelfAnyPermission("getPermissionFlags", "android.permission.GRANT_RUNTIME_PERMISSIONS", "android.permission.REVOKE_RUNTIME_PERMISSIONS", "android.permission.GET_RUNTIME_PERMISSIONS");
        PackageManagerLocal packageManagerLocal = this.packageManagerLocal;
        if (packageManagerLocal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("packageManagerLocal");
            packageManagerLocal = null;
        }
        PackageManagerLocal.FilteredSnapshot withFilteredSnapshot = packageManagerLocal.withFilteredSnapshot();
        try {
            PackageState packageState = withFilteredSnapshot.getPackageState(str);
            AutoCloseableKt.closeFinally(withFilteredSnapshot, null);
            if (packageState != null) {
                AccessState accessState2 = this.service.state;
                if (accessState2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("state");
                } else {
                    accessState = accessState2;
                }
                GetStateScope getStateScope = new GetStateScope(accessState);
                if (this.policy.getPermissions(getStateScope).get(str2) == null) {
                    Log.w(LOG_TAG, "getPermissionFlags: Unknown permission " + str2);
                    return 0;
                }
                return PermissionFlags.INSTANCE.toApiFlags(this.policy.getPermissionFlags(getStateScope, packageState.getAppId(), i, str2));
            }
            Log.w(LOG_TAG, "getPermissionFlags: Unknown package " + str);
            return 0;
        } finally {
        }
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public boolean isPermissionRevokedByPolicy(String str, String str2, int i) {
        UserManagerInternal userManagerInternal = this.userManagerInternal;
        AccessState accessState = null;
        if (userManagerInternal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("userManagerInternal");
            userManagerInternal = null;
        }
        if (!userManagerInternal.exists(i)) {
            Log.w(LOG_TAG, "isPermissionRevokedByPolicy: Unknown user " + i);
            return false;
        }
        enforceCallingOrSelfCrossUserPermission(i, true, false, "isPermissionRevokedByPolicy");
        PackageManagerLocal packageManagerLocal = this.packageManagerLocal;
        if (packageManagerLocal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("packageManagerLocal");
            packageManagerLocal = null;
        }
        PackageManagerLocal.FilteredSnapshot withFilteredSnapshot = withFilteredSnapshot(packageManagerLocal, Binder.getCallingUid(), i);
        try {
            PackageState packageState = withFilteredSnapshot.getPackageState(str);
            AutoCloseableKt.closeFinally(withFilteredSnapshot, null);
            if (packageState == null) {
                return false;
            }
            AccessState accessState2 = this.service.state;
            if (accessState2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("state");
            } else {
                accessState = accessState2;
            }
            GetStateScope getStateScope = new GetStateScope(accessState);
            if (isPermissionGranted(getStateScope, packageState, i, str2)) {
                return false;
            }
            return IntExtensionsKt.hasBits(this.policy.getPermissionFlags(getStateScope, packageState.getAppId(), i, str2), 128);
        } finally {
        }
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public boolean isPermissionsReviewRequired(String str, int i) {
        if (str == null) {
            throw new IllegalArgumentException("packageName cannot be null".toString());
        }
        PackageManagerLocal packageManagerLocal = this.packageManagerLocal;
        AccessState accessState = null;
        if (packageManagerLocal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("packageManagerLocal");
            packageManagerLocal = null;
        }
        PackageManagerLocal.UnfilteredSnapshot withUnfilteredSnapshot = packageManagerLocal.withUnfilteredSnapshot();
        try {
            PackageState packageState = getPackageState(withUnfilteredSnapshot, str);
            AutoCloseableKt.closeFinally(withUnfilteredSnapshot, null);
            if (packageState == null) {
                return false;
            }
            AccessState accessState2 = this.service.state;
            if (accessState2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("state");
            } else {
                accessState = accessState2;
            }
            ArrayMap<String, Integer> uidPermissionFlags = this.policy.getUidPermissionFlags(new GetStateScope(accessState), packageState.getAppId(), i);
            if (uidPermissionFlags == null) {
                return false;
            }
            int size = uidPermissionFlags.size();
            for (int i2 = 0; i2 < size; i2++) {
                uidPermissionFlags.keyAt(i2);
                if (IntExtensionsKt.hasBits(uidPermissionFlags.valueAt(i2).intValue(), 5120)) {
                    return true;
                }
            }
            return false;
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                AutoCloseableKt.closeFinally(withUnfilteredSnapshot, th);
                throw th2;
            }
        }
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public boolean shouldShowRequestPermissionRationale(String str, String str2, int i) {
        int appId;
        UserManagerInternal userManagerInternal = this.userManagerInternal;
        IPlatformCompat iPlatformCompat = null;
        if (userManagerInternal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("userManagerInternal");
            userManagerInternal = null;
        }
        boolean z = false;
        if (!userManagerInternal.exists(i)) {
            Log.w(LOG_TAG, "shouldShowRequestPermissionRationale: Unknown user " + i);
            return false;
        }
        enforceCallingOrSelfCrossUserPermission(i, true, false, "shouldShowRequestPermissionRationale");
        int callingUid = Binder.getCallingUid();
        PackageManagerLocal packageManagerLocal = this.packageManagerLocal;
        if (packageManagerLocal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("packageManagerLocal");
            packageManagerLocal = null;
        }
        PackageManagerLocal.FilteredSnapshot withFilteredSnapshot = withFilteredSnapshot(packageManagerLocal, callingUid, i);
        try {
            PackageState packageState = withFilteredSnapshot.getPackageState(str);
            AutoCloseableKt.closeFinally(withFilteredSnapshot, null);
            if (packageState != null && UserHandle.getAppId(callingUid) == (appId = packageState.getAppId())) {
                Ref$IntRef ref$IntRef = new Ref$IntRef();
                AccessState accessState = this.service.state;
                if (accessState == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("state");
                    accessState = null;
                }
                GetStateScope getStateScope = new GetStateScope(accessState);
                if (isPermissionGranted(getStateScope, packageState, i, str2)) {
                    return false;
                }
                int intValue = Integer.valueOf(this.policy.getPermissionFlags(getStateScope, appId, i, str2)).intValue();
                ref$IntRef.element = intValue;
                if (IntExtensionsKt.hasAnyBit(intValue, 262592)) {
                    return false;
                }
                if (Intrinsics.areEqual(str2, "android.permission.ACCESS_BACKGROUND_LOCATION")) {
                    long clearCallingIdentity = Binder.clearCallingIdentity();
                    try {
                        try {
                            IPlatformCompat iPlatformCompat2 = this.platformCompat;
                            if (iPlatformCompat2 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("platformCompat");
                            } else {
                                iPlatformCompat = iPlatformCompat2;
                            }
                            z = iPlatformCompat.isChangeEnabledByPackageName(BACKGROUND_RATIONALE_CHANGE_ID, str, i);
                        } catch (RemoteException e) {
                            Log.e(LOG_TAG, "shouldShowRequestPermissionRationale: Unable to check if compatibility change is enabled", e);
                        }
                        if (z) {
                            return true;
                        }
                    } finally {
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                    }
                }
                return IntExtensionsKt.hasBits(ref$IntRef.element, 32);
            }
            return false;
        } finally {
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:76:0x010b A[EXC_TOP_SPLITTER, SYNTHETIC] */
    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void updatePermissionFlags(String str, String str2, int i, int i2, boolean z, int i3) {
        String[] sharedUserPackagesForPackage;
        boolean z2;
        AccessCheckingService accessCheckingService;
        int callingUid = Binder.getCallingUid();
        UserManagerInternal userManagerInternal = this.userManagerInternal;
        AccessState accessState = null;
        if (userManagerInternal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("userManagerInternal");
            userManagerInternal = null;
        }
        if (!userManagerInternal.exists(i3)) {
            Log.w(LOG_TAG, "updatePermissionFlags: Unknown user " + i3);
            return;
        }
        enforceCallingOrSelfCrossUserPermission(i3, true, true, "updatePermissionFlags");
        enforceCallingOrSelfAnyPermission("updatePermissionFlags", "android.permission.GRANT_RUNTIME_PERMISSIONS", "android.permission.REVOKE_RUNTIME_PERMISSIONS");
        if (!isRootOrSystem(callingUid) && IntExtensionsKt.hasBits(i, 4)) {
            if (z) {
                this.context.enforceCallingOrSelfPermission("android.permission.ADJUST_RUNTIME_PERMISSIONS_POLICY", "Need android.permission.ADJUST_RUNTIME_PERMISSIONS_POLICY to change policy flags");
            } else {
                PackageManagerInternal packageManagerInternal = this.packageManagerInternal;
                if (packageManagerInternal == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("packageManagerInternal");
                    packageManagerInternal = null;
                }
                if (!(packageManagerInternal.getUidTargetSdkVersion(callingUid) < 29)) {
                    throw new IllegalArgumentException("android.permission.ADJUST_RUNTIME_PERMISSIONS_POLICY needs to be checked for packages targeting 29 or later when changing policy flags".toString());
                }
            }
        }
        PackageManagerInternal packageManagerInternal2 = this.packageManagerInternal;
        if (packageManagerInternal2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("packageManagerInternal");
            packageManagerInternal2 = null;
        }
        PackageStateInternal packageStateInternal = packageManagerInternal2.getPackageStateInternal(str);
        AndroidPackage androidPackage = packageStateInternal != null ? packageStateInternal.getAndroidPackage() : null;
        if (androidPackage != null) {
            PackageManagerInternal packageManagerInternal3 = this.packageManagerInternal;
            if (packageManagerInternal3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("packageManagerInternal");
                packageManagerInternal3 = null;
            }
            if (!packageManagerInternal3.filterAppAccess(str, callingUid, i3, false)) {
                if (!androidPackage.getRequestedPermissions().contains(str2)) {
                    PackageManagerInternal packageManagerInternal4 = this.packageManagerInternal;
                    if (packageManagerInternal4 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("packageManagerInternal");
                        packageManagerInternal4 = null;
                    }
                    for (String str3 : packageManagerInternal4.getSharedUserPackagesForPackage(str, i3)) {
                        PackageManagerInternal packageManagerInternal5 = this.packageManagerInternal;
                        if (packageManagerInternal5 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("packageManagerInternal");
                            packageManagerInternal5 = null;
                        }
                        AndroidPackage androidPackage2 = packageManagerInternal5.getPackage(str3);
                        if (!(androidPackage2 != null && androidPackage2.getRequestedPermissions().contains(str2))) {
                        }
                    }
                    z2 = false;
                    int appId = packageStateInternal.getAppId();
                    accessCheckingService = this.service;
                    synchronized (accessCheckingService.stateLock) {
                        AccessState accessState2 = accessCheckingService.state;
                        if (accessState2 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("state");
                        } else {
                            accessState = accessState2;
                        }
                        AccessState copy = accessState.copy();
                        updatePermissionFlags(new MutateStateScope(accessState, copy), appId, i3, str2, i, i2, true, z2, "updatePermissionFlags", str);
                        accessCheckingService.persistence.write(copy);
                        accessCheckingService.state = copy;
                        accessCheckingService.policy.onStateMutated(new GetStateScope(copy));
                        Unit unit = Unit.INSTANCE;
                    }
                    return;
                }
                z2 = true;
                int appId2 = packageStateInternal.getAppId();
                accessCheckingService = this.service;
                synchronized (accessCheckingService.stateLock) {
                }
            }
        }
        Log.w(LOG_TAG, "updatePermissionFlags: Unknown package " + str);
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public void updatePermissionFlagsForAllApps(int i, int i2, int i3) {
        Binder.getCallingUid();
        UserManagerInternal userManagerInternal = this.userManagerInternal;
        AccessState accessState = null;
        if (userManagerInternal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("userManagerInternal");
            userManagerInternal = null;
        }
        if (!userManagerInternal.exists(i3)) {
            Log.w(LOG_TAG, "updatePermissionFlagsForAllApps: Unknown user " + i3);
            return;
        }
        enforceCallingOrSelfCrossUserPermission(i3, true, true, "updatePermissionFlagsForAllApps");
        enforceCallingOrSelfAnyPermission("updatePermissionFlagsForAllApps", "android.permission.GRANT_RUNTIME_PERMISSIONS", "android.permission.REVOKE_RUNTIME_PERMISSIONS");
        PackageManagerLocal packageManagerLocal = this.packageManagerLocal;
        if (packageManagerLocal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("packageManagerLocal");
            packageManagerLocal = null;
        }
        PackageManagerLocal.UnfilteredSnapshot withUnfilteredSnapshot = packageManagerLocal.withUnfilteredSnapshot();
        try {
            Map<String, PackageState> packageStates = withUnfilteredSnapshot.getPackageStates();
            AutoCloseableKt.closeFinally(withUnfilteredSnapshot, null);
            AccessCheckingService accessCheckingService = this.service;
            synchronized (accessCheckingService.stateLock) {
                AccessState accessState2 = accessCheckingService.state;
                if (accessState2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("state");
                } else {
                    accessState = accessState2;
                }
                AccessState copy = accessState.copy();
                MutateStateScope mutateStateScope = new MutateStateScope(accessState, copy);
                for (Map.Entry<String, PackageState> entry : packageStates.entrySet()) {
                    String key = entry.getKey();
                    PackageState value = entry.getValue();
                    AndroidPackage androidPackage = value.getAndroidPackage();
                    if (androidPackage != null) {
                        for (String str : androidPackage.getRequestedPermissions()) {
                            updatePermissionFlags(mutateStateScope, value.getAppId(), i3, str, i, i2, false, true, "updatePermissionFlagsForAllApps", key);
                            mutateStateScope = mutateStateScope;
                        }
                    }
                    mutateStateScope = mutateStateScope;
                }
                accessCheckingService.persistence.write(copy);
                accessCheckingService.state = copy;
                accessCheckingService.policy.onStateMutated(new GetStateScope(copy));
                Unit unit = Unit.INSTANCE;
            }
        } finally {
        }
    }

    public final void updatePermissionFlags(MutateStateScope mutateStateScope, int i, int i2, String str, int i3, int i4, boolean z, boolean z2, String str2, String str3) {
        int i5;
        int i6;
        int callingUid = Binder.getCallingUid();
        if (isRootOrSystem(callingUid)) {
            i5 = i3;
            i6 = i4;
        } else {
            int i7 = ((isShell(callingUid) || NOTIFICATIONS_PERMISSIONS.contains(str)) ? 0 : 64) | 48 | IInstalld.FLAG_USE_QUOTA | IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES | IInstalld.FLAG_FORCE | 16384;
            i5 = IntExtensionsKt.andInv(i3, i7);
            i6 = IntExtensionsKt.andInv(i4, i7);
        }
        Permission permission = this.policy.getPermissions(mutateStateScope).get(str);
        if (permission == null) {
            if (z) {
                throw new IllegalArgumentException("Unknown permission " + str);
            }
            return;
        }
        int permissionFlags = this.policy.getPermissionFlags(mutateStateScope, i, i2, str);
        if (!z2 && permissionFlags == 0) {
            String str4 = LOG_TAG;
            Log.w(str4, str2 + ": Permission " + str + " isn't requested by package " + str3);
            return;
        }
        this.policy.setPermissionFlags(mutateStateScope, i, i2, str, PermissionFlags.INSTANCE.updateFlags(permission, permissionFlags, i5, i6));
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public ArrayList<String> getAllowlistedRestrictedPermissions(String str, int i, int i2) {
        AndroidPackage androidPackage;
        if (str == null) {
            throw new IllegalArgumentException("packageName cannot be null".toString());
        }
        Preconditions.checkFlagsArgument(i, 7);
        Preconditions.checkArgumentNonnegative(i2, "userId cannot be null");
        UserManagerInternal userManagerInternal = this.userManagerInternal;
        PackageManagerInternal packageManagerInternal = null;
        if (userManagerInternal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("userManagerInternal");
            userManagerInternal = null;
        }
        if (!userManagerInternal.exists(i2)) {
            Log.w(LOG_TAG, "AllowlistedRestrictedPermission api: Unknown user " + i2);
            return null;
        }
        enforceCallingOrSelfCrossUserPermission(i2, false, false, "getAllowlistedRestrictedPermissions");
        int callingUid = Binder.getCallingUid();
        PackageManagerLocal packageManagerLocal = this.packageManagerLocal;
        if (packageManagerLocal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("packageManagerLocal");
            packageManagerLocal = null;
        }
        PackageManagerLocal.FilteredSnapshot withFilteredSnapshot = withFilteredSnapshot(packageManagerLocal, callingUid, i2);
        try {
            PackageState packageState = withFilteredSnapshot.getPackageState(str);
            AutoCloseableKt.closeFinally(withFilteredSnapshot, null);
            if (packageState == null || (androidPackage = packageState.getAndroidPackage()) == null) {
                return null;
            }
            boolean z = this.context.checkCallingOrSelfPermission("android.permission.WHITELIST_RESTRICTED_PERMISSIONS") == 0;
            if (IntExtensionsKt.hasBits(i, 1) && !z) {
                throw new SecurityException("Querying system allowlist requires android.permission.WHITELIST_RESTRICTED_PERMISSIONS");
            }
            PackageManagerInternal packageManagerInternal2 = this.packageManagerInternal;
            if (packageManagerInternal2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("packageManagerInternal");
            } else {
                packageManagerInternal = packageManagerInternal2;
            }
            boolean isCallerInstallerOfRecord = packageManagerInternal.isCallerInstallerOfRecord(androidPackage, callingUid);
            if (IntExtensionsKt.hasAnyBit(i, 6) && !z && !isCallerInstallerOfRecord) {
                throw new SecurityException("Querying upgrade or installer allowlist requires being installer on record or android.permission.WHITELIST_RESTRICTED_PERMISSIONS");
            }
            return getAllowlistedRestrictedPermissionsUnchecked(packageState.getAppId(), i, i2);
        } finally {
        }
    }

    public final ArrayList<String> getAllowlistedRestrictedPermissionsUnchecked(int i, int i2, int i3) {
        AccessState accessState = this.service.state;
        if (accessState == null) {
            Intrinsics.throwUninitializedPropertyAccessException("state");
            accessState = null;
        }
        ArrayMap<String, Integer> uidPermissionFlags = this.policy.getUidPermissionFlags(new GetStateScope(accessState), i, i3);
        if (uidPermissionFlags == null) {
            return null;
        }
        int i4 = IntExtensionsKt.hasBits(i2, 1) ? 65536 : 0;
        if (IntExtensionsKt.hasBits(i2, 4)) {
            i4 |= IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES;
        }
        if (IntExtensionsKt.hasBits(i2, 2)) {
            i4 |= 32768;
        }
        ArrayList<String> arrayList = new ArrayList<>();
        int size = uidPermissionFlags.size();
        for (int i5 = 0; i5 < size; i5++) {
            String keyAt = uidPermissionFlags.keyAt(i5);
            if (!IntExtensionsKt.hasAnyBit(uidPermissionFlags.valueAt(i5).intValue(), i4)) {
                keyAt = null;
            }
            if (keyAt != null) {
                arrayList.add(keyAt);
            }
        }
        return arrayList;
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public boolean addAllowlistedRestrictedPermission(String str, String str2, int i, int i2) {
        if (str2 == null) {
            throw new IllegalArgumentException("permissionName cannot be null".toString());
        }
        if (enforceRestrictedPermission(str2)) {
            ArrayList<String> allowlistedRestrictedPermissions = getAllowlistedRestrictedPermissions(str, i, i2);
            if (allowlistedRestrictedPermissions == null) {
                allowlistedRestrictedPermissions = new ArrayList<>(1);
            }
            ArrayList<String> arrayList = allowlistedRestrictedPermissions;
            if (arrayList.contains(str2)) {
                return false;
            }
            arrayList.add(str2);
            return setAllowlistedRestrictedPermissions(str, arrayList, i, i2, true);
        }
        return false;
    }

    public final void addAllowlistedRestrictedPermissionsUnchecked(AndroidPackage androidPackage, int i, List<String> list, int i2) {
        List<String> list2;
        ArrayList<String> allowlistedRestrictedPermissionsUnchecked = getAllowlistedRestrictedPermissionsUnchecked(i, 2, i2);
        if (allowlistedRestrictedPermissionsUnchecked != null) {
            ArraySet arraySet = new ArraySet(list);
            CollectionsKt__MutableCollectionsKt.addAll(arraySet, allowlistedRestrictedPermissionsUnchecked);
            List<String> list3 = CollectionsKt___CollectionsKt.toList(arraySet);
            if (list3 != null) {
                list2 = list3;
                setAllowlistedRestrictedPermissionsUnchecked(androidPackage, i, list2, 2, i2);
            }
        }
        list2 = list;
        setAllowlistedRestrictedPermissionsUnchecked(androidPackage, i, list2, 2, i2);
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public boolean removeAllowlistedRestrictedPermission(String str, String str2, int i, int i2) {
        ArrayList<String> allowlistedRestrictedPermissions;
        if (str2 == null) {
            throw new IllegalArgumentException("permissionName cannot be null".toString());
        }
        if (enforceRestrictedPermission(str2) && (allowlistedRestrictedPermissions = getAllowlistedRestrictedPermissions(str, i, i2)) != null && allowlistedRestrictedPermissions.remove(str2)) {
            return setAllowlistedRestrictedPermissions(str, allowlistedRestrictedPermissions, i, i2, false);
        }
        return false;
    }

    public final boolean enforceRestrictedPermission(String str) {
        AccessState accessState = this.service.state;
        if (accessState == null) {
            Intrinsics.throwUninitializedPropertyAccessException("state");
            accessState = null;
        }
        Permission permission = this.policy.getPermissions(new GetStateScope(accessState)).get(str);
        boolean z = false;
        if (permission == null) {
            Log.w(LOG_TAG, "permission definition for " + str + " does not exist");
            return false;
        }
        PackageManagerLocal packageManagerLocal = this.packageManagerLocal;
        if (packageManagerLocal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("packageManagerLocal");
            packageManagerLocal = null;
        }
        PackageManagerLocal.FilteredSnapshot withFilteredSnapshot = packageManagerLocal.withFilteredSnapshot();
        try {
            PackageState packageState = withFilteredSnapshot.getPackageState(permission.getPermissionInfo().packageName);
            AutoCloseableKt.closeFinally(withFilteredSnapshot, null);
            if (packageState == null) {
                return false;
            }
            if (IntExtensionsKt.hasBits(permission.getPermissionInfo().flags, 12) && IntExtensionsKt.hasBits(permission.getPermissionInfo().flags, 16)) {
                z = true;
            }
            if (!z || this.context.checkCallingOrSelfPermission("android.permission.WHITELIST_RESTRICTED_PERMISSIONS") == 0) {
                return true;
            }
            throw new SecurityException("Cannot modify allowlist of an immutably restricted permission: " + permission.getPermissionInfo().name);
        } finally {
        }
    }

    public final boolean setAllowlistedRestrictedPermissions(String str, List<String> list, int i, int i2, boolean z) {
        AndroidPackage androidPackage;
        Preconditions.checkArgument(Integer.bitCount(i) == 1);
        boolean z2 = this.context.checkCallingOrSelfPermission("android.permission.WHITELIST_RESTRICTED_PERMISSIONS") == 0;
        int callingUid = Binder.getCallingUid();
        PackageManagerLocal packageManagerLocal = this.packageManagerLocal;
        PackageManagerInternal packageManagerInternal = null;
        if (packageManagerLocal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("packageManagerLocal");
            packageManagerLocal = null;
        }
        PackageManagerLocal.FilteredSnapshot withFilteredSnapshot = withFilteredSnapshot(packageManagerLocal, callingUid, i2);
        try {
            PackageState packageState = withFilteredSnapshot.getPackageStates().get(str);
            AutoCloseableKt.closeFinally(withFilteredSnapshot, null);
            if (packageState == null || (androidPackage = packageState.getAndroidPackage()) == null) {
                return false;
            }
            PackageManagerInternal packageManagerInternal2 = this.packageManagerInternal;
            if (packageManagerInternal2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("packageManagerInternal");
            } else {
                packageManagerInternal = packageManagerInternal2;
            }
            boolean isCallerInstallerOfRecord = packageManagerInternal.isCallerInstallerOfRecord(androidPackage, callingUid);
            if (IntExtensionsKt.hasBits(i, 4)) {
                if (!z2 && !isCallerInstallerOfRecord) {
                    throw new SecurityException("Modifying upgrade allowlist requires being installer on record or android.permission.WHITELIST_RESTRICTED_PERMISSIONS");
                }
                if (z && !z2) {
                    throw new SecurityException("Adding to upgrade allowlist requiresandroid.permission.WHITELIST_RESTRICTED_PERMISSIONS");
                }
            }
            setAllowlistedRestrictedPermissionsUnchecked(androidPackage, packageState.getAppId(), list, i, i2);
            return true;
        } finally {
        }
    }

    public final void setAllowlistedRestrictedPermissionsUnchecked(AndroidPackage androidPackage, int i, List<String> list, int i2, int i3) {
        int i4;
        int i5;
        List<String> list2;
        ArrayMap<String, Permission> arrayMap;
        int i6;
        int i7;
        List<String> list3 = list;
        AccessCheckingService accessCheckingService = this.service;
        synchronized (accessCheckingService.stateLock) {
            AccessState accessState = accessCheckingService.state;
            if (accessState == null) {
                Intrinsics.throwUninitializedPropertyAccessException("state");
                accessState = null;
            }
            AccessState copy = accessState.copy();
            MutateStateScope mutateStateScope = new MutateStateScope(accessState, copy);
            UidPermissionPolicy uidPermissionPolicy = this.policy;
            ArrayMap<String, Integer> uidPermissionFlags = uidPermissionPolicy.getUidPermissionFlags(mutateStateScope, i, i3);
            if (uidPermissionFlags != null) {
                ArrayMap<String, Permission> permissions = uidPermissionPolicy.getPermissions(mutateStateScope);
                List<String> requestedPermissions = androidPackage.getRequestedPermissions();
                int size = requestedPermissions.size();
                int i8 = 0;
                while (i8 < size) {
                    String str = requestedPermissions.get(i8);
                    Permission permission = permissions.get(str);
                    if (permission != null && IntExtensionsKt.hasBits(permission.getPermissionInfo().flags, 12)) {
                        Integer num = uidPermissionFlags.get(str);
                        if (num == null) {
                            num = 0;
                        }
                        int intValue = num.intValue();
                        boolean isPermissionGranted = PermissionFlags.INSTANCE.isPermissionGranted(intValue);
                        int i9 = i2;
                        int i10 = intValue;
                        int i11 = i8;
                        int i12 = 0;
                        while (i9 != 0) {
                            int i13 = size;
                            List<String> list4 = requestedPermissions;
                            int numberOfTrailingZeros = 1 << Integer.numberOfTrailingZeros(i9);
                            i9 &= ~numberOfTrailingZeros;
                            if (numberOfTrailingZeros == 1) {
                                i7 = 65536;
                                i12 |= 65536;
                                if (!list3.contains(str)) {
                                    i10 = IntExtensionsKt.andInv(i10, 65536);
                                }
                            } else if (numberOfTrailingZeros == 2) {
                                i7 = 32768;
                                i12 |= 32768;
                                if (!list3.contains(str)) {
                                    i10 = IntExtensionsKt.andInv(i10, 32768);
                                }
                            } else if (numberOfTrailingZeros == 4) {
                                i7 = IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES;
                                i12 |= IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES;
                                i10 = list3.contains(str) ? i10 | i7 : IntExtensionsKt.andInv(i10, IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES);
                            }
                            size = i13;
                            requestedPermissions = list4;
                        }
                        int i14 = size;
                        List<String> list5 = requestedPermissions;
                        if (intValue == i10) {
                            i4 = i11;
                            i5 = i14;
                            list2 = list5;
                            arrayMap = permissions;
                            i8 = i4 + 1;
                            requestedPermissions = list2;
                            size = i5;
                            permissions = arrayMap;
                            list3 = list;
                        } else {
                            boolean hasAnyBit = IntExtensionsKt.hasAnyBit(intValue, 229376);
                            boolean hasAnyBit2 = IntExtensionsKt.hasAnyBit(i10, 229376);
                            if (IntExtensionsKt.hasBits(intValue, 128) && !hasAnyBit2 && isPermissionGranted) {
                                i12 |= 128;
                                i10 = IntExtensionsKt.andInv(i10, 128);
                            }
                            if (androidPackage.getTargetSdkVersion() >= 23 || hasAnyBit || !hasAnyBit2) {
                                i6 = i12;
                            } else {
                                int i15 = i12 | IInstalld.FLAG_USE_QUOTA;
                                i10 |= IInstalld.FLAG_USE_QUOTA;
                                i6 = i15;
                            }
                            i4 = i11;
                            i5 = i14;
                            list2 = list5;
                            arrayMap = permissions;
                            uidPermissionPolicy.updatePermissionFlags(mutateStateScope, i, i3, str, i6, i10);
                            i8 = i4 + 1;
                            requestedPermissions = list2;
                            size = i5;
                            permissions = arrayMap;
                            list3 = list;
                        }
                    }
                    i4 = i8;
                    i5 = size;
                    list2 = requestedPermissions;
                    arrayMap = permissions;
                    i8 = i4 + 1;
                    requestedPermissions = list2;
                    size = i5;
                    permissions = arrayMap;
                    list3 = list;
                }
            }
            accessCheckingService.persistence.write(copy);
            accessCheckingService.state = copy;
            accessCheckingService.policy.onStateMutated(new GetStateScope(copy));
            Unit unit = Unit.INSTANCE;
        }
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public void addOnPermissionsChangeListener(IOnPermissionsChangeListener iOnPermissionsChangeListener) {
        OnPermissionsChangeListeners onPermissionsChangeListeners = this.onPermissionsChangeListeners;
        if (onPermissionsChangeListeners == null) {
            Intrinsics.throwUninitializedPropertyAccessException("onPermissionsChangeListeners");
            onPermissionsChangeListeners = null;
        }
        onPermissionsChangeListeners.addListener(iOnPermissionsChangeListener);
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public void removeOnPermissionsChangeListener(IOnPermissionsChangeListener iOnPermissionsChangeListener) {
        OnPermissionsChangeListeners onPermissionsChangeListeners = this.onPermissionsChangeListeners;
        if (onPermissionsChangeListeners == null) {
            Intrinsics.throwUninitializedPropertyAccessException("onPermissionsChangeListeners");
            onPermissionsChangeListeners = null;
        }
        onPermissionsChangeListeners.removeListener(iOnPermissionsChangeListener);
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public List<SplitPermissionInfoParcelable> getSplitPermissions() {
        SystemConfig systemConfig = this.systemConfig;
        if (systemConfig == null) {
            Intrinsics.throwUninitializedPropertyAccessException("systemConfig");
            systemConfig = null;
        }
        return PermissionManager.splitPermissionInfoListToParcelableList(systemConfig.getSplitPermissions());
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public String[] getAppOpPermissionPackages(String str) {
        if (str == null) {
            throw new IllegalArgumentException("permissionName cannot be null".toString());
        }
        ArraySet arraySet = new ArraySet();
        AccessState accessState = this.service.state;
        if (accessState == null) {
            Intrinsics.throwUninitializedPropertyAccessException("state");
            accessState = null;
        }
        Permission permission = this.policy.getPermissions(new GetStateScope(accessState)).get(str);
        if (permission == null || !IntExtensionsKt.hasBits(permission.getPermissionInfo().getProtectionFlags(), 64)) {
            arraySet.toArray(new String[0]);
        }
        PackageManagerLocal packageManagerLocal = this.packageManagerLocal;
        if (packageManagerLocal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("packageManagerLocal");
            packageManagerLocal = null;
        }
        PackageManagerLocal.UnfilteredSnapshot withUnfilteredSnapshot = packageManagerLocal.withUnfilteredSnapshot();
        try {
            for (Map.Entry<String, PackageState> entry : withUnfilteredSnapshot.getPackageStates().entrySet()) {
                AndroidPackage androidPackage = entry.getValue().getAndroidPackage();
                if (androidPackage != null && androidPackage.getRequestedPermissions().contains(str)) {
                    arraySet.add(androidPackage.getPackageName());
                }
            }
            Unit unit = Unit.INSTANCE;
            AutoCloseableKt.closeFinally(withUnfilteredSnapshot, null);
            return (String[]) arraySet.toArray(new String[0]);
        } finally {
        }
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public Map<String, Set<String>> getAllAppOpPermissionPackages() {
        ArrayMap arrayMap = new ArrayMap();
        AccessState accessState = this.service.state;
        if (accessState == null) {
            Intrinsics.throwUninitializedPropertyAccessException("state");
            accessState = null;
        }
        ArrayMap<String, Permission> permissions = this.policy.getPermissions(new GetStateScope(accessState));
        PackageManagerLocal packageManagerLocal = this.packageManagerLocal;
        if (packageManagerLocal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("packageManagerLocal");
            packageManagerLocal = null;
        }
        PackageManagerLocal.UnfilteredSnapshot withUnfilteredSnapshot = packageManagerLocal.withUnfilteredSnapshot();
        try {
            for (Map.Entry<String, PackageState> entry : withUnfilteredSnapshot.getPackageStates().entrySet()) {
                AndroidPackage androidPackage = entry.getValue().getAndroidPackage();
                if (androidPackage != null) {
                    for (String str : androidPackage.getRequestedPermissions()) {
                        Permission permission = permissions.get(str);
                        if (permission != null && IntExtensionsKt.hasBits(permission.getPermissionInfo().getProtectionFlags(), 64)) {
                            Object obj = arrayMap.get(str);
                            if (obj == null) {
                                obj = new ArraySet();
                                arrayMap.put(str, obj);
                            }
                            ((ArraySet) obj).add(androidPackage.getPackageName());
                        }
                    }
                }
            }
            Unit unit = Unit.INSTANCE;
            AutoCloseableKt.closeFinally(withUnfilteredSnapshot, null);
            return arrayMap;
        } finally {
        }
    }

    public byte[] backupRuntimePermissions(int i) {
        Preconditions.checkArgumentNonnegative(i, "userId cannot be null");
        final CompletableFuture completableFuture = new CompletableFuture();
        PermissionControllerManager permissionControllerManager = this.permissionControllerManager;
        if (permissionControllerManager == null) {
            Intrinsics.throwUninitializedPropertyAccessException("permissionControllerManager");
            permissionControllerManager = null;
        }
        permissionControllerManager.getRuntimePermissionBackup(UserHandle.of(i), PermissionThread.getExecutor(), new Consumer() { // from class: com.android.server.permission.access.permission.PermissionService$backupRuntimePermissions$1
            @Override // java.util.function.Consumer
            public final void accept(byte[] bArr) {
                completableFuture.complete(bArr);
            }
        });
        try {
            return (byte[]) completableFuture.get(BACKUP_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            if (e instanceof TimeoutException ? true : e instanceof InterruptedException ? true : e instanceof ExecutionException) {
                Log.e(LOG_TAG, "Cannot create permission backup for user " + i, e);
                return null;
            }
            throw e;
        }
    }

    public void restoreRuntimePermissions(byte[] bArr, int i) {
        if (bArr == null) {
            throw new IllegalArgumentException("backup".toString());
        }
        Preconditions.checkArgumentNonnegative(i, "userId");
        synchronized (this.isDelayedPermissionBackupFinished) {
            this.isDelayedPermissionBackupFinished.delete(i);
            Unit unit = Unit.INSTANCE;
        }
        PermissionControllerManager permissionControllerManager = this.permissionControllerManager;
        if (permissionControllerManager == null) {
            Intrinsics.throwUninitializedPropertyAccessException("permissionControllerManager");
            permissionControllerManager = null;
        }
        permissionControllerManager.stageAndApplyRuntimePermissionsBackup(bArr, UserHandle.of(i));
    }

    public void restoreDelayedRuntimePermissions(String str, final int i) {
        if (str == null) {
            throw new IllegalArgumentException("packageName".toString());
        }
        Preconditions.checkArgumentNonnegative(i, "userId");
        synchronized (this.isDelayedPermissionBackupFinished) {
            if (this.isDelayedPermissionBackupFinished.get(i, false)) {
                return;
            }
            Unit unit = Unit.INSTANCE;
            PermissionControllerManager permissionControllerManager = this.permissionControllerManager;
            if (permissionControllerManager == null) {
                Intrinsics.throwUninitializedPropertyAccessException("permissionControllerManager");
                permissionControllerManager = null;
            }
            permissionControllerManager.applyStagedRuntimePermissionBackup(str, UserHandle.of(i), PermissionThread.getExecutor(), new Consumer() { // from class: com.android.server.permission.access.permission.PermissionService$restoreDelayedRuntimePermissions$3
                @Override // java.util.function.Consumer
                public final void accept(Boolean bool) {
                    SparseBooleanArray sparseBooleanArray;
                    SparseBooleanArray sparseBooleanArray2;
                    if (bool.booleanValue()) {
                        return;
                    }
                    sparseBooleanArray = PermissionService.this.isDelayedPermissionBackupFinished;
                    PermissionService permissionService = PermissionService.this;
                    int i2 = i;
                    synchronized (sparseBooleanArray) {
                        sparseBooleanArray2 = permissionService.isDelayedPermissionBackupFinished;
                        sparseBooleanArray2.put(i2, true);
                        Unit unit2 = Unit.INSTANCE;
                    }
                }
            });
        }
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public com.android.server.p011pm.permission.Permission getPermissionTEMP(String str) {
        AccessState accessState = this.service.state;
        if (accessState == null) {
            Intrinsics.throwUninitializedPropertyAccessException("state");
            accessState = null;
        }
        Permission permission = this.policy.getPermissions(new GetStateScope(accessState)).get(str);
        if (permission == null) {
            return null;
        }
        return new com.android.server.p011pm.permission.Permission(permission.getPermissionInfo(), permission.getType(), permission.isReconciled(), permission.getAppId(), permission.getGids(), permission.getAreGidsPerUser());
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public List<LegacyPermission> getLegacyPermissions() {
        AccessState accessState = this.service.state;
        if (accessState == null) {
            Intrinsics.throwUninitializedPropertyAccessException("state");
            accessState = null;
        }
        ArrayMap<String, Permission> permissions = this.policy.getPermissions(new GetStateScope(accessState));
        ArrayList arrayList = new ArrayList();
        int size = permissions.size();
        for (int i = 0; i < size; i++) {
            permissions.keyAt(i);
            Permission valueAt = permissions.valueAt(i);
            arrayList.add(new LegacyPermission(valueAt.getPermissionInfo(), valueAt.getType(), valueAt.getAppId(), valueAt.getGids()));
        }
        return arrayList;
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public void readLegacyPermissionsTEMP(LegacyPermissionSettings legacyPermissionSettings) {
        this.service.initialize();
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public void writeLegacyPermissionsTEMP(LegacyPermissionSettings legacyPermissionSettings) {
        AccessState accessState = this.service.state;
        if (accessState == null) {
            Intrinsics.throwUninitializedPropertyAccessException("state");
            accessState = null;
        }
        GetStateScope getStateScope = new GetStateScope(accessState);
        legacyPermissionSettings.replacePermissions(toLegacyPermissions(this.policy.getPermissions(getStateScope)));
        legacyPermissionSettings.replacePermissionTrees(toLegacyPermissions(this.policy.getPermissionTrees(getStateScope)));
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public LegacyPermissionState getLegacyPermissionState(int i) {
        int[] iArr;
        PermissionService permissionService = this;
        LegacyPermissionState legacyPermissionState = new LegacyPermissionState();
        UserManagerService userManagerService = permissionService.userManagerService;
        AccessState accessState = null;
        if (userManagerService == null) {
            Intrinsics.throwUninitializedPropertyAccessException("userManagerService");
            userManagerService = null;
        }
        int[] userIdsIncludingPreCreated = userManagerService.getUserIdsIncludingPreCreated();
        AccessState accessState2 = permissionService.service.state;
        if (accessState2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("state");
        } else {
            accessState = accessState2;
        }
        GetStateScope getStateScope = new GetStateScope(accessState);
        ArrayMap<String, Permission> permissions = permissionService.policy.getPermissions(getStateScope);
        int length = userIdsIncludingPreCreated.length;
        int i2 = 0;
        while (i2 < length) {
            int i3 = userIdsIncludingPreCreated[i2];
            ArrayMap<String, Integer> uidPermissionFlags = permissionService.policy.getUidPermissionFlags(getStateScope, i, i3);
            if (uidPermissionFlags != null) {
                int size = uidPermissionFlags.size();
                int i4 = 0;
                while (i4 < size) {
                    String keyAt = uidPermissionFlags.keyAt(i4);
                    int intValue = uidPermissionFlags.valueAt(i4).intValue();
                    String str = keyAt;
                    Permission permission = permissions.get(str);
                    if (permission == null) {
                        iArr = userIdsIncludingPreCreated;
                    } else {
                        boolean z = permission.getPermissionInfo().getProtection() == 1;
                        PermissionFlags permissionFlags = PermissionFlags.INSTANCE;
                        iArr = userIdsIncludingPreCreated;
                        legacyPermissionState.putPermissionState(new LegacyPermissionState.PermissionState(str, z, permissionFlags.isPermissionGranted(intValue), permissionFlags.toApiFlags(intValue)), i3);
                    }
                    i4++;
                    userIdsIncludingPreCreated = iArr;
                }
            }
            i2++;
            permissionService = this;
            userIdsIncludingPreCreated = userIdsIncludingPreCreated;
        }
        return legacyPermissionState;
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public void onSystemReady() {
        this.service.m37x9f7a1b33();
        this.permissionControllerManager = new PermissionControllerManager(this.context, PermissionThread.getHandler());
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public void onUserCreated(int i) {
        this.service.m36xd61c3d34(i);
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public void onUserRemoved(int i) {
        this.service.m35x4ed10714(i);
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public void onStorageVolumeMounted(String str, boolean z) {
        this.service.m38xe2cba220(str, z);
        synchronized (this.mountedStorageVolumes) {
            this.mountedStorageVolumes.add(str);
            Unit unit = Unit.INSTANCE;
        }
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public void onPackageAdded(PackageState packageState, boolean z, AndroidPackage androidPackage) {
        synchronized (this.mountedStorageVolumes) {
            if (this.mountedStorageVolumes.contains(packageState.getVolumeUuid())) {
                Unit unit = Unit.INSTANCE;
                this.service.m42x9a5a53b7(packageState.getPackageName());
            }
        }
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public void onPackageInstalled(AndroidPackage androidPackage, int i, PermissionManagerServiceInternal.PackageInstalledParams packageInstalledParams, int i2) {
        int[] iArr;
        synchronized (this.mountedStorageVolumes) {
            if (this.mountedStorageVolumes.contains(androidPackage.getVolumeUuid())) {
                Unit unit = Unit.INSTANCE;
                if (i2 == -1) {
                    UserManagerService userManagerService = this.userManagerService;
                    if (userManagerService == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("userManagerService");
                        userManagerService = null;
                    }
                    iArr = userManagerService.getUserIdsIncludingPreCreated();
                } else {
                    iArr = new int[]{i2};
                }
                for (int i3 : iArr) {
                    this.service.m41x89669eb1(androidPackage.getPackageName(), i3);
                }
                for (int i4 : iArr) {
                    PackageManagerInternal packageManagerInternal = this.packageManagerInternal;
                    if (packageManagerInternal == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("packageManagerInternal");
                        packageManagerInternal = null;
                    }
                    PackageStateInternal packageStateInternal = packageManagerInternal.getPackageStateInternal(androidPackage.getPackageName());
                    Intrinsics.checkNotNull(packageStateInternal);
                    addAllowlistedRestrictedPermissionsUnchecked(androidPackage, packageStateInternal.getAppId(), packageInstalledParams.getAllowlistedRestrictedPermissions(), i4);
                    setRequestedPermissionStates(packageStateInternal, i4, packageInstalledParams.getPermissionStates());
                }
            }
        }
    }

    @Override // com.android.server.p011pm.permission.PermissionManagerServiceInterface
    public void onPackageUninstalled(String str, int i, PackageState packageState, AndroidPackage androidPackage, List<? extends AndroidPackage> list, int i2) {
        int[] iArr;
        PackageManagerInternal packageManagerInternal = null;
        if (i2 == -1) {
            UserManagerService userManagerService = this.userManagerService;
            if (userManagerService == null) {
                Intrinsics.throwUninitializedPropertyAccessException("userManagerService");
                userManagerService = null;
            }
            iArr = userManagerService.getUserIdsIncludingPreCreated();
        } else {
            iArr = new int[]{i2};
        }
        for (int i3 : iArr) {
            this.service.m39x2b8d4f78(str, i, i3);
        }
        PackageManagerInternal packageManagerInternal2 = this.packageManagerInternal;
        if (packageManagerInternal2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("packageManagerInternal");
        } else {
            packageManagerInternal = packageManagerInternal2;
        }
        if (packageManagerInternal.getPackageStates().get(str) == null) {
            this.service.m40xfbe388d7(str, i);
        }
    }

    public final boolean isRootOrSystem(int i) {
        int appId = UserHandle.getAppId(i);
        return appId == 0 || appId == 1000;
    }

    public final boolean isShell(int i) {
        return UserHandle.getAppId(i) == 2000;
    }

    public final boolean isRootOrSystemOrShell(int i) {
        return isRootOrSystem(i) || isShell(i);
    }

    public final void killUid(int i, String str) {
        IActivityManager service = ActivityManager.getService();
        if (service != null) {
            int appId = UserHandle.getAppId(i);
            int userId = UserHandle.getUserId(i);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                try {
                    service.killUidForPermissionChange(appId, userId, str);
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            } catch (RemoteException unused) {
            }
            Unit unit = Unit.INSTANCE;
        }
    }

    public final PackageManagerLocal.FilteredSnapshot withFilteredSnapshot(PackageManagerLocal packageManagerLocal, int i, int i2) {
        return packageManagerLocal.withFilteredSnapshot(i, UserHandle.of(i2));
    }

    public final PackageState getPackageState(PackageManagerLocal.UnfilteredSnapshot unfilteredSnapshot, String str) {
        return unfilteredSnapshot.getPackageStates().get(str);
    }

    public final boolean isUidInstantApp(PackageManagerLocal.UnfilteredSnapshot unfilteredSnapshot, int i) {
        PackageManagerInternal packageManagerInternal = this.packageManagerInternal;
        if (packageManagerInternal == null) {
            Intrinsics.throwUninitializedPropertyAccessException("packageManagerInternal");
            packageManagerInternal = null;
        }
        return packageManagerInternal.getInstantAppPackageName(i) != null;
    }

    public final boolean isPackageVisibleToUid(PackageManagerLocal.UnfilteredSnapshot unfilteredSnapshot, String str, int i) {
        return isPackageVisibleToUid(unfilteredSnapshot, str, UserHandle.getUserId(i), i);
    }

    public final boolean isPackageVisibleToUid(PackageManagerLocal.UnfilteredSnapshot unfilteredSnapshot, String str, int i, int i2) {
        PackageManagerLocal.FilteredSnapshot filtered = filtered(unfilteredSnapshot, i2, i);
        try {
            boolean z = filtered.getPackageState(str) != null;
            AutoCloseableKt.closeFinally(filtered, null);
            return z;
        } finally {
        }
    }

    public final PackageManagerLocal.FilteredSnapshot filtered(PackageManagerLocal.UnfilteredSnapshot unfilteredSnapshot, int i, int i2) {
        return unfilteredSnapshot.filtered(i, UserHandle.of(i2));
    }

    public final void enforceCallingOrSelfCrossUserPermission(int i, boolean z, boolean z2, String str) {
        if (!(i >= 0)) {
            throw new IllegalArgumentException(("userId " + i + " is invalid").toString());
        }
        int callingUid = Binder.getCallingUid();
        if (i != UserHandle.getUserId(callingUid)) {
            String str2 = z ? "android.permission.INTERACT_ACROSS_USERS_FULL" : "android.permission.INTERACT_ACROSS_USERS";
            if (this.context.checkCallingOrSelfPermission(str2) != 0) {
                StringBuilder sb = new StringBuilder();
                if (str != null) {
                    sb.append(str);
                    sb.append(": ");
                }
                sb.append("Neither user ");
                sb.append(Binder.getCallingUid());
                sb.append(" nor current process has ");
                sb.append(str2);
                sb.append(" to access user ");
                sb.append(i);
                String sb2 = sb.toString();
                Intrinsics.checkNotNullExpressionValue(sb2, "StringBuilder().apply(builderAction).toString()");
                throw new SecurityException(sb2);
            }
        }
        if (z2 && isShell(callingUid)) {
            UserManagerInternal userManagerInternal = this.userManagerInternal;
            if (userManagerInternal == null) {
                Intrinsics.throwUninitializedPropertyAccessException("userManagerInternal");
                userManagerInternal = null;
            }
            if (userManagerInternal.hasUserRestriction("no_debugging_features", i)) {
                StringBuilder sb3 = new StringBuilder();
                if (str != null) {
                    sb3.append(str);
                    sb3.append(": ");
                }
                sb3.append("Shell is disallowed to access user ");
                sb3.append(i);
                String sb4 = sb3.toString();
                Intrinsics.checkNotNullExpressionValue(sb4, "StringBuilder().apply(builderAction).toString()");
                throw new SecurityException(sb4);
            }
        }
    }

    /* compiled from: PermissionService.kt */
    /* loaded from: classes2.dex */
    public final class OnPermissionFlagsChangedListener extends UidPermissionPolicy.OnPermissionFlagsChangedListener {
        public boolean isKillRuntimePermissionRevokedUidsSkipped;
        public boolean isPermissionFlagsChanged;
        public final IntSet runtimePermissionChangedUids = new IntSet();
        public final SparseBooleanArray runtimePermissionRevokedUids = new SparseBooleanArray();
        public final IntSet gidsChangedUids = new IntSet();
        public final ArraySet<String> killRuntimePermissionRevokedUidsReasons = new ArraySet<>();

        public OnPermissionFlagsChangedListener() {
        }

        public final void skipKillRuntimePermissionRevokedUids(MutateStateScope mutateStateScope) {
            this.isKillRuntimePermissionRevokedUidsSkipped = true;
        }

        public final void addKillRuntimePermissionRevokedUidsReason(MutateStateScope mutateStateScope, String str) {
            this.killRuntimePermissionRevokedUidsReasons.add(str);
        }

        @Override // com.android.server.permission.access.permission.UidPermissionPolicy.OnPermissionFlagsChangedListener
        public void onPermissionFlagsChanged(int i, int i2, String str, int i3, int i4) {
            boolean z;
            this.isPermissionFlagsChanged = true;
            int uid = UserHandle.getUid(i2, i);
            AccessCheckingService accessCheckingService = PermissionService.this.service;
            PermissionService permissionService = PermissionService.this;
            AccessState accessState = accessCheckingService.state;
            if (accessState == null) {
                Intrinsics.throwUninitializedPropertyAccessException("state");
                accessState = null;
            }
            Permission permission = permissionService.policy.getPermissions(new GetStateScope(accessState)).get(str);
            if (permission == null) {
                return;
            }
            PermissionFlags permissionFlags = PermissionFlags.INSTANCE;
            boolean isPermissionGranted = permissionFlags.isPermissionGranted(i3);
            boolean isPermissionGranted2 = permissionFlags.isPermissionGranted(i4);
            if (permission.getPermissionInfo().getProtection() == 1) {
                this.runtimePermissionChangedUids.add(uid);
                if (isPermissionGranted && !isPermissionGranted2) {
                    SparseBooleanArray sparseBooleanArray = this.runtimePermissionRevokedUids;
                    if (PermissionService.NOTIFICATIONS_PERMISSIONS.contains(str)) {
                        SparseBooleanArray sparseBooleanArray2 = this.runtimePermissionRevokedUids;
                        if (sparseBooleanArray2 == null ? true : sparseBooleanArray2.get(uid, true)) {
                            z = true;
                            sparseBooleanArray.put(uid, z);
                        }
                    }
                    z = false;
                    sparseBooleanArray.put(uid, z);
                }
            }
            if ((!(permission.getGids().length == 0)) && !isPermissionGranted && isPermissionGranted2) {
                this.gidsChangedUids.add(uid);
            }
        }

        @Override // com.android.server.permission.access.permission.UidPermissionPolicy.OnPermissionFlagsChangedListener
        public void onStateMutated() {
            Handler handler;
            OnPermissionsChangeListeners onPermissionsChangeListeners;
            if (this.isPermissionFlagsChanged) {
                PackageManager.invalidatePackageInfoCache();
                this.isPermissionFlagsChanged = false;
            }
            IntSet intSet = this.runtimePermissionChangedUids;
            PermissionService permissionService = PermissionService.this;
            int size = intSet.getSize();
            for (int i = 0; i < size; i++) {
                int elementAt = intSet.elementAt(i);
                OnPermissionsChangeListeners onPermissionsChangeListeners2 = permissionService.onPermissionsChangeListeners;
                if (onPermissionsChangeListeners2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("onPermissionsChangeListeners");
                    onPermissionsChangeListeners = null;
                } else {
                    onPermissionsChangeListeners = onPermissionsChangeListeners2;
                }
                onPermissionsChangeListeners.onPermissionsChanged(elementAt);
            }
            this.runtimePermissionChangedUids.clear();
            if (!this.isKillRuntimePermissionRevokedUidsSkipped) {
                final String joinToString$default = this.killRuntimePermissionRevokedUidsReasons.isEmpty() ^ true ? CollectionsKt___CollectionsKt.joinToString$default(this.killRuntimePermissionRevokedUidsReasons, ", ", null, null, 0, null, null, 62, null) : "permissions revoked";
                SparseBooleanArray sparseBooleanArray = this.runtimePermissionRevokedUids;
                final PermissionService permissionService2 = PermissionService.this;
                int size2 = sparseBooleanArray.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    final int keyAt = sparseBooleanArray.keyAt(i2);
                    final boolean valueAt = sparseBooleanArray.valueAt(i2);
                    Handler handler2 = permissionService2.handler;
                    if (handler2 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("handler");
                        handler = null;
                    } else {
                        handler = handler2;
                    }
                    handler.post(new Runnable() { // from class: com.android.server.permission.access.permission.PermissionService$OnPermissionFlagsChangedListener$onStateMutated$2$1
                        @Override // java.lang.Runnable
                        public final void run() {
                            boolean isAppBackupAndRestoreRunning;
                            if (valueAt) {
                                isAppBackupAndRestoreRunning = this.isAppBackupAndRestoreRunning(keyAt);
                                if (isAppBackupAndRestoreRunning) {
                                    return;
                                }
                            }
                            permissionService2.killUid(keyAt, joinToString$default);
                        }
                    });
                }
            }
            this.runtimePermissionRevokedUids.clear();
            IntSet intSet2 = this.gidsChangedUids;
            final PermissionService permissionService3 = PermissionService.this;
            int size3 = intSet2.getSize();
            for (int i3 = 0; i3 < size3; i3++) {
                final int elementAt2 = intSet2.elementAt(i3);
                Handler handler3 = permissionService3.handler;
                if (handler3 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("handler");
                    handler3 = null;
                }
                handler3.post(new Runnable() { // from class: com.android.server.permission.access.permission.PermissionService$OnPermissionFlagsChangedListener$onStateMutated$3$1
                    @Override // java.lang.Runnable
                    public final void run() {
                        PermissionService.this.killUid(elementAt2, "permission grant or revoke changed gids");
                    }
                });
            }
            this.gidsChangedUids.clear();
            this.isKillRuntimePermissionRevokedUidsSkipped = false;
            this.killRuntimePermissionRevokedUidsReasons.clear();
        }

        public final boolean isAppBackupAndRestoreRunning(int i) {
            if (PermissionService.this.checkUidPermission(i, "android.permission.BACKUP") != 0) {
                return false;
            }
            try {
                ContentResolver contentResolver = PermissionService.this.context.getContentResolver();
                int userId = UserHandle.getUserId(i);
                return (Settings.Secure.getIntForUser(contentResolver, "user_setup_complete", userId) == 0) || (Settings.Secure.getIntForUser(contentResolver, "user_setup_personalization_state", userId) == 1);
            } catch (Settings.SettingNotFoundException e) {
                String str = PermissionService.LOG_TAG;
                Log.w(str, "Failed to check if the user is in restore: " + e);
                return false;
            }
        }
    }

    /* compiled from: PermissionService.kt */
    /* loaded from: classes2.dex */
    public static final class OnPermissionsChangeListeners extends Handler {
        public static final Companion Companion = new Companion(null);
        public final RemoteCallbackList<IOnPermissionsChangeListener> listeners;

        public OnPermissionsChangeListeners(Looper looper) {
            super(looper);
            this.listeners = new RemoteCallbackList<>();
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 1) {
                handleOnPermissionsChanged(message.arg1);
            }
        }

        public final void handleOnPermissionsChanged(final int i) {
            this.listeners.broadcast(new Consumer() { // from class: com.android.server.permission.access.permission.PermissionService$OnPermissionsChangeListeners$handleOnPermissionsChanged$1
                @Override // java.util.function.Consumer
                public final void accept(IOnPermissionsChangeListener iOnPermissionsChangeListener) {
                    try {
                        iOnPermissionsChangeListener.onPermissionsChanged(i);
                    } catch (RemoteException e) {
                        Log.e(PermissionService.LOG_TAG, "Error when calling OnPermissionsChangeListener", e);
                    }
                }
            });
        }

        public final void addListener(IOnPermissionsChangeListener iOnPermissionsChangeListener) {
            this.listeners.register(iOnPermissionsChangeListener);
        }

        public final void removeListener(IOnPermissionsChangeListener iOnPermissionsChangeListener) {
            this.listeners.unregister(iOnPermissionsChangeListener);
        }

        public final void onPermissionsChanged(int i) {
            if (this.listeners.getRegisteredCallbackCount() > 0) {
                obtainMessage(1, i, 0).sendToTarget();
            }
        }

        /* compiled from: PermissionService.kt */
        /* loaded from: classes2.dex */
        public static final class Companion {
            public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
                this();
            }

            public Companion() {
            }
        }
    }

    /* compiled from: PermissionService.kt */
    /* loaded from: classes2.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public Companion() {
        }
    }

    static {
        ArrayMap<String, String> arrayMap = new ArrayMap<>();
        arrayMap.put("android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION");
        arrayMap.put("android.permission.INTERACT_ACROSS_USERS", "android.permission.INTERACT_ACROSS_USERS_FULL");
        FULLER_PERMISSIONS = arrayMap;
        NOTIFICATIONS_PERMISSIONS = new ArraySet<>(ArraysKt___ArraysJvmKt.asList(new String[]{"android.permission.POST_NOTIFICATIONS"}));
        BACKUP_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(60L);
    }

    public final void enforceCallingOrSelfAnyPermission(String str, String... strArr) {
        int length = strArr.length;
        boolean z = false;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            if (this.context.checkCallingOrSelfPermission(strArr[i]) == 0) {
                z = true;
                break;
            }
            i++;
        }
        if (z) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (str != null) {
            sb.append(str);
            sb.append(": ");
        }
        sb.append("Neither user ");
        sb.append(Binder.getCallingUid());
        sb.append(" nor current process has any of ");
        ArraysKt___ArraysKt.joinTo(strArr, sb, (r14 & 2) != 0 ? ", " : ", ", (r14 & 4) != 0 ? "" : null, (r14 & 8) == 0 ? null : "", (r14 & 16) != 0 ? -1 : 0, (r14 & 32) != 0 ? "..." : null, (r14 & 64) != 0 ? null : null);
        String sb2 = sb.toString();
        Intrinsics.checkNotNullExpressionValue(sb2, "StringBuilder().apply(builderAction).toString()");
        throw new SecurityException(sb2);
    }
}
