package com.android.server.apphibernation;

import android.app.ActivityManager;
import android.app.ActivityThread;
import android.app.IActivityManager;
import android.app.IApplicationThread;
import android.app.StatsManager;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManagerInternal;
import android.apphibernation.HibernationStats;
import android.apphibernation.IAppHibernationService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCallback;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.DeviceConfig;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import android.util.StatsEvent;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
/* loaded from: classes.dex */
public final class AppHibernationService extends SystemService {
    @VisibleForTesting
    public static boolean sIsServiceEnabled;
    public final Executor mBackgroundExecutor;
    public final BroadcastReceiver mBroadcastReceiver;
    public final Context mContext;
    @GuardedBy({"mLock"})
    public final Map<String, GlobalLevelState> mGlobalHibernationStates;
    public final HibernationStateDiskStore<GlobalLevelState> mGlobalLevelHibernationDiskStore;
    public final IActivityManager mIActivityManager;
    public final IPackageManager mIPackageManager;
    public final Injector mInjector;
    public final AppHibernationManagerInternal mLocalService;
    public final Object mLock;
    public final boolean mOatArtifactDeletionEnabled;
    public final PackageManagerInternal mPackageManagerInternal;
    public final AppHibernationServiceStub mServiceStub;
    public final StorageStatsManager mStorageStatsManager;
    public final UsageStatsManagerInternal.UsageEventListener mUsageEventListener;
    public final SparseArray<HibernationStateDiskStore<UserLevelState>> mUserDiskStores;
    public final UserManager mUserManager;
    @GuardedBy({"mLock"})
    public final SparseArray<Map<String, UserLevelState>> mUserStates;

    /* loaded from: classes.dex */
    public interface Injector {
        IActivityManager getActivityManager();

        Executor getBackgroundExecutor();

        Context getContext();

        HibernationStateDiskStore<GlobalLevelState> getGlobalLevelDiskStore();

        IPackageManager getPackageManager();

        PackageManagerInternal getPackageManagerInternal();

        StorageStatsManager getStorageStatsManager();

        UsageStatsManagerInternal getUsageStatsManagerInternal();

        HibernationStateDiskStore<UserLevelState> getUserLevelDiskStore(int i);

        UserManager getUserManager();

        boolean isOatArtifactDeletionEnabled();
    }

    public AppHibernationService(Context context) {
        this(new InjectorImpl(context));
    }

