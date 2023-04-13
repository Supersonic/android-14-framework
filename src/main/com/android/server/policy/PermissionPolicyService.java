package com.android.server.policy;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.AppOpsManager;
import android.app.AppOpsManagerInternal;
import android.app.KeyguardManager;
import android.app.TaskInfo;
import android.app.compat.CompatChanges;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.UserPackage;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.p005os.IInstalld;
import android.permission.LegacyPermissionManager;
import android.permission.PermissionControllerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.LongSparseLongArray;
import android.util.Slog;
import android.util.SparseBooleanArray;
import com.android.internal.R;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.IAppOpsCallback;
import com.android.internal.app.IAppOpsService;
import com.android.internal.infra.AndroidFuture;
import com.android.internal.policy.AttributeCache;
import com.android.internal.util.IntPair;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.PermissionThread;
import com.android.server.SystemService;
import com.android.server.notification.NotificationManagerInternal;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.p011pm.permission.PermissionManagerServiceInternal;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p014wm.ActivityInterceptorCallback;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import com.android.server.policy.PermissionPolicyInternal;
import com.android.server.policy.PermissionPolicyService;
import com.android.server.utils.TimingsTraceAndSlog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public final class PermissionPolicyService extends SystemService {
    public static final String LOG_TAG = "PermissionPolicyService";
    public List<String> mAppOpPermissions;
    public IAppOpsCallback mAppOpsCallback;
    @GuardedBy({"mLock"})
    public boolean mBootCompleted;
    public Context mContext;
    public final Handler mHandler;
    @GuardedBy({"mLock"})
    public final ArraySet<UserPackage> mIsPackageSyncsScheduled;
    @GuardedBy({"mLock"})
    public final SparseBooleanArray mIsStarted;
    @GuardedBy({"mLock"})
    public final SparseBooleanArray mIsUidSyncScheduled;
    public final KeyguardManager mKeyguardManager;
    public final Object mLock;
    public NotificationManagerInternal mNotificationManager;
    @GuardedBy({"mLock"})
    public PermissionPolicyInternal.OnInitializedCallback mOnInitializedCallback;
    public final PackageManager mPackageManager;
    public PackageManagerInternal mPackageManagerInternal;
    public PermissionManagerServiceInternal mPermissionManagerInternal;
    public final ArrayList<PhoneCarrierPrivilegesCallback> mPhoneCarrierPrivilegesCallbacks;
    public final BroadcastReceiver mSimConfigBroadcastReceiver;
    public TelephonyManager mTelephonyManager;

    public PermissionPolicyService(Context context) {
        super(context);
        this.mLock = new Object();
        this.mBootCompleted = false;
        this.mIsStarted = new SparseBooleanArray();
        this.mIsPackageSyncsScheduled = new ArraySet<>();
        this.mIsUidSyncScheduled = new SparseBooleanArray();
        this.mPhoneCarrierPrivilegesCallbacks = new ArrayList<>();
        this.mSimConfigBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.policy.PermissionPolicyService.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if ("android.telephony.action.MULTI_SIM_CONFIG_CHANGED".equals(intent.getAction())) {
                    PermissionPolicyService.this.unregisterCarrierPrivilegesCallback();
                    PermissionPolicyService.this.registerCarrierPrivilegesCallbacks();
                }
            }
        };
        this.mContext = context;
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mPackageManager = context.getPackageManager();
        this.mKeyguardManager = (KeyguardManager) context.getSystemService(KeyguardManager.class);
        LocalServices.addService(PermissionPolicyInternal.class, new Internal());
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.android.server.SystemService
    public void onStart() {
        char c;
        int extraAppOpCode;
        this.mPackageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        this.mPermissionManagerInternal = (PermissionManagerServiceInternal) LocalServices.getService(PermissionManagerServiceInternal.class);
        IAppOpsService asInterface = IAppOpsService.Stub.asInterface(ServiceManager.getService("appops"));
        this.mPackageManagerInternal.getPackageList(new PackageManagerInternal.PackageListObserver() { // from class: com.android.server.policy.PermissionPolicyService.1
            @Override // android.content.p000pm.PackageManagerInternal.PackageListObserver
            public void onPackageAdded(String str, int i) {
                int[] userIds;
                for (int i2 : ((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).getUserIds()) {
                    if (PermissionPolicyService.this.isStarted(i2)) {
                        PermissionPolicyService.this.synchronizePackagePermissionsAndAppOpsForUser(str, i2);
                    }
                }
            }

            @Override // android.content.p000pm.PackageManagerInternal.PackageListObserver
            public void onPackageChanged(String str, int i) {
                int[] userIds;
                for (int i2 : ((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).getUserIds()) {
                    if (PermissionPolicyService.this.isStarted(i2)) {
                        PermissionPolicyService.this.synchronizePackagePermissionsAndAppOpsForUser(str, i2);
                        PermissionPolicyService.this.resetAppOpPermissionsIfNotRequestedForUid(UserHandle.getUid(i2, i));
                    }
                }
            }

            @Override // android.content.p000pm.PackageManagerInternal.PackageListObserver
            public void onPackageRemoved(String str, int i) {
                int[] userIds;
                for (int i2 : ((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).getUserIds()) {
                    if (PermissionPolicyService.this.isStarted(i2)) {
                        PermissionPolicyService.this.resetAppOpPermissionsIfNotRequestedForUid(UserHandle.getUid(i2, i));
                    }
                }
            }
        });
        this.mPermissionManagerInternal.addOnRuntimePermissionStateChangedListener(new PermissionManagerServiceInternal.OnRuntimePermissionStateChangedListener() { // from class: com.android.server.policy.PermissionPolicyService$$ExternalSyntheticLambda2
            @Override // com.android.server.p011pm.permission.PermissionManagerServiceInternal.OnRuntimePermissionStateChangedListener
            public final void onRuntimePermissionStateChanged(String str, int i) {
                PermissionPolicyService.this.synchronizePackagePermissionsAndAppOpsAsyncForUser(str, i);
            }
        });
        this.mAppOpsCallback = new IAppOpsCallback.Stub() { // from class: com.android.server.policy.PermissionPolicyService.2
            public void opChanged(int i, int i2, String str) {
                if (str != null) {
                    PermissionPolicyService.this.synchronizePackagePermissionsAndAppOpsAsyncForUser(str, UserHandle.getUserId(i2));
                }
                PermissionPolicyService.this.resetAppOpPermissionsIfNotRequestedForUidAsync(i2);
            }
        };
        List<PermissionInfo> allPermissionsWithProtection = this.mPermissionManagerInternal.getAllPermissionsWithProtection(1);
        try {
            int size = allPermissionsWithProtection.size();
            for (int i = 0; i < size; i++) {
                PermissionInfo permissionInfo = allPermissionsWithProtection.get(i);
                if (permissionInfo.isRuntime()) {
                    asInterface.startWatchingMode(getSwitchOp(permissionInfo.name), (String) null, this.mAppOpsCallback);
                }
                if (permissionInfo.isSoftRestricted() && (extraAppOpCode = SoftRestrictedPermissionPolicy.forPermission(null, null, null, null, permissionInfo.name).getExtraAppOpCode()) != -1) {
                    asInterface.startWatchingMode(extraAppOpCode, (String) null, this.mAppOpsCallback);
                }
            }
        } catch (RemoteException unused) {
            Slog.wtf(LOG_TAG, "Cannot set up app-ops listener");
        }
        List<PermissionInfo> allPermissionsWithProtectionFlags = this.mPermissionManagerInternal.getAllPermissionsWithProtectionFlags(64);
        this.mAppOpPermissions = new ArrayList();
        int size2 = allPermissionsWithProtectionFlags.size();
        for (int i2 = 0; i2 < size2; i2++) {
            PermissionInfo permissionInfo2 = allPermissionsWithProtectionFlags.get(i2);
            String str = permissionInfo2.name;
            str.hashCode();
            switch (str.hashCode()) {
                case 309844284:
                    if (str.equals("android.permission.MANAGE_IPSEC_TUNNELS")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 1353874541:
                    if (str.equals("android.permission.ACCESS_NOTIFICATIONS")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 1777263169:
                    if (str.equals("android.permission.REQUEST_INSTALL_PACKAGES")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                case 1:
                case 2:
                    break;
                default:
                    int permissionToOpCode = AppOpsManager.permissionToOpCode(permissionInfo2.name);
                    if (permissionToOpCode != -1) {
                        this.mAppOpPermissions.add(permissionInfo2.name);
                        try {
                            asInterface.startWatchingMode(permissionToOpCode, (String) null, this.mAppOpsCallback);
                            break;
                        } catch (RemoteException e) {
                            Slog.wtf(LOG_TAG, "Cannot set up app-ops listener", e);
                            break;
                        }
                    } else {
                        break;
                    }
            }
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addDataScheme("package");
        getContext().registerReceiverAsUser(new BroadcastReceiver() { // from class: com.android.server.policy.PermissionPolicyService.3
            public final List<Integer> mUserSetupUids = new ArrayList(200);
            public final Map<UserHandle, PermissionControllerManager> mPermControllerManagers = new HashMap();

            /* JADX WARN: Removed duplicated region for block: B:10:0x002f A[RETURN] */
            /* JADX WARN: Removed duplicated region for block: B:11:0x0030  */
            @Override // android.content.BroadcastReceiver
            /*
                Code decompiled incorrectly, please refer to instructions dump.
            */
            public void onReceive(Context context, Intent intent) {
                boolean z;
                int intExtra;
                ContentResolver contentResolver;
                try {
                    contentResolver = PermissionPolicyService.this.getContext().getContentResolver();
                } catch (Settings.SettingNotFoundException unused2) {
                }
                if (Settings.Secure.getIntForUser(contentResolver, "user_setup_complete", contentResolver.getUserId()) == 0) {
                    z = false;
                    intExtra = intent.getIntExtra("android.intent.extra.UID", -1);
                    if (PermissionPolicyService.this.mPackageManagerInternal.getPackage(intExtra) != null) {
                        return;
                    }
                    if (z) {
                        if (!this.mUserSetupUids.isEmpty()) {
                            synchronized (this.mUserSetupUids) {
                                for (int size3 = this.mUserSetupUids.size() - 1; size3 >= 0; size3--) {
                                    updateUid(this.mUserSetupUids.get(size3).intValue());
                                }
                                this.mUserSetupUids.clear();
                            }
                        }
                        updateUid(intExtra);
                        return;
                    }
                    synchronized (this.mUserSetupUids) {
                        if (!this.mUserSetupUids.contains(Integer.valueOf(intExtra))) {
                            this.mUserSetupUids.add(Integer.valueOf(intExtra));
                        }
                    }
                    return;
                }
                z = true;
                intExtra = intent.getIntExtra("android.intent.extra.UID", -1);
                if (PermissionPolicyService.this.mPackageManagerInternal.getPackage(intExtra) != null) {
                }
            }

            public final void updateUid(int i3) {
                UserHandle userHandleForUid = UserHandle.getUserHandleForUid(i3);
                PermissionControllerManager permissionControllerManager = this.mPermControllerManagers.get(userHandleForUid);
                if (permissionControllerManager == null) {
                    permissionControllerManager = new PermissionControllerManager(PermissionPolicyService.getUserContext(PermissionPolicyService.this.getContext(), userHandleForUid), PermissionThread.getHandler());
                    this.mPermControllerManagers.put(userHandleForUid, permissionControllerManager);
                }
                permissionControllerManager.updateUserSensitiveForApp(i3);
            }
        }, UserHandle.ALL, intentFilter, null, null);
        final PermissionControllerManager permissionControllerManager = new PermissionControllerManager(getUserContext(getContext(), Process.myUserHandle()), PermissionThread.getHandler());
        PermissionThread.getHandler().postDelayed(new Runnable() { // from class: com.android.server.policy.PermissionPolicyService$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                permissionControllerManager.updateUserSensitive();
            }
        }, 60000L);
    }

    public static int getSwitchOp(String str) {
        int permissionToOpCode = AppOpsManager.permissionToOpCode(str);
        if (permissionToOpCode == -1) {
            return -1;
        }
        return AppOpsManager.opToSwitch(permissionToOpCode);
    }

    public final void synchronizePackagePermissionsAndAppOpsAsyncForUser(String str, int i) {
        if (isStarted(i)) {
            synchronized (this.mLock) {
                if (this.mIsPackageSyncsScheduled.add(UserPackage.of(i, str))) {
                    FgThread.getHandler().sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.policy.PermissionPolicyService$$ExternalSyntheticLambda5
                        public final void accept(Object obj, Object obj2, Object obj3) {
                            ((PermissionPolicyService) obj).synchronizePackagePermissionsAndAppOpsForUser((String) obj2, ((Integer) obj3).intValue());
                        }
                    }, this, str, Integer.valueOf(i)));
                }
            }
        }
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        int[] userIds;
        if (i == 520) {
            registerCarrierPrivilegesCallbacks();
            this.mContext.registerReceiver(this.mSimConfigBroadcastReceiver, new IntentFilter("android.telephony.action.MULTI_SIM_CONFIG_CHANGED"));
        }
        if (i == 550) {
            UserManagerInternal userManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
            for (int i2 : userManagerInternal.getUserIds()) {
                if (userManagerInternal.isUserRunning(i2)) {
                    onStartUser(i2);
                }
            }
        }
        if (i == 550) {
            ((Internal) LocalServices.getService(PermissionPolicyInternal.class)).onActivityManagerReady();
        }
        if (i == 1000) {
            synchronized (this.mLock) {
                this.mBootCompleted = true;
            }
        }
    }

    public final void initTelephonyManagerIfNeeded() {
        if (this.mTelephonyManager == null) {
            this.mTelephonyManager = TelephonyManager.from(this.mContext);
        }
    }

    public final void registerCarrierPrivilegesCallbacks() {
        initTelephonyManagerIfNeeded();
        TelephonyManager telephonyManager = this.mTelephonyManager;
        if (telephonyManager == null) {
            return;
        }
        int activeModemCount = telephonyManager.getActiveModemCount();
        for (int i = 0; i < activeModemCount; i++) {
            PhoneCarrierPrivilegesCallback phoneCarrierPrivilegesCallback = new PhoneCarrierPrivilegesCallback(i);
            this.mPhoneCarrierPrivilegesCallbacks.add(phoneCarrierPrivilegesCallback);
            this.mTelephonyManager.registerCarrierPrivilegesCallback(i, this.mContext.getMainExecutor(), phoneCarrierPrivilegesCallback);
        }
    }

    public final void unregisterCarrierPrivilegesCallback() {
        initTelephonyManagerIfNeeded();
        if (this.mTelephonyManager == null) {
            return;
        }
        for (int i = 0; i < this.mPhoneCarrierPrivilegesCallbacks.size(); i++) {
            PhoneCarrierPrivilegesCallback phoneCarrierPrivilegesCallback = this.mPhoneCarrierPrivilegesCallbacks.get(i);
            if (phoneCarrierPrivilegesCallback != null) {
                this.mTelephonyManager.unregisterCarrierPrivilegesCallback(phoneCarrierPrivilegesCallback);
            }
        }
        this.mPhoneCarrierPrivilegesCallbacks.clear();
    }

    /* loaded from: classes2.dex */
    public final class PhoneCarrierPrivilegesCallback implements TelephonyManager.CarrierPrivilegesCallback {
        public int mPhoneId;

        public PhoneCarrierPrivilegesCallback(int i) {
            this.mPhoneId = i;
        }

        public void onCarrierPrivilegesChanged(Set<String> set, Set<Integer> set2) {
            PermissionPolicyService.this.initTelephonyManagerIfNeeded();
            if (PermissionPolicyService.this.mTelephonyManager == null) {
                Log.e(PermissionPolicyService.LOG_TAG, "Cannot grant default permissions to Carrier Service app. TelephonyManager is null");
                return;
            }
            String carrierServicePackageNameForLogicalSlot = PermissionPolicyService.this.mTelephonyManager.getCarrierServicePackageNameForLogicalSlot(this.mPhoneId);
            if (carrierServicePackageNameForLogicalSlot == null) {
                return;
            }
            int[] userIds = ((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).getUserIds();
            LegacyPermissionManager legacyPermissionManager = (LegacyPermissionManager) PermissionPolicyService.this.mContext.getSystemService(LegacyPermissionManager.class);
            for (int i = 0; i < userIds.length; i++) {
                try {
                    PermissionPolicyService.this.mPackageManager.getPackageInfoAsUser(carrierServicePackageNameForLogicalSlot, 0, userIds[i]);
                    legacyPermissionManager.grantDefaultPermissionsToCarrierServiceApp(carrierServicePackageNameForLogicalSlot, userIds[i]);
                } catch (PackageManager.NameNotFoundException unused) {
                }
            }
        }
    }

    public final boolean isStarted(int i) {
        boolean z;
        synchronized (this.mLock) {
            z = this.mIsStarted.get(i);
        }
        return z;
    }

    @Override // com.android.server.SystemService
    public void onUserStarting(SystemService.TargetUser targetUser) {
        onStartUser(targetUser.getUserIdentifier());
    }

    public final void onStartUser(int i) {
        PermissionPolicyInternal.OnInitializedCallback onInitializedCallback;
        if (isStarted(i)) {
            return;
        }
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog();
        timingsTraceAndSlog.traceBegin("Permission_grant_default_permissions-" + i);
        grantOrUpgradeDefaultRuntimePermissionsIfNeeded(i);
        timingsTraceAndSlog.traceEnd();
        synchronized (this.mLock) {
            this.mIsStarted.put(i, true);
            onInitializedCallback = this.mOnInitializedCallback;
        }
        timingsTraceAndSlog.traceBegin("Permission_synchronize_permissions-" + i);
        synchronizePermissionsAndAppOpsForUser(i);
        timingsTraceAndSlog.traceEnd();
        if (onInitializedCallback != null) {
            timingsTraceAndSlog.traceBegin("Permission_onInitialized-" + i);
            onInitializedCallback.onInitialized(i);
            timingsTraceAndSlog.traceEnd();
        }
    }

    @Override // com.android.server.SystemService
    public void onUserStopping(SystemService.TargetUser targetUser) {
        synchronized (this.mLock) {
            this.mIsStarted.delete(targetUser.getUserIdentifier());
        }
    }

    public final void grantOrUpgradeDefaultRuntimePermissionsIfNeeded(final int i) {
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog();
        PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        PermissionManagerServiceInternal permissionManagerServiceInternal = (PermissionManagerServiceInternal) LocalServices.getService(PermissionManagerServiceInternal.class);
        if (packageManagerInternal.isPermissionUpgradeNeeded(i)) {
            final AndroidFuture androidFuture = new AndroidFuture();
            PermissionControllerManager permissionControllerManager = new PermissionControllerManager(getUserContext(getContext(), UserHandle.of(i)), PermissionThread.getHandler());
            permissionControllerManager.grantOrUpgradeDefaultRuntimePermissions(PermissionThread.getExecutor(), new Consumer() { // from class: com.android.server.policy.PermissionPolicyService$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    PermissionPolicyService.lambda$grantOrUpgradeDefaultRuntimePermissionsIfNeeded$0(androidFuture, i, (Boolean) obj);
                }
            });
            try {
                try {
                    timingsTraceAndSlog.traceBegin("Permission_callback_waiting-" + i);
                    androidFuture.get();
                    timingsTraceAndSlog.traceEnd();
                    permissionControllerManager.updateUserSensitive();
                    packageManagerInternal.updateRuntimePermissionsFingerprint(i);
                } catch (InterruptedException | ExecutionException e) {
                    throw new IllegalStateException(e);
                }
            } catch (Throwable th) {
                timingsTraceAndSlog.traceEnd();
                throw th;
            }
        }
    }

    public static /* synthetic */ void lambda$grantOrUpgradeDefaultRuntimePermissionsIfNeeded$0(AndroidFuture androidFuture, int i, Boolean bool) {
        if (bool.booleanValue()) {
            androidFuture.complete((Object) null);
            return;
        }
        String str = "Error granting/upgrading runtime permissions for user " + i;
        Slog.wtf(LOG_TAG, str);
        androidFuture.completeExceptionally(new IllegalStateException(str));
    }

    public static Context getUserContext(Context context, UserHandle userHandle) {
        if (context.getUser().equals(userHandle)) {
            return context;
        }
        try {
            return context.createPackageContextAsUser(context.getPackageName(), 0, userHandle);
        } catch (PackageManager.NameNotFoundException e) {
            String str = LOG_TAG;
            Slog.e(str, "Cannot create context for user " + userHandle, e);
            return null;
        }
    }

    public final void synchronizePackagePermissionsAndAppOpsForUser(String str, int i) {
        synchronized (this.mLock) {
            this.mIsPackageSyncsScheduled.remove(UserPackage.of(i, str));
        }
        PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        PackageInfo packageInfo = packageManagerInternal.getPackageInfo(str, 0L, 1000, i);
        if (packageInfo == null) {
            return;
        }
        PermissionToOpSynchroniser permissionToOpSynchroniser = new PermissionToOpSynchroniser(getUserContext(getContext(), UserHandle.of(i)));
        permissionToOpSynchroniser.addPackage(packageInfo.packageName);
        String[] sharedUserPackagesForPackage = packageManagerInternal.getSharedUserPackagesForPackage(packageInfo.packageName, i);
        for (String str2 : sharedUserPackagesForPackage) {
            AndroidPackage androidPackage = packageManagerInternal.getPackage(str2);
            if (androidPackage != null) {
                permissionToOpSynchroniser.addPackage(androidPackage.getPackageName());
            }
        }
        permissionToOpSynchroniser.syncPackages();
    }

    public final void synchronizePermissionsAndAppOpsForUser(int i) {
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog();
        final PermissionToOpSynchroniser permissionToOpSynchroniser = new PermissionToOpSynchroniser(getUserContext(getContext(), UserHandle.of(i)));
        timingsTraceAndSlog.traceBegin("Permission_synchronize_addPackages-" + i);
        ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).forEachPackage(new Consumer() { // from class: com.android.server.policy.PermissionPolicyService$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PermissionPolicyService.lambda$synchronizePermissionsAndAppOpsForUser$1(PermissionPolicyService.PermissionToOpSynchroniser.this, (AndroidPackage) obj);
            }
        });
        timingsTraceAndSlog.traceEnd();
        timingsTraceAndSlog.traceBegin("Permission_syncPackages-" + i);
        permissionToOpSynchroniser.syncPackages();
        timingsTraceAndSlog.traceEnd();
    }

    public static /* synthetic */ void lambda$synchronizePermissionsAndAppOpsForUser$1(PermissionToOpSynchroniser permissionToOpSynchroniser, AndroidPackage androidPackage) {
        permissionToOpSynchroniser.addPackage(androidPackage.getPackageName());
    }

    public final void resetAppOpPermissionsIfNotRequestedForUidAsync(int i) {
        if (isStarted(UserHandle.getUserId(i))) {
            synchronized (this.mLock) {
                if (!this.mIsUidSyncScheduled.get(i)) {
                    this.mIsUidSyncScheduled.put(i, true);
                    PermissionThread.getHandler().sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.policy.PermissionPolicyService$$ExternalSyntheticLambda4
                        @Override // java.util.function.BiConsumer
                        public final void accept(Object obj, Object obj2) {
                            ((PermissionPolicyService) obj).resetAppOpPermissionsIfNotRequestedForUid(((Integer) obj2).intValue());
                        }
                    }, this, Integer.valueOf(i)));
                }
            }
        }
    }

    public final void resetAppOpPermissionsIfNotRequestedForUid(int i) {
        int i2;
        int i3;
        int i4;
        String[] strArr;
        synchronized (this.mLock) {
            this.mIsUidSyncScheduled.delete(i);
        }
        Context context = getContext();
        PackageManager packageManager = getUserContext(context, UserHandle.getUserHandleForUid(i)).getPackageManager();
        String[] packagesForUid = packageManager.getPackagesForUid(i);
        if (packagesForUid == null || packagesForUid.length == 0) {
            return;
        }
        ArraySet arraySet = new ArraySet();
        for (String str : packagesForUid) {
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(str, IInstalld.FLAG_USE_QUOTA);
                if (packageInfo != null && (strArr = packageInfo.requestedPermissions) != null) {
                    Collections.addAll(arraySet, strArr);
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        AppOpsManagerInternal appOpsManagerInternal = (AppOpsManagerInternal) LocalServices.getService(AppOpsManagerInternal.class);
        int size = this.mAppOpPermissions.size();
        for (int i5 = 0; i5 < size; i5++) {
            String str2 = this.mAppOpPermissions.get(i5);
            if (!arraySet.contains(str2)) {
                int permissionToOpCode = AppOpsManager.permissionToOpCode(str2);
                int opToDefaultMode = AppOpsManager.opToDefaultMode(permissionToOpCode);
                int length = packagesForUid.length;
                int i6 = 0;
                while (i6 < length) {
                    String str3 = packagesForUid[i6];
                    if (appOpsManager.unsafeCheckOpRawNoThrow(permissionToOpCode, i, str3) != opToDefaultMode) {
                        appOpsManagerInternal.setUidModeFromPermissionPolicy(permissionToOpCode, i, opToDefaultMode, this.mAppOpsCallback);
                        i2 = i6;
                        i3 = length;
                        i4 = opToDefaultMode;
                        appOpsManagerInternal.setModeFromPermissionPolicy(permissionToOpCode, i, str3, opToDefaultMode, this.mAppOpsCallback);
                    } else {
                        i2 = i6;
                        i3 = length;
                        i4 = opToDefaultMode;
                    }
                    i6 = i2 + 1;
                    length = i3;
                    opToDefaultMode = i4;
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public class PermissionToOpSynchroniser {
        public final AppOpsManager mAppOpsManager;
        public final Context mContext;
        public final PackageManager mPackageManager;
        public final ArrayList<OpToChange> mOpsToAllow = new ArrayList<>();
        public final ArrayList<OpToChange> mOpsToIgnore = new ArrayList<>();
        public final ArrayList<OpToChange> mOpsToIgnoreIfNotAllowed = new ArrayList<>();
        public final ArrayList<OpToChange> mOpsToForeground = new ArrayList<>();
        public final AppOpsManagerInternal mAppOpsManagerInternal = (AppOpsManagerInternal) LocalServices.getService(AppOpsManagerInternal.class);
        public final ArrayMap<String, PermissionInfo> mRuntimeAndTheirBgPermissionInfos = new ArrayMap<>();

        public PermissionToOpSynchroniser(Context context) {
            this.mContext = context;
            this.mPackageManager = context.getPackageManager();
            this.mAppOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
            List<PermissionInfo> allPermissionsWithProtection = ((PermissionManagerServiceInternal) LocalServices.getService(PermissionManagerServiceInternal.class)).getAllPermissionsWithProtection(1);
            int size = allPermissionsWithProtection.size();
            for (int i = 0; i < size; i++) {
                PermissionInfo permissionInfo = allPermissionsWithProtection.get(i);
                this.mRuntimeAndTheirBgPermissionInfos.put(permissionInfo.name, permissionInfo);
                String str = permissionInfo.backgroundPermission;
                if (str != null) {
                    int i2 = 0;
                    while (true) {
                        if (i2 >= size) {
                            break;
                        } else if (permissionInfo.backgroundPermission.equals(allPermissionsWithProtection.get(i2).name)) {
                            str = null;
                            break;
                        } else {
                            i2++;
                        }
                    }
                    if (str != null) {
                        try {
                            PermissionInfo permissionInfo2 = this.mPackageManager.getPermissionInfo(str, 0);
                            this.mRuntimeAndTheirBgPermissionInfos.put(permissionInfo2.name, permissionInfo2);
                        } catch (PackageManager.NameNotFoundException unused) {
                            Slog.w(PermissionPolicyService.LOG_TAG, "Unknown background permission: " + str);
                        }
                    }
                }
            }
        }

        public final void syncPackages() {
            LongSparseLongArray longSparseLongArray = new LongSparseLongArray();
            int size = this.mOpsToAllow.size();
            for (int i = 0; i < size; i++) {
                OpToChange opToChange = this.mOpsToAllow.get(i);
                setUidModeAllowed(opToChange.code, opToChange.uid, opToChange.packageName);
                longSparseLongArray.put(IntPair.of(opToChange.uid, opToChange.code), 1L);
            }
            int size2 = this.mOpsToForeground.size();
            for (int i2 = 0; i2 < size2; i2++) {
                OpToChange opToChange2 = this.mOpsToForeground.get(i2);
                if (longSparseLongArray.indexOfKey(IntPair.of(opToChange2.uid, opToChange2.code)) < 0) {
                    setUidModeForeground(opToChange2.code, opToChange2.uid, opToChange2.packageName);
                    longSparseLongArray.put(IntPair.of(opToChange2.uid, opToChange2.code), 1L);
                }
            }
            int size3 = this.mOpsToIgnore.size();
            for (int i3 = 0; i3 < size3; i3++) {
                OpToChange opToChange3 = this.mOpsToIgnore.get(i3);
                if (longSparseLongArray.indexOfKey(IntPair.of(opToChange3.uid, opToChange3.code)) < 0) {
                    setUidModeIgnored(opToChange3.code, opToChange3.uid, opToChange3.packageName);
                    longSparseLongArray.put(IntPair.of(opToChange3.uid, opToChange3.code), 1L);
                }
            }
            int size4 = this.mOpsToIgnoreIfNotAllowed.size();
            for (int i4 = 0; i4 < size4; i4++) {
                OpToChange opToChange4 = this.mOpsToIgnoreIfNotAllowed.get(i4);
                if (longSparseLongArray.indexOfKey(IntPair.of(opToChange4.uid, opToChange4.code)) < 0 && setUidModeIgnoredIfNotAllowed(opToChange4.code, opToChange4.uid, opToChange4.packageName)) {
                    longSparseLongArray.put(IntPair.of(opToChange4.uid, opToChange4.code), 1L);
                }
            }
        }

        public final void addAppOps(PackageInfo packageInfo, AndroidPackage androidPackage, String str) {
            PermissionInfo permissionInfo = this.mRuntimeAndTheirBgPermissionInfos.get(str);
            if (permissionInfo == null) {
                return;
            }
            addPermissionAppOp(packageInfo, androidPackage, permissionInfo);
            addExtraAppOp(packageInfo, androidPackage, permissionInfo);
        }

        public final void addPermissionAppOp(PackageInfo packageInfo, AndroidPackage androidPackage, PermissionInfo permissionInfo) {
            int switchOp;
            if (permissionInfo.isRuntime()) {
                String str = permissionInfo.name;
                String str2 = packageInfo.packageName;
                UserHandle.getUserHandleForUid(packageInfo.applicationInfo.uid);
                boolean z = false;
                if (((this.mPackageManager.getPermissionFlags(str, str2, this.mContext.getUser()) & 64) != 0) || (switchOp = PermissionPolicyService.getSwitchOp(str)) == -1) {
                    return;
                }
                if (shouldGrantAppOp(packageInfo, androidPackage, permissionInfo)) {
                    String str3 = permissionInfo.backgroundPermission;
                    if (str3 != null) {
                        PermissionInfo permissionInfo2 = this.mRuntimeAndTheirBgPermissionInfos.get(str3);
                        if (!(permissionInfo2 != null && shouldGrantAppOp(packageInfo, androidPackage, permissionInfo2))) {
                            z = true;
                        }
                    }
                } else {
                    z = true;
                }
                OpToChange opToChange = new OpToChange(packageInfo.applicationInfo.uid, str2, switchOp);
                if (!z) {
                    this.mOpsToAllow.add(opToChange);
                } else if (z) {
                    this.mOpsToIgnore.add(opToChange);
                } else if (!z) {
                } else {
                    this.mOpsToForeground.add(opToChange);
                }
            }
        }

        public final boolean shouldGrantAppOp(PackageInfo packageInfo, AndroidPackage androidPackage, PermissionInfo permissionInfo) {
            String str = permissionInfo.name;
            String str2 = packageInfo.packageName;
            if (this.mPackageManager.checkPermission(str, str2) == 0) {
                int permissionFlags = this.mPackageManager.getPermissionFlags(str, str2, this.mContext.getUser());
                if ((permissionFlags & 8) == 8) {
                    return false;
                }
                if (permissionInfo.isHardRestricted()) {
                    return !((permissionFlags & 16384) == 16384);
                } else if (permissionInfo.isSoftRestricted()) {
                    Context context = this.mContext;
                    return SoftRestrictedPermissionPolicy.forPermission(context, packageInfo.applicationInfo, androidPackage, context.getUser(), str).mayGrantPermission();
                } else {
                    return true;
                }
            }
            return false;
        }

        public final void addExtraAppOp(PackageInfo packageInfo, AndroidPackage androidPackage, PermissionInfo permissionInfo) {
            if (permissionInfo.isSoftRestricted()) {
                String str = permissionInfo.name;
                Context context = this.mContext;
                SoftRestrictedPermissionPolicy forPermission = SoftRestrictedPermissionPolicy.forPermission(context, packageInfo.applicationInfo, androidPackage, context.getUser(), str);
                int extraAppOpCode = forPermission.getExtraAppOpCode();
                if (extraAppOpCode == -1) {
                    return;
                }
                OpToChange opToChange = new OpToChange(packageInfo.applicationInfo.uid, packageInfo.packageName, extraAppOpCode);
                if (forPermission.mayAllowExtraAppOp()) {
                    this.mOpsToAllow.add(opToChange);
                } else if (forPermission.mayDenyExtraAppOpIfGranted()) {
                    this.mOpsToIgnore.add(opToChange);
                } else {
                    this.mOpsToIgnoreIfNotAllowed.add(opToChange);
                }
            }
        }

        public void addPackage(String str) {
            ApplicationInfo applicationInfo;
            String[] strArr;
            int i;
            PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
            try {
                PackageInfo packageInfo = this.mPackageManager.getPackageInfo(str, IInstalld.FLAG_USE_QUOTA);
                AndroidPackage androidPackage = packageManagerInternal.getPackage(str);
                if (packageInfo == null || androidPackage == null || (applicationInfo = packageInfo.applicationInfo) == null || (strArr = packageInfo.requestedPermissions) == null || (i = applicationInfo.uid) == 0 || i == 1000) {
                    return;
                }
                for (String str2 : strArr) {
                    addAppOps(packageInfo, androidPackage, str2);
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }

        public final void setUidModeAllowed(int i, int i2, String str) {
            setUidMode(i, i2, 0, str);
        }

        public final void setUidModeForeground(int i, int i2, String str) {
            setUidMode(i, i2, 4, str);
        }

        public final void setUidModeIgnored(int i, int i2, String str) {
            setUidMode(i, i2, 1, str);
        }

        public final boolean setUidModeIgnoredIfNotAllowed(int i, int i2, String str) {
            int unsafeCheckOpRaw = this.mAppOpsManager.unsafeCheckOpRaw(AppOpsManager.opToPublicName(i), i2, str);
            if (unsafeCheckOpRaw != 0) {
                if (unsafeCheckOpRaw != 1) {
                    this.mAppOpsManagerInternal.setUidModeFromPermissionPolicy(i, i2, 1, PermissionPolicyService.this.mAppOpsCallback);
                }
                return true;
            }
            return false;
        }

        public final void setUidMode(int i, int i2, int i3, String str) {
            if (this.mAppOpsManager.unsafeCheckOpRaw(AppOpsManager.opToPublicName(i), i2, str) != i3) {
                this.mAppOpsManagerInternal.setUidModeFromPermissionPolicy(i, i2, i3, PermissionPolicyService.this.mAppOpsCallback);
                if (this.mAppOpsManager.unsafeCheckOpRaw(AppOpsManager.opToPublicName(i), i2, str) != i3) {
                    this.mAppOpsManagerInternal.setModeFromPermissionPolicy(i, i2, str, AppOpsManager.opToDefaultMode(i), PermissionPolicyService.this.mAppOpsCallback);
                }
            }
        }

        /* loaded from: classes2.dex */
        public class OpToChange {
            public final int code;
            public final String packageName;
            public final int uid;

            public OpToChange(int i, String str, int i2) {
                this.uid = i;
                this.packageName = str;
                this.code = i2;
            }
        }
    }

    /* loaded from: classes2.dex */
    public class Internal extends PermissionPolicyInternal {
        public final ActivityInterceptorCallback mActivityInterceptorCallback;

        public Internal() {
            this.mActivityInterceptorCallback = new C14271();
        }

        /* renamed from: com.android.server.policy.PermissionPolicyService$Internal$1 */
        /* loaded from: classes2.dex */
        public class C14271 implements ActivityInterceptorCallback {
            @Override // com.android.server.p014wm.ActivityInterceptorCallback
            public ActivityInterceptorCallback.ActivityInterceptResult onInterceptActivityLaunch(ActivityInterceptorCallback.ActivityInterceptorInfo activityInterceptorInfo) {
                return null;
            }

            public C14271() {
            }

            @Override // com.android.server.p014wm.ActivityInterceptorCallback
            public void onActivityLaunched(final TaskInfo taskInfo, final ActivityInfo activityInfo, final ActivityInterceptorCallback.ActivityInterceptorInfo activityInterceptorInfo) {
                if (!Internal.this.shouldShowNotificationDialogOrClearFlags(taskInfo, activityInfo.packageName, activityInterceptorInfo.getCallingPackage(), activityInterceptorInfo.getIntent(), activityInterceptorInfo.getCheckedOptions(), activityInfo.name, true) || Internal.this.isNoDisplayActivity(activityInfo)) {
                    return;
                }
                if (CompatChanges.isChangeEnabled(194833441L, activityInfo.packageName, UserHandle.of(taskInfo.userId))) {
                    return;
                }
                PermissionPolicyService.this.mHandler.post(new Runnable() { // from class: com.android.server.policy.PermissionPolicyService$Internal$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PermissionPolicyService.Internal.C14271.this.lambda$onActivityLaunched$0(activityInfo, taskInfo, activityInterceptorInfo);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onActivityLaunched$0(ActivityInfo activityInfo, TaskInfo taskInfo, ActivityInterceptorCallback.ActivityInterceptorInfo activityInterceptorInfo) {
                Internal.this.showNotificationPromptIfNeeded(activityInfo.packageName, taskInfo.userId, taskInfo.taskId, activityInterceptorInfo);
            }
        }

        public final void onActivityManagerReady() {
            ((ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class)).registerActivityStartInterceptor(1, this.mActivityInterceptorCallback);
        }

        @Override // com.android.server.policy.PermissionPolicyInternal
        public boolean checkStartActivity(Intent intent, int i, String str) {
            if (str != null && isActionRemovedForCallingPackage(intent, i, str)) {
                String str2 = PermissionPolicyService.LOG_TAG;
                Slog.w(str2, "Action Removed: starting " + intent.toString() + " from " + str + " (uid=" + i + ")");
                return false;
            } else if ("android.content.pm.action.REQUEST_PERMISSIONS_FOR_OTHER".equals(intent.getAction())) {
                return i == 1000 && PackageManagerShellCommandDataLoader.PACKAGE.equals(str);
            } else {
                return true;
            }
        }

        @Override // com.android.server.policy.PermissionPolicyInternal
        public void showNotificationPromptIfNeeded(String str, int i, int i2) {
            showNotificationPromptIfNeeded(str, i, i2, null);
        }

        public void showNotificationPromptIfNeeded(String str, int i, int i2, ActivityInterceptorCallback.ActivityInterceptorInfo activityInterceptorInfo) {
            UserHandle of = UserHandle.of(i);
            if (str == null || i2 == -1 || !shouldForceShowNotificationPermissionRequest(str, of)) {
                return;
            }
            launchNotificationPermissionRequestDialog(str, of, i2, activityInterceptorInfo);
        }

        @Override // com.android.server.policy.PermissionPolicyInternal
        public boolean isIntentToPermissionDialog(Intent intent) {
            return Objects.equals(intent.getPackage(), PermissionPolicyService.this.mPackageManager.getPermissionControllerPackageName()) && (Objects.equals(intent.getAction(), "android.content.pm.action.REQUEST_PERMISSIONS_FOR_OTHER") || Objects.equals(intent.getAction(), "android.content.pm.action.REQUEST_PERMISSIONS"));
        }

        @Override // com.android.server.policy.PermissionPolicyInternal
        public boolean shouldShowNotificationDialogForTask(TaskInfo taskInfo, String str, String str2, Intent intent, String str3) {
            return shouldShowNotificationDialogOrClearFlags(taskInfo, str, str2, intent, null, str3, false);
        }

        public final boolean isNoDisplayActivity(ActivityInfo activityInfo) {
            AttributeCache.Entry entry;
            int themeResource = activityInfo.getThemeResource();
            if (themeResource == 0 || (entry = AttributeCache.instance().get(activityInfo.packageName, themeResource, R.styleable.Window, 0)) == null) {
                return false;
            }
            return entry.array.getBoolean(10, false);
        }

        public final boolean shouldShowNotificationDialogOrClearFlags(TaskInfo taskInfo, String str, String str2, Intent intent, ActivityOptions activityOptions, String str3, boolean z) {
            if (intent == null || str == null || taskInfo == null || str3 == null) {
                return false;
            }
            if ((taskInfo.isFocused && taskInfo.isVisible && taskInfo.isRunning) || z) {
                if (!isLauncherIntent(intent) && ((activityOptions == null || !activityOptions.isEligibleForLegacyPermissionPrompt()) && !isTaskStartedFromLauncher(str, taskInfo))) {
                    if (!isTaskPotentialTrampoline(str3, str, str2, taskInfo, intent)) {
                        return false;
                    }
                    if (z && !pkgHasRunningLauncherTask(str, taskInfo)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

        public final boolean isTaskPotentialTrampoline(String str, String str2, String str3, TaskInfo taskInfo, Intent intent) {
            return str2.equals(str3) && taskInfo.baseIntent.filterEquals(intent) && taskInfo.numActivities == 1 && str.equals(taskInfo.topActivityInfo.name);
        }

        public final boolean pkgHasRunningLauncherTask(String str, TaskInfo taskInfo) {
            try {
                List<ActivityManager.AppTask> appTasks = ((ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class)).getAppTasks(str, PermissionPolicyService.this.mPackageManager.getPackageUid(str, 0));
                for (int i = 0; i < appTasks.size(); i++) {
                    ActivityManager.RecentTaskInfo taskInfo2 = appTasks.get(i).getTaskInfo();
                    if (((TaskInfo) taskInfo2).taskId != taskInfo.taskId && ((TaskInfo) taskInfo2).isFocused && ((TaskInfo) taskInfo2).isRunning && isTaskStartedFromLauncher(str, taskInfo2)) {
                        return true;
                    }
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
            return false;
        }

        public final boolean isLauncherIntent(Intent intent) {
            return "android.intent.action.MAIN".equals(intent.getAction()) && intent.getCategories() != null && (intent.getCategories().contains("android.intent.category.LAUNCHER") || intent.getCategories().contains("android.intent.category.LEANBACK_LAUNCHER") || intent.getCategories().contains("android.intent.category.CAR_LAUNCHER"));
        }

        public final boolean isTaskStartedFromLauncher(String str, TaskInfo taskInfo) {
            return str.equals(taskInfo.baseActivity.getPackageName()) && isLauncherIntent(taskInfo.baseIntent);
        }

        public final void launchNotificationPermissionRequestDialog(String str, UserHandle userHandle, int i, ActivityInterceptorCallback.ActivityInterceptorInfo activityInterceptorInfo) {
            ActivityOptions activityOptions;
            Intent buildRequestPermissionsIntent = PermissionPolicyService.this.mPackageManager.buildRequestPermissionsIntent(new String[]{"android.permission.POST_NOTIFICATIONS"});
            buildRequestPermissionsIntent.addFlags(268697600);
            buildRequestPermissionsIntent.setAction("android.content.pm.action.REQUEST_PERMISSIONS_FOR_OTHER");
            buildRequestPermissionsIntent.putExtra("android.intent.extra.PACKAGE_NAME", str);
            boolean z = (activityInterceptorInfo == null || activityInterceptorInfo.getCheckedOptions() == null || activityInterceptorInfo.getCheckedOptions().getAnimationType() != 13 || activityInterceptorInfo.getClearOptionsAnimationRunnable() == null) ? false : true;
            if (z) {
                activityOptions = ActivityOptions.makeRemoteAnimation(activityInterceptorInfo.getCheckedOptions().getRemoteAnimationAdapter(), activityInterceptorInfo.getCheckedOptions().getRemoteTransition());
            } else {
                activityOptions = new ActivityOptions(new Bundle());
            }
            activityOptions.setTaskOverlay(true, false);
            activityOptions.setLaunchTaskId(i);
            if (z) {
                activityInterceptorInfo.getClearOptionsAnimationRunnable().run();
            }
            try {
                PermissionPolicyService.this.mContext.startActivityAsUser(buildRequestPermissionsIntent, activityOptions.toBundle(), userHandle);
            } catch (Exception e) {
                String str2 = PermissionPolicyService.LOG_TAG;
                Log.e(str2, "couldn't start grant permission dialogfor other package " + str, e);
            }
        }

        @Override // com.android.server.policy.PermissionPolicyInternal
        public boolean isInitialized(int i) {
            return PermissionPolicyService.this.isStarted(i);
        }

        @Override // com.android.server.policy.PermissionPolicyInternal
        public void setOnInitializedCallback(PermissionPolicyInternal.OnInitializedCallback onInitializedCallback) {
            synchronized (PermissionPolicyService.this.mLock) {
                PermissionPolicyService.this.mOnInitializedCallback = onInitializedCallback;
            }
        }

        public final boolean isActionRemovedForCallingPackage(Intent intent, int i, String str) {
            String action = intent.getAction();
            if (action == null) {
                return false;
            }
            if (action.equals("android.provider.Telephony.ACTION_CHANGE_DEFAULT") || action.equals("android.telecom.action.CHANGE_DEFAULT_DIALER")) {
                try {
                    if (PermissionPolicyService.this.getContext().getPackageManager().getApplicationInfoAsUser(str, 0, UserHandle.getUserId(i)).targetSdkVersion >= 29) {
                        return true;
                    }
                } catch (PackageManager.NameNotFoundException unused) {
                    String str2 = PermissionPolicyService.LOG_TAG;
                    Slog.i(str2, "Cannot find application info for " + str);
                }
                intent.putExtra("android.intent.extra.CALLING_PACKAGE", str);
                return false;
            }
            return false;
        }

        public final boolean shouldForceShowNotificationPermissionRequest(String str, UserHandle userHandle) {
            AndroidPackage androidPackage = PermissionPolicyService.this.mPackageManagerInternal.getPackage(str);
            if (androidPackage == null || androidPackage.getPackageName() == null || Objects.equals(str, PermissionPolicyService.this.mPackageManager.getPermissionControllerPackageName()) || androidPackage.getTargetSdkVersion() < 23) {
                if (androidPackage == null) {
                    String str2 = PermissionPolicyService.LOG_TAG;
                    Slog.w(str2, "Cannot check for Notification prompt, no package for " + str);
                }
                return false;
            }
            synchronized (PermissionPolicyService.this.mLock) {
                if (PermissionPolicyService.this.mBootCompleted) {
                    if (!androidPackage.getRequestedPermissions().contains("android.permission.POST_NOTIFICATIONS") || CompatChanges.isChangeEnabled(194833441L, str, userHandle) || PermissionPolicyService.this.mKeyguardManager.isKeyguardLocked()) {
                        return false;
                    }
                    int uid = userHandle.getUid(androidPackage.getUid());
                    if (PermissionPolicyService.this.mNotificationManager == null) {
                        PermissionPolicyService.this.mNotificationManager = (NotificationManagerInternal) LocalServices.getService(NotificationManagerInternal.class);
                    }
                    return ((PermissionPolicyService.this.mPermissionManagerInternal.checkUidPermission(uid, "android.permission.POST_NOTIFICATIONS") == 0) || !(PermissionPolicyService.this.mNotificationManager.getNumNotificationChannelsForPackage(str, uid, true) > 0) || ((PermissionPolicyService.this.mPackageManager.getPermissionFlags("android.permission.POST_NOTIFICATIONS", str, userHandle) & 32823) != 0)) ? false : true;
                }
                return false;
            }
        }
    }
}