    @VisibleForTesting
    public AppHibernationService(Injector injector) {
        super(injector.getContext());
        this.mLock = new Object();
        this.mUserStates = new SparseArray<>();
        this.mUserDiskStores = new SparseArray<>();
        this.mGlobalHibernationStates = new ArrayMap();
        LocalService localService = new LocalService(this);
        this.mLocalService = localService;
        this.mServiceStub = new AppHibernationServiceStub(this);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.apphibernation.AppHibernationService.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                int intExtra = intent.getIntExtra("android.intent.extra.user_handle", -10000);
                if (intExtra == -10000) {
                    return;
                }
                String action = intent.getAction();
                if ("android.intent.action.PACKAGE_ADDED".equals(action) || "android.intent.action.PACKAGE_REMOVED".equals(action)) {
                    String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
                    if (intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                        return;
                    }
                    if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
                        AppHibernationService.this.onPackageAdded(schemeSpecificPart, intExtra);
                    } else if ("android.intent.action.PACKAGE_REMOVED".equals(action)) {
                        AppHibernationService.this.onPackageRemoved(schemeSpecificPart, intExtra);
                        if (intent.getBooleanExtra("android.intent.extra.REMOVED_FOR_ALL_USERS", false)) {
                            AppHibernationService.this.onPackageRemovedForAllUsers(schemeSpecificPart);
                        }
                    }
                }
            }
        };
        this.mBroadcastReceiver = broadcastReceiver;
        UsageStatsManagerInternal.UsageEventListener usageEventListener = new UsageStatsManagerInternal.UsageEventListener() { // from class: com.android.server.apphibernation.AppHibernationService$$ExternalSyntheticLambda5
            @Override // android.app.usage.UsageStatsManagerInternal.UsageEventListener
            public final void onUsageEvent(int i, UsageEvents.Event event) {
                AppHibernationService.this.lambda$new$6(i, event);
            }
        };
        this.mUsageEventListener = usageEventListener;
        Context context = injector.getContext();
        this.mContext = context;
        this.mIPackageManager = injector.getPackageManager();
        this.mPackageManagerInternal = injector.getPackageManagerInternal();
        this.mIActivityManager = injector.getActivityManager();
        this.mUserManager = injector.getUserManager();
        this.mStorageStatsManager = injector.getStorageStatsManager();
        this.mGlobalLevelHibernationDiskStore = injector.getGlobalLevelDiskStore();
        this.mBackgroundExecutor = injector.getBackgroundExecutor();
        this.mOatArtifactDeletionEnabled = injector.isOatArtifactDeletionEnabled();
        this.mInjector = injector;
        Context createContextAsUser = context.createContextAsUser(UserHandle.ALL, 0);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme("package");
        createContextAsUser.registerReceiver(broadcastReceiver, intentFilter);
        LocalServices.addService(AppHibernationManagerInternal.class, localService);
        injector.getUsageStatsManagerInternal().registerListener(usageEventListener);
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("app_hibernation", this.mServiceStub);
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 1000) {
            this.mBackgroundExecutor.execute(new Runnable() { // from class: com.android.server.apphibernation.AppHibernationService$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    AppHibernationService.this.lambda$onBootPhase$0();
                }
            });
        }
        if (i == 500) {
            sIsServiceEnabled = isDeviceConfigAppHibernationEnabled();
            DeviceConfig.addOnPropertiesChangedListener("app_hibernation", ActivityThread.currentApplication().getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.apphibernation.AppHibernationService$$ExternalSyntheticLambda7
                public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                    AppHibernationService.this.onDeviceConfigChanged(properties);
                }
            });
            StatsManager statsManager = (StatsManager) getContext().getSystemService(StatsManager.class);
            StatsPullAtomCallbackImpl statsPullAtomCallbackImpl = new StatsPullAtomCallbackImpl();
            statsManager.setPullAtomCallback((int) FrameworkStatsLog.USER_LEVEL_HIBERNATED_APPS, (StatsManager.PullAtomMetadata) null, this.mBackgroundExecutor, statsPullAtomCallbackImpl);
            statsManager.setPullAtomCallback((int) FrameworkStatsLog.GLOBAL_HIBERNATED_APPS, (StatsManager.PullAtomMetadata) null, this.mBackgroundExecutor, statsPullAtomCallbackImpl);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBootPhase$0() {
        List<GlobalLevelState> readHibernationStates = this.mGlobalLevelHibernationDiskStore.readHibernationStates();
        synchronized (this.mLock) {
            initializeGlobalHibernationStates(readHibernationStates);
        }
    }

    public final boolean isOatArtifactDeletionEnabled() {
        getContext().enforceCallingOrSelfPermission("android.permission.MANAGE_APP_HIBERNATION", "Caller does not have MANAGE_APP_HIBERNATION permission.");
        return this.mOatArtifactDeletionEnabled;
    }

    public boolean isHibernatingForUser(String str, int i) {
        if (sIsServiceEnabled) {
            Context context = getContext();
            context.enforceCallingOrSelfPermission("android.permission.MANAGE_APP_HIBERNATION", "Caller did not have permission while calling isHibernatingForUser");
            int handleIncomingUser = handleIncomingUser(i, "isHibernatingForUser");
            synchronized (this.mLock) {
                if (checkUserStatesExist(handleIncomingUser, "isHibernatingForUser", false)) {
                    UserLevelState userLevelState = this.mUserStates.get(handleIncomingUser).get(str);
                    if (userLevelState != null && this.mPackageManagerInternal.canQueryPackage(Binder.getCallingUid(), str)) {
                        return userLevelState.hibernated;
                    }
                    return false;
                }
                return false;
            }
        }
        return false;
    }

    public boolean isHibernatingGlobally(String str) {
        if (sIsServiceEnabled) {
            getContext().enforceCallingOrSelfPermission("android.permission.MANAGE_APP_HIBERNATION", "Caller does not have MANAGE_APP_HIBERNATION permission.");
            synchronized (this.mLock) {
                GlobalLevelState globalLevelState = this.mGlobalHibernationStates.get(str);
                if (globalLevelState != null && this.mPackageManagerInternal.canQueryPackage(Binder.getCallingUid(), str)) {
                    return globalLevelState.hibernated;
                }
                return false;
            }
        }
        return false;
    }

    public void setHibernatingForUser(final String str, int i, boolean z) {
        if (sIsServiceEnabled) {
            getContext().enforceCallingOrSelfPermission("android.permission.MANAGE_APP_HIBERNATION", "Caller does not have MANAGE_APP_HIBERNATION permission.");
            final int handleIncomingUser = handleIncomingUser(i, "setHibernatingForUser");
            synchronized (this.mLock) {
                if (checkUserStatesExist(handleIncomingUser, "setHibernatingForUser", true)) {
                    final UserLevelState userLevelState = this.mUserStates.get(handleIncomingUser).get(str);
                    if (userLevelState != null && this.mPackageManagerInternal.canQueryPackage(Binder.getCallingUid(), str)) {
                        if (userLevelState.hibernated == z) {
                            return;
                        }
                        userLevelState.hibernated = z;
                        if (z) {
                            this.mBackgroundExecutor.execute(new Runnable() { // from class: com.android.server.apphibernation.AppHibernationService$$ExternalSyntheticLambda2
                                @Override // java.lang.Runnable
                                public final void run() {
                                    AppHibernationService.this.lambda$setHibernatingForUser$1(str, handleIncomingUser, userLevelState);
                                }
                            });
                        } else {
                            this.mBackgroundExecutor.execute(new Runnable() { // from class: com.android.server.apphibernation.AppHibernationService$$ExternalSyntheticLambda3
                                @Override // java.lang.Runnable
                                public final void run() {
                                    AppHibernationService.this.lambda$setHibernatingForUser$2(str, handleIncomingUser);
                                }
                            });
                            userLevelState.lastUnhibernatedMs = System.currentTimeMillis();
                        }
                        final UserLevelState userLevelState2 = new UserLevelState(userLevelState);
                        this.mBackgroundExecutor.execute(new Runnable() { // from class: com.android.server.apphibernation.AppHibernationService$$ExternalSyntheticLambda4
                            @Override // java.lang.Runnable
                            public final void run() {
                                AppHibernationService.lambda$setHibernatingForUser$3(UserLevelState.this, handleIncomingUser);
                            }
                        });
                        this.mUserDiskStores.get(handleIncomingUser).scheduleWriteHibernationStates(new ArrayList(this.mUserStates.get(handleIncomingUser).values()));
                        return;
                    }
                    Slog.e("AppHibernationService", TextUtils.formatSimple("Package %s is not installed for user %s", new Object[]{str, Integer.valueOf(handleIncomingUser)}));
                }
            }
        }
    }

    public static /* synthetic */ void lambda$setHibernatingForUser$3(UserLevelState userLevelState, int i) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.USER_LEVEL_HIBERNATION_STATE_CHANGED, userLevelState.packageName, i, userLevelState.hibernated);
    }

    public void setHibernatingGlobally(final String str, boolean z) {
        if (sIsServiceEnabled) {
            getContext().enforceCallingOrSelfPermission("android.permission.MANAGE_APP_HIBERNATION", "Caller does not have MANAGE_APP_HIBERNATION permission.");
            synchronized (this.mLock) {
                final GlobalLevelState globalLevelState = this.mGlobalHibernationStates.get(str);
                if (globalLevelState != null && this.mPackageManagerInternal.canQueryPackage(Binder.getCallingUid(), str)) {
                    if (globalLevelState.hibernated != z) {
                        globalLevelState.hibernated = z;
                        if (z) {
                            this.mBackgroundExecutor.execute(new Runnable() { // from class: com.android.server.apphibernation.AppHibernationService$$ExternalSyntheticLambda0
                                @Override // java.lang.Runnable
                                public final void run() {
                                    AppHibernationService.this.lambda$setHibernatingGlobally$4(str, globalLevelState);
                                }
                            });
                        } else {
                            globalLevelState.savedByte = 0L;
                            globalLevelState.lastUnhibernatedMs = System.currentTimeMillis();
                        }
                        this.mGlobalLevelHibernationDiskStore.scheduleWriteHibernationStates(new ArrayList(this.mGlobalHibernationStates.values()));
                    }
                    return;
                }
                Slog.e("AppHibernationService", TextUtils.formatSimple("Package %s is not installed for any user", new Object[]{str}));
            }
        }
    }

    public List<String> getHibernatingPackagesForUser(int i) {
        ArrayList arrayList = new ArrayList();
        if (sIsServiceEnabled) {
            getContext().enforceCallingOrSelfPermission("android.permission.MANAGE_APP_HIBERNATION", "Caller does not have MANAGE_APP_HIBERNATION permission.");
            int handleIncomingUser = handleIncomingUser(i, "getHibernatingPackagesForUser");
            synchronized (this.mLock) {
                if (checkUserStatesExist(handleIncomingUser, "getHibernatingPackagesForUser", true)) {
                    for (UserLevelState userLevelState : this.mUserStates.get(handleIncomingUser).values()) {
                        if (this.mPackageManagerInternal.canQueryPackage(Binder.getCallingUid(), userLevelState.packageName) && userLevelState.hibernated) {
                            arrayList.add(userLevelState.packageName);
                        }
                    }
                    return arrayList;
                }
                return arrayList;
            }
        }
        return arrayList;
    }

    public Map<String, HibernationStats> getHibernationStatsForUser(Set<String> set, int i) {
        ArrayMap arrayMap = new ArrayMap();
        if (sIsServiceEnabled) {
            getContext().enforceCallingOrSelfPermission("android.permission.MANAGE_APP_HIBERNATION", "Caller does not have MANAGE_APP_HIBERNATION permission.");
            int handleIncomingUser = handleIncomingUser(i, "getHibernationStatsForUser");
            synchronized (this.mLock) {
                if (checkUserStatesExist(handleIncomingUser, "getHibernationStatsForUser", true)) {
                    Map<String, UserLevelState> map = this.mUserStates.get(handleIncomingUser);
                    if (set == null) {
                        set = map.keySet();
                    }
                    for (String str : set) {
                        if (this.mPackageManagerInternal.canQueryPackage(Binder.getCallingUid(), str)) {
                            if (this.mGlobalHibernationStates.containsKey(str) && map.containsKey(str)) {
                                arrayMap.put(str, new HibernationStats(this.mGlobalHibernationStates.get(str).savedByte + map.get(str).savedByte));
                            }
                            Slog.w("AppHibernationService", TextUtils.formatSimple("No hibernation state associated with package %s user %d. Maybethe package was uninstalled? ", new Object[]{str, Integer.valueOf(handleIncomingUser)}));
                        }
                    }
                    return arrayMap;
                }
                return arrayMap;
            }
        }
        return arrayMap;
    }

    /* renamed from: hibernatePackageForUser */
    public final void lambda$setHibernatingForUser$1(String str, int i, UserLevelState userLevelState) {
        Trace.traceBegin(524288L, "hibernatePackage");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                try {
                    StorageStats queryStatsForPackage = this.mStorageStatsManager.queryStatsForPackage(this.mIPackageManager.getApplicationInfo(str, 537698816L, i).storageUuid, str, new UserHandle(i));
                    this.mIActivityManager.forceStopPackage(str, i);
                    this.mIPackageManager.deleteApplicationCacheFilesAsUser(str, i, (IPackageDataObserver) null);
                    synchronized (this.mLock) {
                        userLevelState.savedByte = queryStatsForPackage.getCacheBytes();
                    }
                } catch (IOException e) {
                    Slog.e("AppHibernationService", "Storage device not found", e);
                }
            } catch (PackageManager.NameNotFoundException e2) {
                Slog.e("AppHibernationService", "Package name not found when querying storage stats", e2);
            } catch (RemoteException e3) {
                throw new IllegalStateException("Failed to hibernate due to manager not being available", e3);
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            Trace.traceEnd(524288L);
        }
    }

    /* renamed from: unhibernatePackageForUser */
    public final void lambda$setHibernatingForUser$2(String str, int i) {
        Trace.traceBegin(524288L, "unhibernatePackage");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                String[] strArr = {"android.permission.RECEIVE_BOOT_COMPLETED"};
                this.mIActivityManager.broadcastIntentWithFeature((IApplicationThread) null, (String) null, new Intent("android.intent.action.LOCKED_BOOT_COMPLETED").setPackage(str), (String) null, (IIntentReceiver) null, -1, (String) null, (Bundle) null, strArr, (String[]) null, (String[]) null, -1, (Bundle) null, false, false, i);
                this.mIActivityManager.broadcastIntentWithFeature((IApplicationThread) null, (String) null, new Intent("android.intent.action.BOOT_COMPLETED").setPackage(str), (String) null, (IIntentReceiver) null, -1, (String) null, (Bundle) null, strArr, (String[]) null, (String[]) null, -1, (Bundle) null, false, false, i);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            Trace.traceEnd(524288L);
        }
    }

    /* renamed from: hibernatePackageGlobally */
    public final void lambda$setHibernatingGlobally$4(String str, GlobalLevelState globalLevelState) {
        Trace.traceBegin(524288L, "hibernatePackageGlobally");
        long max = this.mOatArtifactDeletionEnabled ? Math.max(this.mPackageManagerInternal.deleteOatArtifactsOfPackage(str), 0L) : 0L;
        synchronized (this.mLock) {
            globalLevelState.savedByte = max;
        }
        Trace.traceEnd(524288L);
    }

    @GuardedBy({"mLock"})
    public final void initializeUserHibernationStates(int i, List<UserLevelState> list) {
        try {
            List list2 = this.mIPackageManager.getInstalledPackages(537698816L, i).getList();
            ArrayMap arrayMap = new ArrayMap();
            int size = list2.size();
            for (int i2 = 0; i2 < size; i2++) {
                String str = ((PackageInfo) list2.get(i2)).packageName;
                UserLevelState userLevelState = new UserLevelState();
                userLevelState.packageName = str;
                arrayMap.put(str, userLevelState);
            }
            if (list != null) {
                ArrayMap arrayMap2 = new ArrayMap();
                int size2 = list2.size();
                for (int i3 = 0; i3 < size2; i3++) {
                    arrayMap2.put(((PackageInfo) list2.get(i3)).packageName, (PackageInfo) list2.get(i3));
                }
                int size3 = list.size();
                for (int i4 = 0; i4 < size3; i4++) {
                    String str2 = list.get(i4).packageName;
                    PackageInfo packageInfo = (PackageInfo) arrayMap2.get(str2);
                    UserLevelState userLevelState2 = list.get(i4);
                    if (packageInfo == null) {
                        Slog.w("AppHibernationService", TextUtils.formatSimple("No hibernation state associated with package %s user %d. Maybethe package was uninstalled? ", new Object[]{str2, Integer.valueOf(i)}));
                    } else {
                        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                        if (applicationInfo != null) {
                            int i5 = applicationInfo.flags & 2097152;
                            applicationInfo.flags = i5;
                            if (i5 == 0 && userLevelState2.hibernated) {
                                userLevelState2.hibernated = false;
                                userLevelState2.lastUnhibernatedMs = System.currentTimeMillis();
                            }
                        }
                        arrayMap.put(str2, userLevelState2);
                    }
                }
            }
            this.mUserStates.put(i, arrayMap);
        } catch (RemoteException e) {
            throw new IllegalStateException("Package manager not available", e);
        }
    }

    @GuardedBy({"mLock"})
    public final void initializeGlobalHibernationStates(List<GlobalLevelState> list) {
        try {
            List list2 = this.mIPackageManager.getInstalledPackages(541893120L, 0).getList();
            int size = list2.size();
            for (int i = 0; i < size; i++) {
                String str = ((PackageInfo) list2.get(i)).packageName;
                GlobalLevelState globalLevelState = new GlobalLevelState();
                globalLevelState.packageName = str;
                this.mGlobalHibernationStates.put(str, globalLevelState);
            }
            if (list != null) {
                ArraySet arraySet = new ArraySet();
                int size2 = list2.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    arraySet.add(((PackageInfo) list2.get(i2)).packageName);
                }
                int size3 = list.size();
                for (int i3 = 0; i3 < size3; i3++) {
                    GlobalLevelState globalLevelState2 = list.get(i3);
                    if (!arraySet.contains(globalLevelState2.packageName)) {
                        Slog.w("AppHibernationService", TextUtils.formatSimple("No hibernation state associated with package %s. Maybe the package was uninstalled? ", new Object[]{globalLevelState2.packageName}));
                    } else {
                        this.mGlobalHibernationStates.put(globalLevelState2.packageName, globalLevelState2);
                    }
                }
            }
        } catch (RemoteException e) {
            throw new IllegalStateException("Package manager not available", e);
        }
    }

    @Override // com.android.server.SystemService
    public void onUserUnlocking(SystemService.TargetUser targetUser) {
        final int userIdentifier = targetUser.getUserIdentifier();
        final HibernationStateDiskStore<UserLevelState> userLevelDiskStore = this.mInjector.getUserLevelDiskStore(userIdentifier);
        this.mUserDiskStores.put(userIdentifier, userLevelDiskStore);
        this.mBackgroundExecutor.execute(new Runnable() { // from class: com.android.server.apphibernation.AppHibernationService$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                AppHibernationService.this.lambda$onUserUnlocking$5(userLevelDiskStore, userIdentifier);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onUserUnlocking$5(HibernationStateDiskStore hibernationStateDiskStore, int i) {
        List<UserLevelState> readHibernationStates = hibernationStateDiskStore.readHibernationStates();
        synchronized (this.mLock) {
            if (this.mUserManager.isUserUnlockingOrUnlocked(i)) {
                initializeUserHibernationStates(i, readHibernationStates);
                for (UserLevelState userLevelState : this.mUserStates.get(i).values()) {
                    String str = userLevelState.packageName;
                    if (this.mGlobalHibernationStates.get(str).hibernated && !userLevelState.hibernated) {
                        setHibernatingGlobally(str, false);
                    }
                }
            }
        }
    }

    @Override // com.android.server.SystemService
    public void onUserStopping(SystemService.TargetUser targetUser) {
        int userIdentifier = targetUser.getUserIdentifier();
        synchronized (this.mLock) {
            this.mUserDiskStores.remove(userIdentifier);
            this.mUserStates.remove(userIdentifier);
        }
    }

    public final void onPackageAdded(String str, int i) {
        synchronized (this.mLock) {
            if (this.mUserStates.contains(i)) {
                UserLevelState userLevelState = new UserLevelState();
                userLevelState.packageName = str;
                this.mUserStates.get(i).put(str, userLevelState);
                if (!this.mGlobalHibernationStates.containsKey(str)) {
                    GlobalLevelState globalLevelState = new GlobalLevelState();
                    globalLevelState.packageName = str;
                    this.mGlobalHibernationStates.put(str, globalLevelState);
                }
            }
        }
    }

    public final void onPackageRemoved(String str, int i) {
        synchronized (this.mLock) {
            if (this.mUserStates.contains(i)) {
                this.mUserStates.get(i).remove(str);
            }
        }
    }

    public final void onPackageRemovedForAllUsers(String str) {
        synchronized (this.mLock) {
            this.mGlobalHibernationStates.remove(str);
        }
    }

    public final void onDeviceConfigChanged(DeviceConfig.Properties properties) {
        for (String str : properties.getKeyset()) {
            if (TextUtils.equals("app_hibernation_enabled", str)) {
                sIsServiceEnabled = isDeviceConfigAppHibernationEnabled();
                Slog.d("AppHibernationService", "App hibernation changed to enabled=" + sIsServiceEnabled);
                return;
            }
        }
    }

    public final int handleIncomingUser(int i, String str) {
        try {
            return this.mIActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, true, str, (String) null);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @GuardedBy({"mLock"})
    public final boolean checkUserStatesExist(int i, String str, boolean z) {
        if (!this.mUserManager.isUserUnlockingOrUnlocked(i)) {
            if (z) {
                Slog.w("AppHibernationService", TextUtils.formatSimple("Attempt to call %s on stopped or nonexistent user %d", new Object[]{str, Integer.valueOf(i)}));
            }
            return false;
        } else if (this.mUserStates.contains(i)) {
            return true;
        } else {
            if (z) {
                Slog.w("AppHibernationService", TextUtils.formatSimple("Attempt to call %s before states have been read from disk", new Object[]{str}));
            }
            return false;
        }
    }

    public final void dump(PrintWriter printWriter) {
        if (DumpUtils.checkDumpAndUsageStatsPermission(getContext(), "AppHibernationService", printWriter)) {
            IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
            synchronized (this.mLock) {
                int size = this.mUserStates.size();
                for (int i = 0; i < size; i++) {
                    int keyAt = this.mUserStates.keyAt(i);
                    indentingPrintWriter.print("User Level Hibernation States, ");
                    indentingPrintWriter.printPair("user", Integer.valueOf(keyAt));
                    indentingPrintWriter.println();
                    indentingPrintWriter.increaseIndent();
                    for (UserLevelState userLevelState : this.mUserStates.get(keyAt).values()) {
                        indentingPrintWriter.print(userLevelState);
                        indentingPrintWriter.println();
                    }
                    indentingPrintWriter.decreaseIndent();
                }
                indentingPrintWriter.println();
                indentingPrintWriter.print("Global Level Hibernation States");
                indentingPrintWriter.println();
                for (GlobalLevelState globalLevelState : this.mGlobalHibernationStates.values()) {
                    indentingPrintWriter.print(globalLevelState);
                    indentingPrintWriter.println();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class LocalService extends AppHibernationManagerInternal {
        public final AppHibernationService mService;

        public LocalService(AppHibernationService appHibernationService) {
            this.mService = appHibernationService;
        }

        @Override // com.android.server.apphibernation.AppHibernationManagerInternal
        public boolean isHibernatingForUser(String str, int i) {
            return this.mService.isHibernatingForUser(str, i);
        }

        @Override // com.android.server.apphibernation.AppHibernationManagerInternal
        public void setHibernatingForUser(String str, int i, boolean z) {
            this.mService.setHibernatingForUser(str, i, z);
        }

        @Override // com.android.server.apphibernation.AppHibernationManagerInternal
        public void setHibernatingGlobally(String str, boolean z) {
            this.mService.setHibernatingGlobally(str, z);
        }

        @Override // com.android.server.apphibernation.AppHibernationManagerInternal
        public boolean isHibernatingGlobally(String str) {
            return this.mService.isHibernatingGlobally(str);
        }

        @Override // com.android.server.apphibernation.AppHibernationManagerInternal
        public boolean isOatArtifactDeletionEnabled() {
            return this.mService.isOatArtifactDeletionEnabled();
        }
    }

    /* loaded from: classes.dex */
    public static final class AppHibernationServiceStub extends IAppHibernationService.Stub {
        public final AppHibernationService mService;

        public AppHibernationServiceStub(AppHibernationService appHibernationService) {
            this.mService = appHibernationService;
        }

        public boolean isHibernatingForUser(String str, int i) {
            return this.mService.isHibernatingForUser(str, i);
        }

        public void setHibernatingForUser(String str, int i, boolean z) {
            this.mService.setHibernatingForUser(str, i, z);
        }

        public void setHibernatingGlobally(String str, boolean z) {
            this.mService.setHibernatingGlobally(str, z);
        }

        public boolean isHibernatingGlobally(String str) {
            return this.mService.isHibernatingGlobally(str);
        }

        public List<String> getHibernatingPackagesForUser(int i) {
            return this.mService.getHibernatingPackagesForUser(i);
        }

        public Map<String, HibernationStats> getHibernationStatsForUser(List<String> list, int i) {
            return this.mService.getHibernationStatsForUser(list != null ? new ArraySet(list) : null, i);
        }

        public boolean isOatArtifactDeletionEnabled() {
            return this.mService.isOatArtifactDeletionEnabled();
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
            new AppHibernationShellCommand(this.mService).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            this.mService.dump(printWriter);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(int i, UsageEvents.Event event) {
        if (isAppHibernationEnabled()) {
            int i2 = event.mEventType;
            if (i2 == 7 || i2 == 1 || i2 == 31) {
                String str = event.mPackage;
                setHibernatingForUser(str, i, false);
                setHibernatingGlobally(str, false);
            }
        }
    }

    public static boolean isAppHibernationEnabled() {
        return sIsServiceEnabled;
    }

    public static boolean isDeviceConfigAppHibernationEnabled() {
        return DeviceConfig.getBoolean("app_hibernation", "app_hibernation_enabled", true);
    }

    /* loaded from: classes.dex */
    public static final class InjectorImpl implements Injector {
        public final Context mContext;
        public final ScheduledExecutorService mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        public final UserLevelHibernationProto mUserLevelHibernationProto = new UserLevelHibernationProto();

        public InjectorImpl(Context context) {
            this.mContext = context;
        }

        @Override // com.android.server.apphibernation.AppHibernationService.Injector
        public Context getContext() {
            return this.mContext;
        }

        @Override // com.android.server.apphibernation.AppHibernationService.Injector
        public IPackageManager getPackageManager() {
            return IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
        }

        @Override // com.android.server.apphibernation.AppHibernationService.Injector
        public PackageManagerInternal getPackageManagerInternal() {
            return (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        }

        @Override // com.android.server.apphibernation.AppHibernationService.Injector
        public IActivityManager getActivityManager() {
            return ActivityManager.getService();
        }

        @Override // com.android.server.apphibernation.AppHibernationService.Injector
        public UserManager getUserManager() {
            return (UserManager) this.mContext.getSystemService(UserManager.class);
        }

        @Override // com.android.server.apphibernation.AppHibernationService.Injector
        public StorageStatsManager getStorageStatsManager() {
            return (StorageStatsManager) this.mContext.getSystemService(StorageStatsManager.class);
        }

        @Override // com.android.server.apphibernation.AppHibernationService.Injector
        public Executor getBackgroundExecutor() {
            return this.mScheduledExecutorService;
        }

        @Override // com.android.server.apphibernation.AppHibernationService.Injector
        public UsageStatsManagerInternal getUsageStatsManagerInternal() {
            return (UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class);
        }

        @Override // com.android.server.apphibernation.AppHibernationService.Injector
        public HibernationStateDiskStore<GlobalLevelState> getGlobalLevelDiskStore() {
            return new HibernationStateDiskStore<>(new File(Environment.getDataSystemDirectory(), "hibernation"), new GlobalLevelHibernationProto(), this.mScheduledExecutorService);
        }

        @Override // com.android.server.apphibernation.AppHibernationService.Injector
        public HibernationStateDiskStore<UserLevelState> getUserLevelDiskStore(int i) {
            return new HibernationStateDiskStore<>(new File(Environment.getDataSystemCeDirectory(i), "hibernation"), this.mUserLevelHibernationProto, this.mScheduledExecutorService);
        }

        @Override // com.android.server.apphibernation.AppHibernationService.Injector
        public boolean isOatArtifactDeletionEnabled() {
            return this.mContext.getResources().getBoolean(17891700);
        }
    }

    /* loaded from: classes.dex */
    public final class StatsPullAtomCallbackImpl implements StatsManager.StatsPullAtomCallback {
        public StatsPullAtomCallbackImpl() {
        }

        public int onPullAtom(int i, List<StatsEvent> list) {
            long j;
            int i2;
            if (AppHibernationService.isAppHibernationEnabled() || !(i == 10107 || i == 10109)) {
                if (i == 10107) {
                    List aliveUsers = AppHibernationService.this.mUserManager.getAliveUsers();
                    int size = aliveUsers.size();
                    for (int i3 = 0; i3 < size; i3++) {
                        int i4 = ((UserInfo) aliveUsers.get(i3)).id;
                        if (AppHibernationService.this.mUserManager.isUserUnlockingOrUnlocked(i4)) {
                            list.add(FrameworkStatsLog.buildStatsEvent(i, AppHibernationService.this.getHibernatingPackagesForUser(i4).size(), i4));
                        }
                    }
                } else if (i != 10109) {
                    return 1;
                } else {
                    synchronized (AppHibernationService.this.mLock) {
                        j = 0;
                        i2 = 0;
                        for (GlobalLevelState globalLevelState : AppHibernationService.this.mGlobalHibernationStates.values()) {
                            if (globalLevelState.hibernated) {
                                i2++;
                                j += globalLevelState.savedByte;
                            }
                        }
                    }
                    list.add(FrameworkStatsLog.buildStatsEvent(i, i2, j / 1000000));
                }
                return 0;
            }
            return 0;
        }
    }
}
