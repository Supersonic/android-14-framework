package com.android.server.content;

import android.accounts.Account;
import android.accounts.AccountAndUser;
import android.accounts.AccountManager;
import android.accounts.AccountManagerInternal;
import android.annotation.SuppressLint;
import android.app.ActivityManagerInternal;
import android.app.AppGlobals;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.usage.UsageStatsManagerInternal;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ISyncAdapter;
import android.content.ISyncAdapterUnsyncableAccountCallback;
import android.content.ISyncContext;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.PeriodicSync;
import android.content.ServiceConnection;
import android.content.SyncActivityTooManyDeletes;
import android.content.SyncAdapterType;
import android.content.SyncAdaptersCache;
import android.content.SyncInfo;
import android.content.SyncResult;
import android.content.SyncStats;
import android.content.SyncStatusInfo;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.RegisteredServicesCache;
import android.content.pm.RegisteredServicesCacheListener;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.WorkSource;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.TimeMigrationUtils;
import android.util.EventLog;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseBooleanArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IBatteryStats;
import com.android.internal.config.appcloning.AppCloningDeviceConfigHelper;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.function.QuadConsumer;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.DeviceIdleInternal;
import com.android.server.LocalServices;
import com.android.server.accounts.AccountManagerService;
import com.android.server.backup.AccountSyncSettingsBackupHelper;
import com.android.server.content.SyncManager;
import com.android.server.content.SyncStorageEngine;
import com.android.server.job.JobSchedulerInternal;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import dalvik.annotation.optimization.NeverCompile;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class SyncManager {
    @GuardedBy({"SyncManager.class"})
    public static SyncManager sInstance;
    public final AccountManager mAccountManager;
    public final AccountManagerInternal mAccountManagerInternal;
    public final BroadcastReceiver mAccountsUpdatedReceiver;
    public final ActivityManagerInternal mAmi;
    public final AppCloningDeviceConfigHelper mAppCloningDeviceConfigHelper;
    public final IBatteryStats mBatteryStats;
    public ConnectivityManager mConnManagerDoNotUseDirectly;
    public BroadcastReceiver mConnectivityIntentReceiver;
    public final SyncManagerConstants mConstants;
    public Context mContext;
    public JobScheduler mJobScheduler;
    public final SyncLogger mLogger;
    public final NotificationManager mNotificationMgr;
    public final BroadcastReceiver mOtherIntentsReceiver;
    public final PackageManagerInternal mPackageManagerInternal;
    public final PowerManager mPowerManager;
    public volatile boolean mProvisioned;
    public BroadcastReceiver mShutdownIntentReceiver;
    public final SyncAdaptersCache mSyncAdapters;
    public final SyncHandler mSyncHandler;
    public volatile PowerManager.WakeLock mSyncManagerWakeLock;
    public SyncStorageEngine mSyncStorageEngine;
    public final HandlerThread mThread;
    @GuardedBy({"mUnlockedUsers"})
    public final SparseBooleanArray mUnlockedUsers;
    public BroadcastReceiver mUserIntentReceiver;
    public final UserManager mUserManager;
    public static final boolean ENABLE_SUSPICIOUS_CHECK = Build.IS_DEBUGGABLE;
    public static final long LOCAL_SYNC_DELAY = SystemProperties.getLong("sync.local_sync_delay", 30000);
    public static final AccountAndUser[] INITIAL_ACCOUNTS_ARRAY = new AccountAndUser[0];
    public static final Comparator<SyncOperation> sOpDumpComparator = new Comparator() { // from class: com.android.server.content.SyncManager$$ExternalSyntheticLambda1
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            int lambda$static$6;
            lambda$static$6 = SyncManager.lambda$static$6((SyncOperation) obj, (SyncOperation) obj2);
            return lambda$static$6;
        }
    };
    public static final Comparator<SyncOperation> sOpRuntimeComparator = new Comparator() { // from class: com.android.server.content.SyncManager$$ExternalSyntheticLambda2
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            int lambda$static$7;
            lambda$static$7 = SyncManager.lambda$static$7((SyncOperation) obj, (SyncOperation) obj2);
            return lambda$static$7;
        }
    };
    public final Object mAccountsLock = new Object();
    public volatile AccountAndUser[] mRunningAccounts = INITIAL_ACCOUNTS_ARRAY;
    public volatile boolean mDataConnectionIsConnected = false;
    public volatile int mNextJobId = 0;
    public final ArrayList<ActiveSyncContext> mActiveSyncContexts = Lists.newArrayList();

    /* loaded from: classes.dex */
    public interface OnReadyCallback {
        void onReady();
    }

    public final boolean isJobIdInUseLockedH(int i, List<JobInfo> list) {
        int size = list.size();
        for (int i2 = 0; i2 < size; i2++) {
            if (list.get(i2).getId() == i) {
                return true;
            }
        }
        int size2 = this.mActiveSyncContexts.size();
        for (int i3 = 0; i3 < size2; i3++) {
            if (this.mActiveSyncContexts.get(i3).mSyncOperation.jobId == i) {
                return true;
            }
        }
        return false;
    }

    public final int getUnusedJobIdH() {
        List<JobInfo> allPendingJobs = this.mJobScheduler.getAllPendingJobs();
        while (isJobIdInUseLockedH(this.mNextJobId, allPendingJobs)) {
            this.mNextJobId++;
        }
        return this.mNextJobId;
    }

    public final List<SyncOperation> getAllPendingSyncs() {
        verifyJobScheduler();
        List<JobInfo> allPendingJobs = this.mJobScheduler.getAllPendingJobs();
        int size = allPendingJobs.size();
        ArrayList arrayList = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            SyncOperation maybeCreateFromJobExtras = SyncOperation.maybeCreateFromJobExtras(allPendingJobs.get(i).getExtras());
            if (maybeCreateFromJobExtras != null) {
                arrayList.add(maybeCreateFromJobExtras);
            } else {
                Slog.wtf("SyncManager", "Non-sync job inside of SyncManager's namespace");
            }
        }
        return arrayList;
    }

    public final List<UserInfo> getAllUsers() {
        return this.mUserManager.getUsers();
    }

    public final boolean containsAccountAndUser(AccountAndUser[] accountAndUserArr, Account account, int i) {
        for (AccountAndUser accountAndUser : accountAndUserArr) {
            if (accountAndUser.userId == i && accountAndUser.account.equals(account)) {
                return true;
            }
        }
        return false;
    }

    public final void updateRunningAccounts(SyncStorageEngine.EndPoint endPoint) {
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "sending MESSAGE_ACCOUNTS_UPDATED");
        }
        Message obtainMessage = this.mSyncHandler.obtainMessage(9);
        obtainMessage.obj = endPoint;
        obtainMessage.sendToTarget();
    }

    public final void removeStaleAccounts() {
        for (UserInfo userInfo : this.mUserManager.getAliveUsers()) {
            if (!userInfo.partial) {
                this.mSyncStorageEngine.removeStaleAccounts(AccountManagerService.getSingleton().getAccounts(userInfo.id, this.mContext.getOpPackageName()), userInfo.id);
            }
        }
    }

    public final void clearAllBackoffs(String str) {
        this.mSyncStorageEngine.clearAllBackoffsLocked();
        rescheduleSyncs(SyncStorageEngine.EndPoint.USER_ALL_PROVIDER_ALL_ACCOUNTS_ALL, str);
    }

    public final boolean readDataConnectionState() {
        NetworkInfo activeNetworkInfo = getConnectivityManager().getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public final String getJobStats() {
        JobSchedulerInternal jobSchedulerInternal = (JobSchedulerInternal) LocalServices.getService(JobSchedulerInternal.class);
        StringBuilder sb = new StringBuilder();
        sb.append("JobStats: ");
        sb.append(jobSchedulerInternal == null ? "(JobSchedulerInternal==null)" : jobSchedulerInternal.getPersistStats().toString());
        return sb.toString();
    }

    public final ConnectivityManager getConnectivityManager() {
        ConnectivityManager connectivityManager;
        synchronized (this) {
            if (this.mConnManagerDoNotUseDirectly == null) {
                this.mConnManagerDoNotUseDirectly = (ConnectivityManager) this.mContext.getSystemService("connectivity");
            }
            connectivityManager = this.mConnManagerDoNotUseDirectly;
        }
        return connectivityManager;
    }

    public final void cleanupJobs() {
        this.mSyncHandler.postAtFrontOfQueue(new Runnable() { // from class: com.android.server.content.SyncManager.6
            @Override // java.lang.Runnable
            public void run() {
                List<SyncOperation> allPendingSyncs = SyncManager.this.getAllPendingSyncs();
                HashSet hashSet = new HashSet();
                for (SyncOperation syncOperation : allPendingSyncs) {
                    if (!hashSet.contains(syncOperation.key)) {
                        hashSet.add(syncOperation.key);
                        for (SyncOperation syncOperation2 : allPendingSyncs) {
                            if (syncOperation != syncOperation2 && syncOperation.key.equals(syncOperation2.key)) {
                                SyncManager.this.mLogger.log("Removing duplicate sync: ", syncOperation2);
                                SyncManager syncManager = SyncManager.this;
                                syncManager.cancelJob(syncOperation2, "cleanupJobs() x=" + syncOperation + " y=" + syncOperation2);
                            }
                        }
                    }
                }
            }
        });
    }

    public final void migrateSyncJobNamespaceIfNeeded() {
        boolean isJobNamespaceMigrated = this.mSyncStorageEngine.isJobNamespaceMigrated();
        boolean isJobAttributionFixed = this.mSyncStorageEngine.isJobAttributionFixed();
        if (isJobNamespaceMigrated && isJobAttributionFixed) {
            return;
        }
        JobScheduler jobScheduler = (JobScheduler) this.mContext.getSystemService(JobScheduler.class);
        boolean z = true;
        if (!isJobNamespaceMigrated) {
            List<JobInfo> allPendingJobs = jobScheduler.getAllPendingJobs();
            boolean z2 = true;
            for (int size = allPendingJobs.size() - 1; size >= 0; size--) {
                JobInfo jobInfo = allPendingJobs.get(size);
                SyncOperation maybeCreateFromJobExtras = SyncOperation.maybeCreateFromJobExtras(jobInfo.getExtras());
                if (maybeCreateFromJobExtras != null) {
                    this.mJobScheduler.scheduleAsPackage(jobInfo, maybeCreateFromJobExtras.owningPackage, maybeCreateFromJobExtras.target.userId, maybeCreateFromJobExtras.wakeLockName());
                    jobScheduler.cancel(jobInfo.getId());
                    z2 = false;
                }
            }
            this.mSyncStorageEngine.setJobNamespaceMigrated(z2);
        }
        List systemScheduledOwnJobs = ((JobSchedulerInternal) LocalServices.getService(JobSchedulerInternal.class)).getSystemScheduledOwnJobs(this.mJobScheduler.getNamespace());
        for (int size2 = systemScheduledOwnJobs.size() - 1; size2 >= 0; size2--) {
            JobInfo jobInfo2 = (JobInfo) systemScheduledOwnJobs.get(size2);
            SyncOperation maybeCreateFromJobExtras2 = SyncOperation.maybeCreateFromJobExtras(jobInfo2.getExtras());
            if (maybeCreateFromJobExtras2 != null) {
                this.mJobScheduler.scheduleAsPackage(jobInfo2, maybeCreateFromJobExtras2.owningPackage, maybeCreateFromJobExtras2.target.userId, maybeCreateFromJobExtras2.wakeLockName());
                z = false;
            }
        }
        this.mSyncStorageEngine.setJobAttributionFixed(z);
    }

    public final synchronized void verifyJobScheduler() {
        List<JobInfo> allPendingJobs;
        if (this.mJobScheduler != null) {
            return;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        if (Log.isLoggable("SyncManager", 2)) {
            Log.d("SyncManager", "initializing JobScheduler object.");
        }
        this.mJobScheduler = ((JobScheduler) this.mContext.getSystemService(JobScheduler.class)).forNamespace("SyncManager");
        migrateSyncJobNamespaceIfNeeded();
        int i = 0;
        int i2 = 0;
        for (JobInfo jobInfo : this.mJobScheduler.getAllPendingJobs()) {
            SyncOperation maybeCreateFromJobExtras = SyncOperation.maybeCreateFromJobExtras(jobInfo.getExtras());
            if (maybeCreateFromJobExtras != null) {
                if (maybeCreateFromJobExtras.isPeriodic) {
                    i++;
                } else {
                    i2++;
                    this.mSyncStorageEngine.markPending(maybeCreateFromJobExtras.target, true);
                }
            } else {
                Slog.wtf("SyncManager", "Non-sync job inside of SyncManager namespace");
            }
        }
        String str = "Loaded persisted syncs: " + i + " periodic syncs, " + i2 + " oneshot syncs, " + allPendingJobs.size() + " total system server jobs, " + getJobStats();
        Slog.i("SyncManager", str);
        this.mLogger.log(str);
        cleanupJobs();
        if (ENABLE_SUSPICIOUS_CHECK && i == 0 && likelyHasPeriodicSyncs()) {
            Slog.wtf("SyncManager", "Device booted with no persisted periodic syncs: " + str);
        }
        Binder.restoreCallingIdentity(clearCallingIdentity);
    }

    public final boolean likelyHasPeriodicSyncs() {
        try {
            return this.mSyncStorageEngine.getAuthorityCount() >= 6;
        } catch (Throwable unused) {
            return false;
        }
    }

    public final JobScheduler getJobScheduler() {
        verifyJobScheduler();
        return this.mJobScheduler;
    }

    public SyncManager(Context context, boolean z) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.content.SyncManager.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                SyncManager.this.updateRunningAccounts(new SyncStorageEngine.EndPoint(null, null, getSendingUserId()));
            }
        };
        this.mAccountsUpdatedReceiver = broadcastReceiver;
        this.mConnectivityIntentReceiver = new BroadcastReceiver() { // from class: com.android.server.content.SyncManager.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                boolean z2 = SyncManager.this.mDataConnectionIsConnected;
                SyncManager syncManager = SyncManager.this;
                syncManager.mDataConnectionIsConnected = syncManager.readDataConnectionState();
                if (!SyncManager.this.mDataConnectionIsConnected || z2) {
                    return;
                }
                if (Log.isLoggable("SyncManager", 2)) {
                    Slog.v("SyncManager", "Reconnection detected: clearing all backoffs");
                }
                SyncManager.this.clearAllBackoffs("network reconnect");
            }
        };
        this.mShutdownIntentReceiver = new BroadcastReceiver() { // from class: com.android.server.content.SyncManager.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                Log.w("SyncManager", "Writing sync state before shutdown...");
                SyncManager.this.getSyncStorageEngine().writeAllState();
                SyncManager.this.mLogger.log(SyncManager.this.getJobStats());
                SyncManager.this.mLogger.log("Shutting down.");
            }
        };
        BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() { // from class: com.android.server.content.SyncManager.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if ("android.intent.action.TIME_SET".equals(intent.getAction())) {
                    SyncManager.this.mSyncStorageEngine.setClockValid();
                }
            }
        };
        this.mOtherIntentsReceiver = broadcastReceiver2;
        this.mUserIntentReceiver = new BroadcastReceiver() { // from class: com.android.server.content.SyncManager.5
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                int intExtra = intent.getIntExtra("android.intent.extra.user_handle", -10000);
                if (intExtra == -10000) {
                    return;
                }
                if ("android.intent.action.USER_REMOVED".equals(action)) {
                    SyncManager.this.onUserRemoved(intExtra);
                } else if ("android.intent.action.USER_UNLOCKED".equals(action)) {
                    SyncManager.this.onUserUnlocked(intExtra);
                } else if ("android.intent.action.USER_STOPPED".equals(action)) {
                    SyncManager.this.onUserStopped(intExtra);
                }
            }
        };
        this.mUnlockedUsers = new SparseBooleanArray();
        synchronized (SyncManager.class) {
            if (sInstance == null) {
                sInstance = this;
            } else {
                Slog.wtf("SyncManager", "SyncManager instantiated multiple times");
            }
        }
        this.mContext = context;
        SyncLogger syncLogger = SyncLogger.getInstance();
        this.mLogger = syncLogger;
        SyncStorageEngine.init(context, BackgroundThread.get().getLooper());
        SyncStorageEngine singleton = SyncStorageEngine.getSingleton();
        this.mSyncStorageEngine = singleton;
        singleton.setOnSyncRequestListener(new SyncStorageEngine.OnSyncRequestListener() { // from class: com.android.server.content.SyncManager.7
            @Override // com.android.server.content.SyncStorageEngine.OnSyncRequestListener
            public void onSyncRequest(SyncStorageEngine.EndPoint endPoint, int i, Bundle bundle, int i2, int i3, int i4) {
                SyncManager.this.scheduleSync(endPoint.account, endPoint.userId, i, endPoint.provider, bundle, -2, i2, i3, i4, null);
            }
        });
        this.mSyncStorageEngine.setPeriodicSyncAddedListener(new SyncStorageEngine.PeriodicSyncAddedListener() { // from class: com.android.server.content.SyncManager.8
            @Override // com.android.server.content.SyncStorageEngine.PeriodicSyncAddedListener
            public void onPeriodicSyncAdded(SyncStorageEngine.EndPoint endPoint, Bundle bundle, long j, long j2) {
                SyncManager.this.updateOrAddPeriodicSync(endPoint, j, j2, bundle);
            }
        });
        this.mSyncStorageEngine.setOnAuthorityRemovedListener(new SyncStorageEngine.OnAuthorityRemovedListener() { // from class: com.android.server.content.SyncManager.9
            @Override // com.android.server.content.SyncStorageEngine.OnAuthorityRemovedListener
            public void onAuthorityRemoved(SyncStorageEngine.EndPoint endPoint) {
                SyncManager.this.removeSyncsForAuthority(endPoint, "onAuthorityRemoved");
            }
        });
        SyncAdaptersCache syncAdaptersCache = new SyncAdaptersCache(this.mContext);
        this.mSyncAdapters = syncAdaptersCache;
        HandlerThread handlerThread = new HandlerThread("SyncManager", 10);
        this.mThread = handlerThread;
        handlerThread.start();
        SyncHandler syncHandler = new SyncHandler(handlerThread.getLooper());
        this.mSyncHandler = syncHandler;
        syncAdaptersCache.setListener(new RegisteredServicesCacheListener<SyncAdapterType>() { // from class: com.android.server.content.SyncManager.10
            public void onServiceChanged(SyncAdapterType syncAdapterType, int i, boolean z2) {
                if (z2) {
                    return;
                }
                SyncManager.this.scheduleSync(null, -1, -3, syncAdapterType.authority, null, -2, 0, Process.myUid(), -1, null);
            }
        }, syncHandler);
        this.mConstants = new SyncManagerConstants(context);
        this.mAppCloningDeviceConfigHelper = AppCloningDeviceConfigHelper.getInstance(context);
        context.registerReceiver(this.mConnectivityIntentReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        IntentFilter intentFilter = new IntentFilter("android.intent.action.ACTION_SHUTDOWN");
        intentFilter.setPriority(1000);
        context.registerReceiver(this.mShutdownIntentReceiver, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.USER_REMOVED");
        intentFilter2.addAction("android.intent.action.USER_UNLOCKED");
        intentFilter2.addAction("android.intent.action.USER_STOPPED");
        this.mContext.registerReceiverAsUser(this.mUserIntentReceiver, UserHandle.ALL, intentFilter2, null, null);
        context.registerReceiver(broadcastReceiver2, new IntentFilter("android.intent.action.TIME_SET"));
        if (!z) {
            this.mNotificationMgr = (NotificationManager) context.getSystemService("notification");
        } else {
            this.mNotificationMgr = null;
        }
        PowerManager powerManager = (PowerManager) context.getSystemService("power");
        this.mPowerManager = powerManager;
        this.mUserManager = (UserManager) this.mContext.getSystemService("user");
        this.mAccountManager = (AccountManager) this.mContext.getSystemService("account");
        AccountManagerInternal accountManagerInternal = getAccountManagerInternal();
        this.mAccountManagerInternal = accountManagerInternal;
        this.mPackageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        this.mAmi = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        accountManagerInternal.addOnAppPermissionChangeListener(new AccountManagerInternal.OnAppPermissionChangeListener() { // from class: com.android.server.content.SyncManager$$ExternalSyntheticLambda0
            public final void onAppPermissionChanged(Account account, int i) {
                SyncManager.this.lambda$new$0(account, i);
            }
        });
        this.mBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
        this.mSyncManagerWakeLock = powerManager.newWakeLock(1, "SyncLoopWakeLock");
        this.mSyncManagerWakeLock.setReferenceCounted(false);
        this.mProvisioned = isDeviceProvisioned();
        if (!this.mProvisioned) {
            final ContentResolver contentResolver = context.getContentResolver();
            ContentObserver contentObserver = new ContentObserver(null) { // from class: com.android.server.content.SyncManager.11
                @Override // android.database.ContentObserver
                public void onChange(boolean z2) {
                    SyncManager.this.mProvisioned |= SyncManager.this.isDeviceProvisioned();
                    if (SyncManager.this.mProvisioned) {
                        contentResolver.unregisterContentObserver(this);
                    }
                }
            };
            synchronized (syncHandler) {
                contentResolver.registerContentObserver(Settings.Global.getUriFor("device_provisioned"), false, contentObserver);
                this.mProvisioned |= isDeviceProvisioned();
                if (this.mProvisioned) {
                    contentResolver.unregisterContentObserver(contentObserver);
                }
            }
        }
        if (!z) {
            this.mContext.registerReceiverAsUser(broadcastReceiver, UserHandle.ALL, new IntentFilter("android.accounts.LOGIN_ACCOUNTS_CHANGED"), null, null);
        }
        whiteListExistingSyncAdaptersIfNeeded();
        syncLogger.log("Sync manager initialized: " + Build.FINGERPRINT);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(Account account, int i) {
        if (this.mAccountManagerInternal.hasAccountAccess(account, i)) {
            scheduleSync(account, UserHandle.getUserId(i), -2, null, null, 3, 0, Process.myUid(), -2, null);
        }
    }

    @VisibleForTesting
    public AccountManagerInternal getAccountManagerInternal() {
        return (AccountManagerInternal) LocalServices.getService(AccountManagerInternal.class);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStartUser$1(int i) {
        this.mLogger.log("onStartUser: user=", Integer.valueOf(i));
    }

    public void onStartUser(final int i) {
        this.mSyncHandler.post(new Runnable() { // from class: com.android.server.content.SyncManager$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                SyncManager.this.lambda$onStartUser$1(i);
            }
        });
    }

    public void onUnlockUser(final int i) {
        synchronized (this.mUnlockedUsers) {
            this.mUnlockedUsers.put(i, true);
        }
        this.mSyncHandler.post(new Runnable() { // from class: com.android.server.content.SyncManager$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                SyncManager.this.lambda$onUnlockUser$2(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onUnlockUser$2(int i) {
        this.mLogger.log("onUnlockUser: user=", Integer.valueOf(i));
    }

    public void onStopUser(final int i) {
        synchronized (this.mUnlockedUsers) {
            this.mUnlockedUsers.put(i, false);
        }
        this.mSyncHandler.post(new Runnable() { // from class: com.android.server.content.SyncManager$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                SyncManager.this.lambda$onStopUser$3(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStopUser$3(int i) {
        this.mLogger.log("onStopUser: user=", Integer.valueOf(i));
    }

    public final boolean isUserUnlocked(int i) {
        boolean z;
        synchronized (this.mUnlockedUsers) {
            z = this.mUnlockedUsers.get(i);
        }
        return z;
    }

    public void onBootPhase(int i) {
        if (i != 550) {
            return;
        }
        this.mConstants.start();
    }

    public final void whiteListExistingSyncAdaptersIfNeeded() {
        SyncManager syncManager = this;
        if (syncManager.mSyncStorageEngine.shouldGrantSyncAdaptersAccountAccess()) {
            List aliveUsers = syncManager.mUserManager.getAliveUsers();
            int size = aliveUsers.size();
            int i = 0;
            while (i < size) {
                UserHandle userHandle = ((UserInfo) aliveUsers.get(i)).getUserHandle();
                int identifier = userHandle.getIdentifier();
                for (RegisteredServicesCache.ServiceInfo serviceInfo : syncManager.mSyncAdapters.getAllServices(identifier)) {
                    String packageName = serviceInfo.componentName.getPackageName();
                    Account[] accountsByTypeAsUser = syncManager.mAccountManager.getAccountsByTypeAsUser(((SyncAdapterType) serviceInfo.type).accountType, userHandle);
                    int length = accountsByTypeAsUser.length;
                    int i2 = 0;
                    while (i2 < length) {
                        Account account = accountsByTypeAsUser[i2];
                        if (!syncManager.canAccessAccount(account, packageName, identifier)) {
                            syncManager.mAccountManager.updateAppPermission(account, "com.android.AccountManager.ACCOUNT_ACCESS_TOKEN_TYPE", serviceInfo.uid, true);
                        }
                        i2++;
                        syncManager = this;
                    }
                    syncManager = this;
                }
                i++;
                syncManager = this;
            }
        }
    }

    public final boolean isDeviceProvisioned() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "device_provisioned", 0) != 0;
    }

    public final long jitterize(long j, long j2) {
        Random random = new Random(SystemClock.elapsedRealtime());
        long j3 = j2 - j;
        if (j3 > 2147483647L) {
            throw new IllegalArgumentException("the difference between the maxValue and the minValue must be less than 2147483647");
        }
        return j + random.nextInt((int) j3);
    }

    public SyncStorageEngine getSyncStorageEngine() {
        return this.mSyncStorageEngine;
    }

    @SuppressLint({"AndroidFrameworkRequiresPermission"})
    public final boolean areContactWritesEnabledForUser(UserInfo userInfo) {
        try {
            return !UserManager.get(this.mContext).getUserProperties(userInfo.getUserHandle()).getUseParentsContacts();
        } catch (IllegalArgumentException unused) {
            Log.w("SyncManager", "Trying to fetch user properties for non-existing/partial user " + userInfo.getUserHandle());
            return false;
        }
    }

    public boolean isContactSharingAllowedForCloneProfile() {
        return this.mAppCloningDeviceConfigHelper.getEnableAppCloningBuildingBlocks();
    }

    @VisibleForTesting
    public boolean shouldDisableSyncForUser(UserInfo userInfo, String str) {
        return (userInfo == null || str == null || !isContactSharingAllowedForCloneProfile() || !str.equals("com.android.contacts") || areContactWritesEnabledForUser(userInfo)) ? false : true;
    }

    public final int getIsSyncable(Account account, int i, String str) {
        PackageInfo packageInfo;
        String str2;
        int isSyncable = this.mSyncStorageEngine.getIsSyncable(account, i, str);
        UserInfo userInfo = UserManager.get(this.mContext).getUserInfo(i);
        if (shouldDisableSyncForUser(userInfo, str)) {
            Log.w("SyncManager", "Account sync is disabled for account: " + account + " userId: " + i + " provider: " + str);
            return 0;
        } else if (userInfo == null || !userInfo.isRestricted()) {
            return isSyncable;
        } else {
            RegisteredServicesCache.ServiceInfo serviceInfo = this.mSyncAdapters.getServiceInfo(SyncAdapterType.newKey(str, account.type), i);
            if (serviceInfo == null) {
                return 0;
            }
            try {
                packageInfo = AppGlobals.getPackageManager().getPackageInfo(serviceInfo.componentName.getPackageName(), 0L, i);
            } catch (RemoteException unused) {
            }
            if (packageInfo == null || (str2 = packageInfo.restrictedAccountType) == null || !str2.equals(account.type)) {
                return 0;
            }
            return isSyncable;
        }
    }

    public final void setAuthorityPendingState(SyncStorageEngine.EndPoint endPoint) {
        for (SyncOperation syncOperation : getAllPendingSyncs()) {
            if (!syncOperation.isPeriodic && syncOperation.target.matchesSpec(endPoint)) {
                getSyncStorageEngine().markPending(endPoint, true);
                return;
            }
        }
        getSyncStorageEngine().markPending(endPoint, false);
    }

    public void scheduleSync(Account account, int i, int i2, String str, Bundle bundle, int i3, int i4, int i5, int i6, String str2) {
        scheduleSync(account, i, i2, str, bundle, i3, 0L, true, i4, i5, i6, str2);
    }

    /* JADX WARN: Code restructure failed: missing block: B:95:0x029c, code lost:
        if (r14.mSyncStorageEngine.getSyncAutomatically(r15.account, r15.userId, r9) != false) goto L96;
     */
    /* JADX WARN: Removed duplicated region for block: B:132:0x02ba A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:134:0x02a9 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void scheduleSync(Account account, final int i, final int i2, String str, Bundle bundle, final int i3, final long j, boolean z, final int i4, final int i5, final int i6, final String str2) {
        int i7;
        Bundle bundle2;
        char c;
        boolean z2;
        int i8;
        AccountAndUser[] accountAndUserArr;
        AccountAndUser[] accountAndUserArr2;
        String str3;
        int i9;
        SyncManager syncManager;
        AccountAndUser accountAndUser;
        String str4;
        boolean z3;
        char c2;
        Bundle bundle3;
        long j2;
        int i10;
        AccountAndUser[] accountAndUserArr3;
        Bundle bundle4 = bundle == null ? new Bundle() : bundle;
        bundle4.size();
        if (Log.isLoggable("SyncManager", 2)) {
            i7 = 2;
            bundle2 = bundle4;
            this.mLogger.log("scheduleSync: account=", account, " u", Integer.valueOf(i), " authority=", str, " reason=", Integer.valueOf(i2), " extras=", bundle2, " cuid=", Integer.valueOf(i5), " cpid=", Integer.valueOf(i6), " cpkg=", str2, " mdm=", Long.valueOf(j), " ciar=", Boolean.valueOf(z), " sef=", Integer.valueOf(i4));
        } else {
            i7 = 2;
            bundle2 = bundle4;
        }
        SyncManager syncManager2 = this;
        synchronized (syncManager2.mAccountsLock) {
            c = 65535;
            z2 = false;
            if (account != null) {
                i8 = i;
                if (i8 != -1) {
                    accountAndUserArr = new AccountAndUser[]{new AccountAndUser(account, i8)};
                } else {
                    AccountAndUser[] accountAndUserArr4 = null;
                    for (AccountAndUser accountAndUser2 : syncManager2.mRunningAccounts) {
                        if (account.equals(accountAndUser2.account)) {
                            accountAndUserArr4 = (AccountAndUser[]) ArrayUtils.appendElement(AccountAndUser.class, accountAndUserArr4, accountAndUser2);
                        }
                    }
                    accountAndUserArr2 = accountAndUserArr4;
                }
            } else {
                i8 = i;
                accountAndUserArr = syncManager2.mRunningAccounts;
            }
            accountAndUserArr2 = accountAndUserArr;
        }
        if (ArrayUtils.isEmpty(accountAndUserArr2)) {
            return;
        }
        Bundle bundle5 = bundle2;
        boolean z4 = bundle5.getBoolean("upload", false);
        boolean z5 = bundle5.getBoolean("force", false);
        if (z5) {
            bundle5.putBoolean("ignore_backoff", true);
            bundle5.putBoolean("ignore_settings", true);
        }
        boolean z6 = bundle5.getBoolean("ignore_settings", false);
        int i11 = 3;
        if (z4) {
            str3 = str;
            i9 = 1;
        } else if (z5) {
            str3 = str;
            i9 = 3;
        } else {
            str3 = str;
            if (str3 == null) {
                i9 = i7;
            } else {
                i9 = bundle5.containsKey("feed") ? 5 : 0;
            }
        }
        int length = accountAndUserArr2.length;
        int i12 = 0;
        while (i12 < length) {
            AccountAndUser accountAndUser3 = accountAndUserArr2[i12];
            if (i8 < 0 || (i10 = accountAndUser3.userId) < 0 || i8 == i10) {
                HashSet hashSet = new HashSet();
                for (RegisteredServicesCache.ServiceInfo serviceInfo : syncManager2.mSyncAdapters.getAllServices(accountAndUser3.userId)) {
                    hashSet.add(((SyncAdapterType) serviceInfo.type).authority);
                }
                if (str3 != null) {
                    boolean contains = hashSet.contains(str3);
                    hashSet.clear();
                    if (contains) {
                        hashSet.add(str3);
                    }
                }
                Iterator it = hashSet.iterator();
                while (it.hasNext()) {
                    final String str5 = (String) it.next();
                    int computeSyncable = syncManager2.computeSyncable(accountAndUser3.account, accountAndUser3.userId, str5, !z);
                    if (computeSyncable != 0) {
                        RegisteredServicesCache.ServiceInfo serviceInfo2 = syncManager2.mSyncAdapters.getServiceInfo(SyncAdapterType.newKey(str5, accountAndUser3.account.type), accountAndUser3.userId);
                        if (serviceInfo2 != null) {
                            int i13 = serviceInfo2.uid;
                            if (computeSyncable == i11) {
                                syncManager2.mLogger.log("scheduleSync: Not scheduling sync operation: isSyncable == SYNCABLE_NO_ACCOUNT_ACCESS");
                                final Bundle bundle6 = new Bundle(bundle5);
                                String packageName = serviceInfo2.componentName.getPackageName();
                                if (syncManager2.wasPackageEverLaunched(packageName, i8)) {
                                    final AccountAndUser accountAndUser4 = accountAndUser3;
                                    syncManager2.mAccountManagerInternal.requestAccountAccess(accountAndUser3.account, packageName, i, new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: com.android.server.content.SyncManager$$ExternalSyntheticLambda7
                                        public final void onResult(Bundle bundle7) {
                                            SyncManager.this.lambda$scheduleSync$4(accountAndUser4, i, i2, str5, bundle6, i3, j, i4, i5, i6, str2, bundle7);
                                        }
                                    }));
                                    c = 65535;
                                    i8 = i;
                                    accountAndUser3 = accountAndUser3;
                                    i12 = i12;
                                    length = length;
                                    bundle5 = bundle5;
                                    i11 = i11;
                                    accountAndUserArr2 = accountAndUserArr2;
                                    z2 = z2;
                                    syncManager2 = this;
                                }
                            } else {
                                AccountAndUser accountAndUser5 = accountAndUser3;
                                int i14 = i12;
                                int i15 = length;
                                Bundle bundle7 = bundle5;
                                int i16 = i11;
                                AccountAndUser[] accountAndUserArr5 = accountAndUserArr2;
                                int i17 = i8;
                                boolean z7 = z2;
                                boolean allowParallelSyncs = ((SyncAdapterType) serviceInfo2.type).allowParallelSyncs();
                                boolean isAlwaysSyncable = ((SyncAdapterType) serviceInfo2.type).isAlwaysSyncable();
                                if (z || computeSyncable >= 0 || !isAlwaysSyncable) {
                                    syncManager = this;
                                    accountAndUser = accountAndUser5;
                                } else {
                                    syncManager = this;
                                    accountAndUser = accountAndUser5;
                                    syncManager.mSyncStorageEngine.setIsSyncable(accountAndUser.account, accountAndUser.userId, str5, 1, i5, i6);
                                    computeSyncable = 1;
                                }
                                if ((i3 == -2 || i3 == computeSyncable) && (((SyncAdapterType) serviceInfo2.type).supportsUploading() || !z4)) {
                                    if (computeSyncable < 0 || z6) {
                                        str4 = str5;
                                    } else {
                                        if (syncManager.mSyncStorageEngine.getMasterSyncAutomatically(accountAndUser.userId)) {
                                            str4 = str5;
                                        } else {
                                            str4 = str5;
                                        }
                                        z3 = z7;
                                        if (z3) {
                                            syncManager.mLogger.log("scheduleSync: sync of ", accountAndUser, " ", str4, " is not allowed, dropping request");
                                        } else {
                                            syncManager.mSyncStorageEngine.getDelayUntilTime(new SyncStorageEngine.EndPoint(accountAndUser.account, str4, accountAndUser.userId));
                                            String packageName2 = serviceInfo2.componentName.getPackageName();
                                            if (computeSyncable != -1) {
                                                c2 = 65535;
                                                bundle3 = bundle7;
                                                j2 = j;
                                                if (i3 == -2 || i3 == computeSyncable) {
                                                    syncManager.mLogger.log("scheduleSync: scheduling sync ", accountAndUser, " ", str4);
                                                    syncManager.postScheduleSyncMessage(new SyncOperation(accountAndUser.account, accountAndUser.userId, i13, packageName2, i2, i9, str4, bundle3, allowParallelSyncs, i4), j);
                                                    i8 = i;
                                                    syncManager2 = syncManager;
                                                    accountAndUser3 = accountAndUser;
                                                    bundle5 = bundle3;
                                                    c = c2;
                                                    i12 = i14;
                                                    length = i15;
                                                    i11 = i16;
                                                    accountAndUserArr2 = accountAndUserArr5;
                                                    z2 = z7;
                                                } else {
                                                    syncManager.mLogger.log("scheduleSync: not handling ", accountAndUser, " ", str4);
                                                    i8 = i;
                                                    syncManager2 = syncManager;
                                                    accountAndUser3 = accountAndUser;
                                                    bundle5 = bundle3;
                                                    c = c2;
                                                    i12 = i14;
                                                    length = i15;
                                                    i11 = i16;
                                                    accountAndUserArr2 = accountAndUserArr5;
                                                    z2 = z7;
                                                }
                                            } else if (z) {
                                                final Bundle bundle8 = new Bundle(bundle7);
                                                final AccountAndUser accountAndUser6 = accountAndUser;
                                                final String str6 = str4;
                                                bundle3 = bundle7;
                                                c2 = 65535;
                                                sendOnUnsyncableAccount(syncManager.mContext, serviceInfo2, accountAndUser.userId, new OnReadyCallback() { // from class: com.android.server.content.SyncManager$$ExternalSyntheticLambda8
                                                    @Override // com.android.server.content.SyncManager.OnReadyCallback
                                                    public final void onReady() {
                                                        SyncManager.this.lambda$scheduleSync$5(accountAndUser6, i2, str6, bundle8, i3, j, i4, i5, i6, str2);
                                                    }
                                                });
                                                syncManager = this;
                                                accountAndUser = accountAndUser;
                                                i8 = i;
                                                syncManager2 = syncManager;
                                                accountAndUser3 = accountAndUser;
                                                bundle5 = bundle3;
                                                c = c2;
                                                i12 = i14;
                                                length = i15;
                                                i11 = i16;
                                                accountAndUserArr2 = accountAndUserArr5;
                                                z2 = z7;
                                            } else {
                                                c2 = 65535;
                                                bundle3 = bundle7;
                                                Bundle bundle9 = new Bundle();
                                                bundle9.putBoolean("initialize", true);
                                                syncManager = this;
                                                syncManager.mLogger.log("scheduleSync: schedule initialisation sync ", accountAndUser, " ", str4);
                                                j2 = j;
                                                syncManager.postScheduleSyncMessage(new SyncOperation(accountAndUser.account, accountAndUser.userId, i13, packageName2, i2, i9, str4, bundle9, allowParallelSyncs, i4), j2);
                                                i8 = i;
                                                syncManager2 = syncManager;
                                                accountAndUser3 = accountAndUser;
                                                bundle5 = bundle3;
                                                c = c2;
                                                i12 = i14;
                                                length = i15;
                                                i11 = i16;
                                                accountAndUserArr2 = accountAndUserArr5;
                                                z2 = z7;
                                            }
                                        }
                                    }
                                    z3 = true;
                                    if (z3) {
                                    }
                                }
                                i8 = i17;
                                syncManager2 = syncManager;
                                accountAndUser3 = accountAndUser;
                                i12 = i14;
                                length = i15;
                                bundle5 = bundle7;
                                i11 = i16;
                                accountAndUserArr2 = accountAndUserArr5;
                                z2 = z7;
                                c = 65535;
                            }
                        }
                    }
                }
            }
            i12++;
            i8 = i;
            str3 = str;
            syncManager2 = syncManager2;
            bundle5 = bundle5;
            c = c;
            length = length;
            i11 = i11;
            accountAndUserArr2 = accountAndUserArr2;
            z2 = z2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleSync$4(AccountAndUser accountAndUser, int i, int i2, String str, Bundle bundle, int i3, long j, int i4, int i5, int i6, String str2, Bundle bundle2) {
        if (bundle2 == null || !bundle2.getBoolean("booleanResult")) {
            return;
        }
        scheduleSync(accountAndUser.account, i, i2, str, bundle, i3, j, true, i4, i5, i6, str2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleSync$5(AccountAndUser accountAndUser, int i, String str, Bundle bundle, int i2, long j, int i3, int i4, int i5, String str2) {
        scheduleSync(accountAndUser.account, accountAndUser.userId, i, str, bundle, i2, j, false, i3, i4, i5, str2);
    }

    public int computeSyncable(Account account, int i, String str, boolean z) {
        int isSyncable = getIsSyncable(account, i, str);
        if (isSyncable == 0) {
            return 0;
        }
        RegisteredServicesCache.ServiceInfo serviceInfo = this.mSyncAdapters.getServiceInfo(SyncAdapterType.newKey(str, account.type), i);
        if (serviceInfo == null) {
            return 0;
        }
        int i2 = serviceInfo.uid;
        String packageName = serviceInfo.componentName.getPackageName();
        if (this.mAmi.isAppStartModeDisabled(i2, packageName)) {
            Slog.w("SyncManager", "Not scheduling job " + serviceInfo.uid + XmlUtils.STRING_ARRAY_SEPARATOR + serviceInfo.componentName + " -- package not allowed to start");
            return 0;
        } else if (!z || canAccessAccount(account, packageName, i2)) {
            return isSyncable;
        } else {
            Log.w("SyncManager", "Access to " + SyncLogger.logSafe(account) + " denied for package " + packageName + " in UID " + serviceInfo.uid);
            return 3;
        }
    }

    public final boolean canAccessAccount(Account account, String str, int i) {
        if (this.mAccountManager.hasAccountAccess(account, str, UserHandle.getUserHandleForUid(i))) {
            return true;
        }
        try {
            this.mContext.getPackageManager().getApplicationInfoAsUser(str, 1048576, UserHandle.getUserId(i));
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public final void removeSyncsForAuthority(SyncStorageEngine.EndPoint endPoint, String str) {
        this.mLogger.log("removeSyncsForAuthority: ", endPoint, str);
        verifyJobScheduler();
        for (SyncOperation syncOperation : getAllPendingSyncs()) {
            if (syncOperation.target.matchesSpec(endPoint)) {
                this.mLogger.log("canceling: ", syncOperation);
                cancelJob(syncOperation, str);
            }
        }
    }

    public void removePeriodicSync(SyncStorageEngine.EndPoint endPoint, Bundle bundle, String str) {
        Message obtainMessage = this.mSyncHandler.obtainMessage(14, Pair.create(endPoint, str));
        obtainMessage.setData(bundle);
        obtainMessage.sendToTarget();
    }

    public void updateOrAddPeriodicSync(SyncStorageEngine.EndPoint endPoint, long j, long j2, Bundle bundle) {
        this.mSyncHandler.obtainMessage(13, new UpdatePeriodicSyncMessagePayload(endPoint, j, j2, bundle)).sendToTarget();
    }

    public List<PeriodicSync> getPeriodicSyncs(SyncStorageEngine.EndPoint endPoint) {
        List<SyncOperation> allPendingSyncs = getAllPendingSyncs();
        ArrayList arrayList = new ArrayList();
        for (SyncOperation syncOperation : allPendingSyncs) {
            if (syncOperation.isPeriodic && syncOperation.target.matchesSpec(endPoint)) {
                SyncStorageEngine.EndPoint endPoint2 = syncOperation.target;
                arrayList.add(new PeriodicSync(endPoint2.account, endPoint2.provider, syncOperation.getClonedExtras(), syncOperation.periodMillis / 1000, syncOperation.flexMillis / 1000));
            }
        }
        return arrayList;
    }

    public void scheduleLocalSync(Account account, int i, int i2, String str, int i3, int i4, int i5, String str2) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("upload", true);
        scheduleSync(account, i, i2, str, bundle, -2, LOCAL_SYNC_DELAY, true, i3, i4, i5, str2);
    }

    public SyncAdapterType[] getSyncAdapterTypes(int i, int i2) {
        Collection<RegisteredServicesCache.ServiceInfo> allServices = this.mSyncAdapters.getAllServices(i2);
        ArrayList arrayList = new ArrayList(allServices.size());
        for (RegisteredServicesCache.ServiceInfo serviceInfo : allServices) {
            String packageName = ((SyncAdapterType) serviceInfo.type).getPackageName();
            if (TextUtils.isEmpty(packageName) || !this.mPackageManagerInternal.filterAppAccess(packageName, i, i2)) {
                arrayList.add((SyncAdapterType) serviceInfo.type);
            }
        }
        return (SyncAdapterType[]) arrayList.toArray(new SyncAdapterType[0]);
    }

    public String[] getSyncAdapterPackagesForAuthorityAsUser(String str, int i, int i2) {
        String[] syncAdapterPackagesForAuthority = this.mSyncAdapters.getSyncAdapterPackagesForAuthority(str, i2);
        ArrayList arrayList = new ArrayList(syncAdapterPackagesForAuthority.length);
        for (String str2 : syncAdapterPackagesForAuthority) {
            if (!TextUtils.isEmpty(str2) && !this.mPackageManagerInternal.filterAppAccess(str2, i, i2)) {
                arrayList.add(str2);
            }
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    public String getSyncAdapterPackageAsUser(String str, String str2, int i, int i2) {
        RegisteredServicesCache.ServiceInfo serviceInfo;
        if (str == null || str2 == null || (serviceInfo = this.mSyncAdapters.getServiceInfo(SyncAdapterType.newKey(str2, str), i2)) == null) {
            return null;
        }
        String packageName = ((SyncAdapterType) serviceInfo.type).getPackageName();
        if (TextUtils.isEmpty(packageName) || this.mPackageManagerInternal.filterAppAccess(packageName, i, i2)) {
            return null;
        }
        return packageName;
    }

    public final void sendSyncFinishedOrCanceledMessage(ActiveSyncContext activeSyncContext, SyncResult syncResult) {
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "sending MESSAGE_SYNC_FINISHED");
        }
        Message obtainMessage = this.mSyncHandler.obtainMessage();
        obtainMessage.what = 1;
        obtainMessage.obj = new SyncFinishedOrCancelledMessagePayload(activeSyncContext, syncResult);
        this.mSyncHandler.sendMessage(obtainMessage);
    }

    public final void sendCancelSyncsMessage(SyncStorageEngine.EndPoint endPoint, Bundle bundle, String str) {
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "sending MESSAGE_CANCEL");
        }
        this.mLogger.log("sendCancelSyncsMessage() ep=", endPoint, " why=", str);
        Message obtainMessage = this.mSyncHandler.obtainMessage();
        obtainMessage.what = 6;
        obtainMessage.setData(bundle);
        obtainMessage.obj = endPoint;
        this.mSyncHandler.sendMessage(obtainMessage);
    }

    public final void postMonitorSyncProgressMessage(ActiveSyncContext activeSyncContext) {
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "posting MESSAGE_SYNC_MONITOR in 60s");
        }
        activeSyncContext.mBytesTransferredAtLastPoll = getTotalBytesTransferredByUid(activeSyncContext.mSyncAdapterUid);
        activeSyncContext.mLastPolledTimeElapsed = SystemClock.elapsedRealtime();
        this.mSyncHandler.sendMessageDelayed(this.mSyncHandler.obtainMessage(8, activeSyncContext), 60000L);
    }

    public final void postScheduleSyncMessage(SyncOperation syncOperation, long j) {
        this.mSyncHandler.obtainMessage(12, new ScheduleSyncMessagePayload(syncOperation, j)).sendToTarget();
    }

    public final long getTotalBytesTransferredByUid(int i) {
        return TrafficStats.getUidRxBytes(i) + TrafficStats.getUidTxBytes(i);
    }

    /* loaded from: classes.dex */
    public class SyncFinishedOrCancelledMessagePayload {
        public final ActiveSyncContext activeSyncContext;
        public final SyncResult syncResult;

        public SyncFinishedOrCancelledMessagePayload(ActiveSyncContext activeSyncContext, SyncResult syncResult) {
            this.activeSyncContext = activeSyncContext;
            this.syncResult = syncResult;
        }
    }

    /* loaded from: classes.dex */
    public class UpdatePeriodicSyncMessagePayload {
        public final Bundle extras;
        public final long flex;
        public final long pollFrequency;
        public final SyncStorageEngine.EndPoint target;

        public UpdatePeriodicSyncMessagePayload(SyncStorageEngine.EndPoint endPoint, long j, long j2, Bundle bundle) {
            this.target = endPoint;
            this.pollFrequency = j;
            this.flex = j2;
            this.extras = bundle;
        }
    }

    /* loaded from: classes.dex */
    public static class ScheduleSyncMessagePayload {
        public final long minDelayMillis;
        public final SyncOperation syncOperation;

        public ScheduleSyncMessagePayload(SyncOperation syncOperation, long j) {
            this.syncOperation = syncOperation;
            this.minDelayMillis = j;
        }
    }

    public final void clearBackoffSetting(SyncStorageEngine.EndPoint endPoint, String str) {
        Pair<Long, Long> backoff = this.mSyncStorageEngine.getBackoff(endPoint);
        if (backoff != null && ((Long) backoff.first).longValue() == -1 && ((Long) backoff.second).longValue() == -1) {
            return;
        }
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "Clearing backoffs for " + endPoint);
        }
        this.mSyncStorageEngine.setBackoff(endPoint, -1L, -1L);
        rescheduleSyncs(endPoint, str);
    }

    public final void increaseBackoffSetting(SyncStorageEngine.EndPoint endPoint) {
        long j;
        long elapsedRealtime = SystemClock.elapsedRealtime();
        Pair<Long, Long> backoff = this.mSyncStorageEngine.getBackoff(endPoint);
        if (backoff == null) {
            j = -1;
        } else if (elapsedRealtime < ((Long) backoff.first).longValue()) {
            if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "Still in backoff, do not increase it. Remaining: " + ((((Long) backoff.first).longValue() - elapsedRealtime) / 1000) + " seconds.");
                return;
            }
            return;
        } else {
            j = ((float) ((Long) backoff.second).longValue()) * this.mConstants.getRetryTimeIncreaseFactor();
        }
        if (j <= 0) {
            long initialSyncRetryTimeInSeconds = this.mConstants.getInitialSyncRetryTimeInSeconds() * 1000;
            j = jitterize(initialSyncRetryTimeInSeconds, (long) (initialSyncRetryTimeInSeconds * 1.1d));
        }
        long maxSyncRetryTimeInSeconds = this.mConstants.getMaxSyncRetryTimeInSeconds() * 1000;
        long j2 = j > maxSyncRetryTimeInSeconds ? maxSyncRetryTimeInSeconds : j;
        long j3 = elapsedRealtime + j2;
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "Backoff until: " + j3 + ", delayTime: " + j2);
        }
        this.mSyncStorageEngine.setBackoff(endPoint, j3, j2);
        rescheduleSyncs(endPoint, "increaseBackoffSetting");
    }

    public final void rescheduleSyncs(SyncStorageEngine.EndPoint endPoint, String str) {
        this.mLogger.log("rescheduleSyncs() ep=", endPoint, " why=", str);
        int i = 0;
        for (SyncOperation syncOperation : getAllPendingSyncs()) {
            if (!syncOperation.isPeriodic && syncOperation.target.matchesSpec(endPoint)) {
                i++;
                cancelJob(syncOperation, str);
                postScheduleSyncMessage(syncOperation, 0L);
            }
        }
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "Rescheduled " + i + " syncs for " + endPoint);
        }
    }

    public final void setDelayUntilTime(SyncStorageEngine.EndPoint endPoint, long j) {
        long j2 = j * 1000;
        long currentTimeMillis = System.currentTimeMillis();
        long elapsedRealtime = j2 > currentTimeMillis ? SystemClock.elapsedRealtime() + (j2 - currentTimeMillis) : 0L;
        this.mSyncStorageEngine.setDelayUntilTime(endPoint, elapsedRealtime);
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "Delay Until time set to " + elapsedRealtime + " for " + endPoint);
        }
        rescheduleSyncs(endPoint, "delayUntil newDelayUntilTime: " + elapsedRealtime);
    }

    public final boolean isAdapterDelayed(SyncStorageEngine.EndPoint endPoint) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        Pair<Long, Long> backoff = this.mSyncStorageEngine.getBackoff(endPoint);
        return !(backoff == null || ((Long) backoff.first).longValue() == -1 || ((Long) backoff.first).longValue() <= elapsedRealtime) || this.mSyncStorageEngine.getDelayUntilTime(endPoint) > elapsedRealtime;
    }

    public void cancelActiveSync(SyncStorageEngine.EndPoint endPoint, Bundle bundle, String str) {
        sendCancelSyncsMessage(endPoint, bundle, str);
    }

    public final void scheduleSyncOperationH(SyncOperation syncOperation) {
        scheduleSyncOperationH(syncOperation, 0L);
    }

    /* JADX WARN: Removed duplicated region for block: B:72:0x019d  */
    /* JADX WARN: Removed duplicated region for block: B:82:0x01d9  */
    /* JADX WARN: Removed duplicated region for block: B:85:0x01e1  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void scheduleSyncOperationH(SyncOperation syncOperation, long j) {
        long j2;
        long j3;
        boolean z;
        DeviceIdleInternal deviceIdleInternal;
        int i;
        long j4;
        boolean isLoggable = Log.isLoggable("SyncManager", 2);
        if (syncOperation == null) {
            Slog.e("SyncManager", "Can't schedule null sync operation.");
            return;
        }
        int i2 = 1;
        if (syncOperation.hasIgnoreBackoff()) {
            j2 = j;
        } else {
            Pair<Long, Long> backoff = this.mSyncStorageEngine.getBackoff(syncOperation.target);
            if (backoff == null) {
                Slog.e("SyncManager", "Couldn't find backoff values for " + SyncLogger.logSafe(syncOperation.target));
                backoff = new Pair<>(-1L, -1L);
            } else if (((Long) backoff.first).longValue() != -1) {
                syncOperation.scheduleEjAsRegularJob = true;
            }
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long longValue = ((Long) backoff.first).longValue() == -1 ? 0L : ((Long) backoff.first).longValue() - elapsedRealtime;
            long delayUntilTime = this.mSyncStorageEngine.getDelayUntilTime(syncOperation.target);
            long j5 = delayUntilTime > elapsedRealtime ? delayUntilTime - elapsedRealtime : 0L;
            if (isLoggable) {
                Slog.v("SyncManager", "backoff delay:" + longValue + " delayUntil delay:" + j5);
            }
            j2 = Math.max(j, Math.max(longValue, j5));
        }
        int i3 = (j2 > 0L ? 1 : (j2 == 0L ? 0 : -1));
        if (i3 < 0) {
            j2 = 0;
        } else if (i3 > 0) {
            syncOperation.scheduleEjAsRegularJob = true;
        }
        if (syncOperation.isPeriodic) {
            j3 = j2;
        } else {
            Iterator<ActiveSyncContext> it = this.mActiveSyncContexts.iterator();
            while (it.hasNext()) {
                if (it.next().mSyncOperation.key.equals(syncOperation.key)) {
                    if (isLoggable) {
                        Log.v("SyncManager", "Duplicate sync is already running. Not scheduling " + syncOperation);
                        return;
                    }
                    return;
                }
            }
            syncOperation.expectedRuntime = SystemClock.elapsedRealtime() + j2;
            List<SyncOperation> allPendingSyncs = getAllPendingSyncs();
            SyncOperation syncOperation2 = syncOperation;
            int i4 = 0;
            for (SyncOperation syncOperation3 : allPendingSyncs) {
                if (!syncOperation3.isPeriodic) {
                    if (syncOperation3.key.equals(syncOperation.key)) {
                        j4 = j2;
                        if (syncOperation2.expectedRuntime > syncOperation3.expectedRuntime) {
                            syncOperation2 = syncOperation3;
                        }
                        i4++;
                    } else {
                        j4 = j2;
                    }
                    j2 = j4;
                    i2 = 1;
                }
            }
            j3 = j2;
            if (i4 > i2) {
                StringBuilder sb = new StringBuilder();
                sb.append("duplicates found when scheduling a sync operation: owningUid=");
                sb.append(syncOperation.owningUid);
                sb.append("; owningPackage=");
                sb.append(syncOperation.owningPackage);
                sb.append("; source=");
                sb.append(syncOperation.syncSource);
                sb.append("; adapter=");
                SyncStorageEngine.EndPoint endPoint = syncOperation.target;
                sb.append(endPoint != null ? endPoint.provider : "unknown");
                Slog.wtf("SyncManager", sb.toString());
            }
            if (syncOperation != syncOperation2 && j3 == 0) {
                int i5 = syncOperation2.syncExemptionFlag;
                int i6 = syncOperation.syncExemptionFlag;
                if (i5 < i6) {
                    syncOperation2 = syncOperation;
                    i = Math.max(0, i6);
                    for (SyncOperation syncOperation4 : allPendingSyncs) {
                        if (!syncOperation4.isPeriodic && syncOperation4.key.equals(syncOperation.key) && syncOperation4 != syncOperation2) {
                            if (isLoggable) {
                                Slog.v("SyncManager", "Cancelling duplicate sync " + syncOperation4);
                            }
                            i = Math.max(i, syncOperation4.syncExemptionFlag);
                            cancelJob(syncOperation4, "scheduleSyncOperationH-duplicate");
                        }
                    }
                    if (syncOperation2 == syncOperation) {
                        if (isLoggable) {
                            Slog.v("SyncManager", "Not scheduling because a duplicate exists.");
                            return;
                        }
                        return;
                    } else if (i > 0) {
                        syncOperation.syncExemptionFlag = i;
                    }
                }
            }
            i = 0;
            while (r5.hasNext()) {
            }
            if (syncOperation2 == syncOperation) {
            }
        }
        if (syncOperation.jobId == -1) {
            syncOperation.jobId = getUnusedJobIdH();
        }
        if (isLoggable) {
            Slog.v("SyncManager", "scheduling sync operation " + syncOperation.toString());
        }
        JobInfo.Builder flags = new JobInfo.Builder(syncOperation.jobId, new ComponentName(this.mContext, SyncJobService.class)).setExtras(syncOperation.toJobInfoExtras()).setRequiredNetworkType(syncOperation.isNotAllowedOnMetered() ? 2 : 1).setRequiresStorageNotLow(true).setPersisted(true).setBias(syncOperation.getJobBias()).setFlags(syncOperation.isAppStandbyExempted() ? 8 : 0);
        if (syncOperation.isPeriodic) {
            flags.setPeriodic(syncOperation.periodMillis, syncOperation.flexMillis);
            z = true;
        } else {
            if (j3 > 0) {
                flags.setMinimumLatency(j3);
            }
            z = true;
            getSyncStorageEngine().markPending(syncOperation.target, true);
        }
        if (syncOperation.hasRequireCharging()) {
            flags.setRequiresCharging(z);
        }
        if (syncOperation.isScheduledAsExpeditedJob() && !syncOperation.scheduleEjAsRegularJob) {
            flags.setExpedited(z);
        }
        if (syncOperation.syncExemptionFlag == 2 && (deviceIdleInternal = (DeviceIdleInternal) LocalServices.getService(DeviceIdleInternal.class)) != null) {
            deviceIdleInternal.addPowerSaveTempWhitelistApp(1000, syncOperation.owningPackage, this.mConstants.getKeyExemptionTempWhitelistDurationInSeconds() * 1000, 1, UserHandle.getUserId(syncOperation.owningUid), false, 306, "sync by top app");
        }
        UsageStatsManagerInternal usageStatsManagerInternal = (UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class);
        if (usageStatsManagerInternal != null) {
            usageStatsManagerInternal.reportSyncScheduled(syncOperation.owningPackage, UserHandle.getUserId(syncOperation.owningUid), syncOperation.isAppStandbyExempted());
        }
        JobInfo build = flags.build();
        int scheduleAsPackage = getJobScheduler().scheduleAsPackage(build, syncOperation.owningPackage, syncOperation.target.userId, syncOperation.wakeLockName());
        if (scheduleAsPackage == 0 && build.isExpedited()) {
            if (isLoggable) {
                Slog.i("SyncManager", "Failed to schedule EJ for " + syncOperation.owningPackage + ". Downgrading to regular");
            }
            syncOperation.scheduleEjAsRegularJob = true;
            flags.setExpedited(false).setExtras(syncOperation.toJobInfoExtras());
            scheduleAsPackage = getJobScheduler().scheduleAsPackage(flags.build(), syncOperation.owningPackage, syncOperation.target.userId, syncOperation.wakeLockName());
        }
        if (scheduleAsPackage == 0) {
            Slog.e("SyncManager", "Failed to schedule job for " + syncOperation.owningPackage);
        }
    }

    public void clearScheduledSyncOperations(SyncStorageEngine.EndPoint endPoint) {
        for (SyncOperation syncOperation : getAllPendingSyncs()) {
            if (!syncOperation.isPeriodic && syncOperation.target.matchesSpec(endPoint)) {
                cancelJob(syncOperation, "clearScheduledSyncOperations");
                getSyncStorageEngine().markPending(syncOperation.target, false);
            }
        }
        this.mSyncStorageEngine.setBackoff(endPoint, -1L, -1L);
    }

    public void cancelScheduledSyncOperation(SyncStorageEngine.EndPoint endPoint, Bundle bundle) {
        for (SyncOperation syncOperation : getAllPendingSyncs()) {
            if (!syncOperation.isPeriodic && syncOperation.target.matchesSpec(endPoint) && syncOperation.areExtrasEqual(bundle, false)) {
                cancelJob(syncOperation, "cancelScheduledSyncOperation");
            }
        }
        setAuthorityPendingState(endPoint);
        if (this.mSyncStorageEngine.isSyncPending(endPoint)) {
            return;
        }
        this.mSyncStorageEngine.setBackoff(endPoint, -1L, -1L);
    }

    public final void maybeRescheduleSync(SyncResult syncResult, SyncOperation syncOperation) {
        boolean isLoggable = Log.isLoggable("SyncManager", 3);
        if (isLoggable) {
            Log.d("SyncManager", "encountered error(s) during the sync: " + syncResult + ", " + syncOperation);
        }
        syncOperation.enableBackoff();
        syncOperation.scheduleEjAsRegularJob = true;
        if (syncOperation.hasDoNotRetry() && !syncResult.syncAlreadyInProgress) {
            if (isLoggable) {
                Log.d("SyncManager", "not retrying sync operation because SYNC_EXTRAS_DO_NOT_RETRY was specified " + syncOperation);
            }
        } else if (syncOperation.isUpload() && !syncResult.syncAlreadyInProgress) {
            syncOperation.enableTwoWaySync();
            if (isLoggable) {
                Log.d("SyncManager", "retrying sync operation as a two-way sync because an upload-only sync encountered an error: " + syncOperation);
            }
            scheduleSyncOperationH(syncOperation);
        } else if (syncResult.tooManyRetries) {
            if (isLoggable) {
                Log.d("SyncManager", "not retrying sync operation because it retried too many times: " + syncOperation);
            }
        } else if (syncResult.madeSomeProgress()) {
            if (isLoggable) {
                Log.d("SyncManager", "retrying sync operation because even though it had an error it achieved some success");
            }
            scheduleSyncOperationH(syncOperation);
        } else if (syncResult.syncAlreadyInProgress) {
            if (isLoggable) {
                Log.d("SyncManager", "retrying sync operation that failed because there was already a sync in progress: " + syncOperation);
            }
            scheduleSyncOperationH(syncOperation, 10000L);
        } else if (syncResult.hasSoftError()) {
            if (isLoggable) {
                Log.d("SyncManager", "retrying sync operation because it encountered a soft error: " + syncOperation);
            }
            scheduleSyncOperationH(syncOperation);
        } else {
            Log.e("SyncManager", "not retrying sync operation because the error is a hard error: " + SyncLogger.logSafe(syncOperation));
        }
    }

    public final void onUserUnlocked(int i) {
        AccountManagerService.getSingleton().validateAccounts(i);
        this.mSyncAdapters.invalidateCache(i);
        updateRunningAccounts(new SyncStorageEngine.EndPoint(null, null, i));
        for (Account account : AccountManagerService.getSingleton().getAccounts(i, this.mContext.getOpPackageName())) {
            scheduleSync(account, i, -8, null, null, -1, 0, Process.myUid(), -3, null);
        }
    }

    public final void onUserStopped(int i) {
        updateRunningAccounts(null);
        cancelActiveSync(new SyncStorageEngine.EndPoint(null, null, i), null, "onUserStopped");
    }

    public final void onUserRemoved(int i) {
        this.mLogger.log("onUserRemoved: u", Integer.valueOf(i));
        updateRunningAccounts(null);
        this.mSyncStorageEngine.removeStaleAccounts(null, i);
        for (SyncOperation syncOperation : getAllPendingSyncs()) {
            if (syncOperation.target.userId == i) {
                cancelJob(syncOperation, "user removed u" + i);
            }
        }
    }

    public static Intent getAdapterBindIntent(Context context, ComponentName componentName, int i) {
        Intent intent = new Intent();
        intent.setAction("android.content.SyncAdapter");
        intent.setComponent(componentName);
        intent.putExtra("android.intent.extra.client_label", 17041628);
        intent.putExtra("android.intent.extra.client_intent", PendingIntent.getActivityAsUser(context, 0, new Intent("android.settings.SYNC_SETTINGS"), 67108864, null, UserHandle.of(i)));
        return intent;
    }

    /* loaded from: classes.dex */
    public class ActiveSyncContext extends ISyncContext.Stub implements ServiceConnection, IBinder.DeathRecipient {
        public boolean mBound;
        public long mBytesTransferredAtLastPoll;
        public String mEventName;
        public final long mHistoryRowId;
        public long mLastPolledTimeElapsed;
        public final long mStartTime;
        public final int mSyncAdapterUid;
        public SyncInfo mSyncInfo;
        public final SyncOperation mSyncOperation;
        public final PowerManager.WakeLock mSyncWakeLock;
        public long mTimeoutStartTime;
        public boolean mIsLinkedToDeath = false;
        public ISyncAdapter mSyncAdapter = null;

        public void sendHeartbeat() {
        }

        public ActiveSyncContext(SyncOperation syncOperation, long j, int i) {
            this.mSyncAdapterUid = i;
            this.mSyncOperation = syncOperation;
            this.mHistoryRowId = j;
            long elapsedRealtime = SystemClock.elapsedRealtime();
            this.mStartTime = elapsedRealtime;
            this.mTimeoutStartTime = elapsedRealtime;
            PowerManager.WakeLock syncWakeLock = SyncManager.this.mSyncHandler.getSyncWakeLock(syncOperation);
            this.mSyncWakeLock = syncWakeLock;
            syncWakeLock.setWorkSource(new WorkSource(i));
            syncWakeLock.acquire();
        }

        public void onFinished(SyncResult syncResult) {
            if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "onFinished: " + this);
            }
            SyncLogger syncLogger = SyncManager.this.mLogger;
            Object[] objArr = new Object[4];
            objArr[0] = "onFinished result=";
            objArr[1] = syncResult;
            objArr[2] = " endpoint=";
            SyncOperation syncOperation = this.mSyncOperation;
            objArr[3] = syncOperation == null ? "null" : syncOperation.target;
            syncLogger.log(objArr);
            SyncManager.this.sendSyncFinishedOrCanceledMessage(this, syncResult);
        }

        public void toString(StringBuilder sb, boolean z) {
            sb.append("startTime ");
            sb.append(this.mStartTime);
            sb.append(", mTimeoutStartTime ");
            sb.append(this.mTimeoutStartTime);
            sb.append(", mHistoryRowId ");
            sb.append(this.mHistoryRowId);
            sb.append(", syncOperation ");
            SyncOperation syncOperation = this.mSyncOperation;
            String str = syncOperation;
            if (z) {
                str = SyncLogger.logSafe(syncOperation);
            }
            sb.append(str);
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Message obtainMessage = SyncManager.this.mSyncHandler.obtainMessage();
            obtainMessage.what = 4;
            obtainMessage.obj = new ServiceConnectionData(this, iBinder);
            SyncManager.this.mSyncHandler.sendMessage(obtainMessage);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Message obtainMessage = SyncManager.this.mSyncHandler.obtainMessage();
            obtainMessage.what = 5;
            obtainMessage.obj = new ServiceConnectionData(this, null);
            SyncManager.this.mSyncHandler.sendMessage(obtainMessage);
        }

        public boolean bindToSyncAdapter(ComponentName componentName, int i) {
            if (Log.isLoggable("SyncManager", 2)) {
                Log.d("SyncManager", "bindToSyncAdapter: " + componentName + ", connection " + this);
            }
            Intent adapterBindIntent = SyncManager.getAdapterBindIntent(SyncManager.this.mContext, componentName, i);
            this.mBound = true;
            boolean bindServiceAsUser = SyncManager.this.mContext.bindServiceAsUser(adapterBindIntent, this, 21, new UserHandle(this.mSyncOperation.target.userId));
            SyncManager.this.mLogger.log("bindService() returned=", Boolean.valueOf(this.mBound), " for ", this);
            if (!bindServiceAsUser) {
                this.mBound = false;
            } else {
                try {
                    this.mEventName = this.mSyncOperation.wakeLockName();
                    SyncManager.this.mBatteryStats.noteSyncStart(this.mEventName, this.mSyncAdapterUid);
                } catch (RemoteException unused) {
                }
            }
            return bindServiceAsUser;
        }

        public void close() {
            if (Log.isLoggable("SyncManager", 2)) {
                Log.d("SyncManager", "unBindFromSyncAdapter: connection " + this);
            }
            if (this.mBound) {
                this.mBound = false;
                SyncManager.this.mLogger.log("unbindService for ", this);
                SyncManager.this.mContext.unbindService(this);
                try {
                    SyncManager.this.mBatteryStats.noteSyncFinish(this.mEventName, this.mSyncAdapterUid);
                } catch (RemoteException unused) {
                }
            }
            this.mSyncWakeLock.release();
            this.mSyncWakeLock.setWorkSource(null);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            toString(sb, false);
            return sb.toString();
        }

        public String toSafeString() {
            StringBuilder sb = new StringBuilder();
            toString(sb, true);
            return sb.toString();
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            SyncManager.this.sendSyncFinishedOrCanceledMessage(this, null);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, boolean z) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        dumpSyncState(indentingPrintWriter, new SyncAdapterStateFetcher());
        this.mConstants.dump(printWriter, "");
        dumpSyncAdapters(indentingPrintWriter);
        if (z) {
            indentingPrintWriter.println("Detailed Sync History");
            this.mLogger.dumpAll(printWriter);
        }
    }

    public static String formatTime(long j) {
        return j == 0 ? "N/A" : TimeMigrationUtils.formatMillisWithFixedFormat(j);
    }

    public static /* synthetic */ int lambda$static$6(SyncOperation syncOperation, SyncOperation syncOperation2) {
        int compare = Integer.compare(syncOperation.target.userId, syncOperation2.target.userId);
        if (compare != 0) {
            return compare;
        }
        Comparator comparator = String.CASE_INSENSITIVE_ORDER;
        int compare2 = comparator.compare(syncOperation.target.account.type, syncOperation2.target.account.type);
        if (compare2 != 0) {
            return compare2;
        }
        int compare3 = comparator.compare(syncOperation.target.account.name, syncOperation2.target.account.name);
        if (compare3 != 0) {
            return compare3;
        }
        int compare4 = comparator.compare(syncOperation.target.provider, syncOperation2.target.provider);
        if (compare4 != 0) {
            return compare4;
        }
        int compare5 = Integer.compare(syncOperation.reason, syncOperation2.reason);
        if (compare5 != 0) {
            return compare5;
        }
        int compare6 = Long.compare(syncOperation.periodMillis, syncOperation2.periodMillis);
        if (compare6 != 0) {
            return compare6;
        }
        int compare7 = Long.compare(syncOperation.expectedRuntime, syncOperation2.expectedRuntime);
        if (compare7 != 0) {
            return compare7;
        }
        int compare8 = Long.compare(syncOperation.jobId, syncOperation2.jobId);
        if (compare8 != 0) {
            return compare8;
        }
        return 0;
    }

    public static /* synthetic */ int lambda$static$7(SyncOperation syncOperation, SyncOperation syncOperation2) {
        int compare = Long.compare(syncOperation.expectedRuntime, syncOperation2.expectedRuntime);
        return compare != 0 ? compare : sOpDumpComparator.compare(syncOperation, syncOperation2);
    }

    public static <T> int countIf(Collection<T> collection, Predicate<T> predicate) {
        int i = 0;
        for (T t : collection) {
            if (predicate.test(t)) {
                i++;
            }
        }
        return i;
    }

    public void dumpPendingSyncs(PrintWriter printWriter, SyncAdapterStateFetcher syncAdapterStateFetcher) {
        List<SyncOperation> allPendingSyncs = getAllPendingSyncs();
        printWriter.print("Pending Syncs: ");
        printWriter.println(countIf(allPendingSyncs, new Predicate() { // from class: com.android.server.content.SyncManager$$ExternalSyntheticLambda11
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$dumpPendingSyncs$8;
                lambda$dumpPendingSyncs$8 = SyncManager.lambda$dumpPendingSyncs$8((SyncOperation) obj);
                return lambda$dumpPendingSyncs$8;
            }
        }));
        Collections.sort(allPendingSyncs, sOpRuntimeComparator);
        for (SyncOperation syncOperation : allPendingSyncs) {
            if (!syncOperation.isPeriodic) {
                printWriter.println(syncOperation.dump(null, false, syncAdapterStateFetcher, false));
            }
        }
        printWriter.println();
    }

    public static /* synthetic */ boolean lambda$dumpPendingSyncs$8(SyncOperation syncOperation) {
        return !syncOperation.isPeriodic;
    }

    public void dumpPeriodicSyncs(PrintWriter printWriter, SyncAdapterStateFetcher syncAdapterStateFetcher) {
        List<SyncOperation> allPendingSyncs = getAllPendingSyncs();
        printWriter.print("Periodic Syncs: ");
        printWriter.println(countIf(allPendingSyncs, new Predicate() { // from class: com.android.server.content.SyncManager$$ExternalSyntheticLambda13
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean z;
                z = ((SyncOperation) obj).isPeriodic;
                return z;
            }
        }));
        Collections.sort(allPendingSyncs, sOpDumpComparator);
        for (SyncOperation syncOperation : allPendingSyncs) {
            if (syncOperation.isPeriodic) {
                printWriter.println(syncOperation.dump(null, false, syncAdapterStateFetcher, false));
            }
        }
        printWriter.println();
    }

    public static StringBuilder formatDurationHMS(StringBuilder sb, long j) {
        boolean z;
        long j2 = j / 1000;
        if (j2 < 0) {
            sb.append('-');
            j2 = -j2;
        }
        long j3 = j2 % 60;
        long j4 = j2 / 60;
        long j5 = j4 % 60;
        long j6 = j4 / 60;
        long j7 = j6 % 24;
        long j8 = j6 / 24;
        if (j8 > 0) {
            sb.append(j8);
            sb.append('d');
            z = true;
        } else {
            z = false;
        }
        if (!printTwoDigitNumber(sb, j3, 's', printTwoDigitNumber(sb, j5, 'm', printTwoDigitNumber(sb, j7, 'h', z)))) {
            sb.append("0s");
        }
        return sb;
    }

    public static boolean printTwoDigitNumber(StringBuilder sb, long j, char c, boolean z) {
        if (z || j != 0) {
            if (z && j < 10) {
                sb.append('0');
            }
            sb.append(j);
            sb.append(c);
            return true;
        }
        return false;
    }

    @NeverCompile
    public void dumpSyncState(PrintWriter printWriter, SyncAdapterStateFetcher syncAdapterStateFetcher) {
        boolean z;
        final StringBuilder sb = new StringBuilder();
        printWriter.print("Data connected: ");
        printWriter.println(this.mDataConnectionIsConnected);
        printWriter.print("Battery saver: ");
        PowerManager powerManager = this.mPowerManager;
        char c = 1;
        int i = 0;
        printWriter.println(powerManager != null && powerManager.isPowerSaveMode());
        printWriter.print("Background network restriction: ");
        ConnectivityManager connectivityManager = getConnectivityManager();
        int restrictBackgroundStatus = connectivityManager == null ? -1 : connectivityManager.getRestrictBackgroundStatus();
        if (restrictBackgroundStatus == 1) {
            printWriter.println(" disabled");
        } else if (restrictBackgroundStatus == 2) {
            printWriter.println(" whitelisted");
        } else if (restrictBackgroundStatus == 3) {
            printWriter.println(" enabled");
        } else {
            printWriter.print("Unknown(");
            printWriter.print(restrictBackgroundStatus);
            printWriter.println(")");
        }
        printWriter.print("Auto sync: ");
        List<UserInfo> allUsers = getAllUsers();
        if (allUsers != null) {
            for (UserInfo userInfo : allUsers) {
                printWriter.print("u" + userInfo.id + "=" + this.mSyncStorageEngine.getMasterSyncAutomatically(userInfo.id) + " ");
            }
            printWriter.println();
        }
        Intent registerReceiver = this.mContext.registerReceiver(null, new IntentFilter("android.intent.action.DEVICE_STORAGE_LOW"));
        printWriter.print("Storage low: ");
        printWriter.println(registerReceiver != null);
        printWriter.print("Clock valid: ");
        printWriter.println(this.mSyncStorageEngine.isClockValid());
        AccountAndUser[] allAccountsForSystemProcess = AccountManagerService.getSingleton().getAllAccountsForSystemProcess();
        printWriter.print("Accounts: ");
        if (allAccountsForSystemProcess != INITIAL_ACCOUNTS_ARRAY) {
            printWriter.println(allAccountsForSystemProcess.length);
        } else {
            printWriter.println("not known yet");
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        printWriter.print("Now: ");
        printWriter.print(elapsedRealtime);
        printWriter.println(" (" + formatTime(System.currentTimeMillis()) + ")");
        sb.setLength(0);
        printWriter.print("Uptime: ");
        printWriter.print(formatDurationHMS(sb, elapsedRealtime));
        printWriter.println();
        printWriter.print("Time spent syncing: ");
        sb.setLength(0);
        printWriter.print(formatDurationHMS(sb, this.mSyncHandler.mSyncTimeTracker.timeSpentSyncing()));
        printWriter.print(", sync ");
        printWriter.print(this.mSyncHandler.mSyncTimeTracker.mLastWasSyncing ? "" : "not ");
        printWriter.println("in progress");
        printWriter.println();
        printWriter.println("Active Syncs: " + this.mActiveSyncContexts.size());
        PackageManager packageManager = this.mContext.getPackageManager();
        Iterator<ActiveSyncContext> it = this.mActiveSyncContexts.iterator();
        while (it.hasNext()) {
            ActiveSyncContext next = it.next();
            printWriter.print("  ");
            sb.setLength(0);
            printWriter.print(formatDurationHMS(sb, elapsedRealtime - next.mStartTime));
            printWriter.print(" - ");
            printWriter.print(next.mSyncOperation.dump(packageManager, false, syncAdapterStateFetcher, false));
            printWriter.println();
        }
        printWriter.println();
        dumpPendingSyncs(printWriter, syncAdapterStateFetcher);
        dumpPeriodicSyncs(printWriter, syncAdapterStateFetcher);
        printWriter.println("Sync Status");
        ArrayList arrayList = new ArrayList();
        this.mSyncStorageEngine.resetTodayStats(false);
        int length = allAccountsForSystemProcess.length;
        int i2 = 0;
        while (i2 < length) {
            AccountAndUser accountAndUser = allAccountsForSystemProcess[i2];
            synchronized (this.mUnlockedUsers) {
                z = this.mUnlockedUsers.get(accountAndUser.userId);
            }
            Object[] objArr = new Object[4];
            objArr[i] = accountAndUser.account.name;
            objArr[c] = Integer.valueOf(accountAndUser.userId);
            objArr[2] = accountAndUser.account.type;
            objArr[3] = z ? "" : " (locked)";
            printWriter.printf("Account %s u%d %s%s\n", objArr);
            printWriter.println("=======================================================================");
            final PrintTable printTable = new PrintTable(16);
            printTable.set(i, i, "Authority", "Syncable", "Enabled", "Stats", "Loc", "Poll", "Per", "Feed", "User", "Othr", "Tot", "Fail", "Can", "Time", "Last Sync", "Backoff");
            ArrayList newArrayList = Lists.newArrayList();
            newArrayList.addAll(this.mSyncAdapters.getAllServices(accountAndUser.userId));
            Collections.sort(newArrayList, new Comparator<RegisteredServicesCache.ServiceInfo<SyncAdapterType>>() { // from class: com.android.server.content.SyncManager.12
                @Override // java.util.Comparator
                public int compare(RegisteredServicesCache.ServiceInfo<SyncAdapterType> serviceInfo, RegisteredServicesCache.ServiceInfo<SyncAdapterType> serviceInfo2) {
                    return ((SyncAdapterType) serviceInfo.type).authority.compareTo(((SyncAdapterType) serviceInfo2.type).authority);
                }
            });
            Iterator it2 = newArrayList.iterator();
            while (it2.hasNext()) {
                RegisteredServicesCache.ServiceInfo serviceInfo = (RegisteredServicesCache.ServiceInfo) it2.next();
                if (((SyncAdapterType) serviceInfo.type).accountType.equals(accountAndUser.account.type)) {
                    int numRows = printTable.getNumRows();
                    AccountAndUser[] accountAndUserArr = allAccountsForSystemProcess;
                    Iterator it3 = it2;
                    Pair<SyncStorageEngine.AuthorityInfo, SyncStatusInfo> copyOfAuthorityWithSyncStatus = this.mSyncStorageEngine.getCopyOfAuthorityWithSyncStatus(new SyncStorageEngine.EndPoint(accountAndUser.account, ((SyncAdapterType) serviceInfo.type).authority, accountAndUser.userId));
                    SyncStorageEngine.AuthorityInfo authorityInfo = (SyncStorageEngine.AuthorityInfo) copyOfAuthorityWithSyncStatus.first;
                    SyncStatusInfo syncStatusInfo = (SyncStatusInfo) copyOfAuthorityWithSyncStatus.second;
                    arrayList.add(Pair.create(authorityInfo.target, syncStatusInfo));
                    String str = authorityInfo.target.provider;
                    if (str.length() > 50) {
                        str = str.substring(str.length() - 50);
                    }
                    printTable.set(numRows, 0, str, Integer.valueOf(authorityInfo.syncable), Boolean.valueOf(authorityInfo.enabled));
                    QuadConsumer quadConsumer = new QuadConsumer() { // from class: com.android.server.content.SyncManager$$ExternalSyntheticLambda3
                        public final void accept(Object obj, Object obj2, Object obj3, Object obj4) {
                            SyncManager.lambda$dumpSyncState$10(sb, printTable, (String) obj, (SyncStatusInfo.Stats) obj2, (Function) obj3, (Integer) obj4);
                        }
                    };
                    StringBuilder sb2 = sb;
                    int i3 = length;
                    quadConsumer.accept("Total", syncStatusInfo.totalStats, new Function() { // from class: com.android.server.content.SyncManager$$ExternalSyntheticLambda4
                        @Override // java.util.function.Function
                        public final Object apply(Object obj) {
                            String lambda$dumpSyncState$11;
                            lambda$dumpSyncState$11 = SyncManager.lambda$dumpSyncState$11((Integer) obj);
                            return lambda$dumpSyncState$11;
                        }
                    }, Integer.valueOf(numRows));
                    int i4 = numRows + 1;
                    AccountAndUser accountAndUser2 = accountAndUser;
                    quadConsumer.accept("Today", syncStatusInfo.todayStats, new Function() { // from class: com.android.server.content.SyncManager$$ExternalSyntheticLambda5
                        @Override // java.util.function.Function
                        public final Object apply(Object obj) {
                            String zeroToEmpty;
                            zeroToEmpty = SyncManager.this.zeroToEmpty(((Integer) obj).intValue());
                            return zeroToEmpty;
                        }
                    }, Integer.valueOf(i4));
                    quadConsumer.accept("Yestr", syncStatusInfo.yesterdayStats, new Function() { // from class: com.android.server.content.SyncManager$$ExternalSyntheticLambda5
                        @Override // java.util.function.Function
                        public final Object apply(Object obj) {
                            String zeroToEmpty;
                            zeroToEmpty = SyncManager.this.zeroToEmpty(((Integer) obj).intValue());
                            return zeroToEmpty;
                        }
                    }, Integer.valueOf(numRows + 2));
                    if (authorityInfo.delayUntil > elapsedRealtime) {
                        printTable.set(numRows, 15, "D: " + ((authorityInfo.delayUntil - elapsedRealtime) / 1000));
                        if (authorityInfo.backoffTime > elapsedRealtime) {
                            printTable.set(i4, 15, "B: " + ((authorityInfo.backoffTime - elapsedRealtime) / 1000));
                            printTable.set(i4 + 1, 15, Long.valueOf(authorityInfo.backoffDelay / 1000));
                        }
                    }
                    if (syncStatusInfo.lastSuccessTime != 0) {
                        printTable.set(numRows, 14, SyncStorageEngine.SOURCES[syncStatusInfo.lastSuccessSource] + " SUCCESS");
                        numRows = i4 + 1;
                        printTable.set(i4, 14, formatTime(syncStatusInfo.lastSuccessTime));
                    }
                    if (syncStatusInfo.lastFailureTime != 0) {
                        int i5 = numRows + 1;
                        printTable.set(numRows, 14, SyncStorageEngine.SOURCES[syncStatusInfo.lastFailureSource] + " FAILURE");
                        printTable.set(i5, 14, formatTime(syncStatusInfo.lastFailureTime));
                        printTable.set(i5 + 1, 14, syncStatusInfo.lastFailureMesg);
                    }
                    allAccountsForSystemProcess = accountAndUserArr;
                    it2 = it3;
                    sb = sb2;
                    length = i3;
                    accountAndUser = accountAndUser2;
                }
            }
            printTable.writeTo(printWriter);
            i2++;
            c = 1;
            i = 0;
        }
        dumpSyncHistory(printWriter);
        printWriter.println();
        printWriter.println("Per Adapter History");
        printWriter.println("(SERVER is now split up to FEED and OTHER)");
        for (int i6 = 0; i6 < arrayList.size(); i6++) {
            Pair pair = (Pair) arrayList.get(i6);
            printWriter.print("  ");
            printWriter.print(((SyncStorageEngine.EndPoint) pair.first).account.name);
            printWriter.print('/');
            printWriter.print(((SyncStorageEngine.EndPoint) pair.first).account.type);
            printWriter.print(" u");
            printWriter.print(((SyncStorageEngine.EndPoint) pair.first).userId);
            printWriter.print(" [");
            printWriter.print(((SyncStorageEngine.EndPoint) pair.first).provider);
            printWriter.print("]");
            printWriter.println();
            printWriter.println("    Per source last syncs:");
            int i7 = 0;
            while (true) {
                String[] strArr = SyncStorageEngine.SOURCES;
                if (i7 >= strArr.length) {
                    break;
                }
                printWriter.print("      ");
                printWriter.print(String.format("%8s", strArr[i7]));
                printWriter.print("  Success: ");
                printWriter.print(formatTime(((SyncStatusInfo) pair.second).perSourceLastSuccessTimes[i7]));
                printWriter.print("  Failure: ");
                printWriter.println(formatTime(((SyncStatusInfo) pair.second).perSourceLastFailureTimes[i7]));
                i7++;
            }
            printWriter.println("    Last syncs:");
            for (int i8 = 0; i8 < ((SyncStatusInfo) pair.second).getEventCount(); i8++) {
                printWriter.print("      ");
                printWriter.print(formatTime(((SyncStatusInfo) pair.second).getEventTime(i8)));
                printWriter.print(' ');
                printWriter.print(((SyncStatusInfo) pair.second).getEvent(i8));
                printWriter.println();
            }
            if (((SyncStatusInfo) pair.second).getEventCount() == 0) {
                printWriter.println("      N/A");
            }
        }
    }

    public static /* synthetic */ void lambda$dumpSyncState$10(StringBuilder sb, PrintTable printTable, String str, SyncStatusInfo.Stats stats, Function function, Integer num) {
        sb.setLength(0);
        printTable.set(num.intValue(), 3, str, function.apply(Integer.valueOf(stats.numSourceLocal)), function.apply(Integer.valueOf(stats.numSourcePoll)), function.apply(Integer.valueOf(stats.numSourcePeriodic)), function.apply(Integer.valueOf(stats.numSourceFeed)), function.apply(Integer.valueOf(stats.numSourceUser)), function.apply(Integer.valueOf(stats.numSourceOther)), function.apply(Integer.valueOf(stats.numSyncs)), function.apply(Integer.valueOf(stats.numFailures)), function.apply(Integer.valueOf(stats.numCancels)), formatDurationHMS(sb, stats.totalElapsedTime));
    }

    public static /* synthetic */ String lambda$dumpSyncState$11(Integer num) {
        return Integer.toString(num.intValue());
    }

    public final String zeroToEmpty(int i) {
        return i != 0 ? Integer.toString(i) : "";
    }

    public final void dumpTimeSec(PrintWriter printWriter, long j) {
        printWriter.print(j / 1000);
        printWriter.print('.');
        printWriter.print((j / 100) % 10);
        printWriter.print('s');
    }

    public final void dumpDayStatistic(PrintWriter printWriter, SyncStorageEngine.DayStats dayStats) {
        printWriter.print("Success (");
        printWriter.print(dayStats.successCount);
        if (dayStats.successCount > 0) {
            printWriter.print(" for ");
            dumpTimeSec(printWriter, dayStats.successTime);
            printWriter.print(" avg=");
            dumpTimeSec(printWriter, dayStats.successTime / dayStats.successCount);
        }
        printWriter.print(") Failure (");
        printWriter.print(dayStats.failureCount);
        if (dayStats.failureCount > 0) {
            printWriter.print(" for ");
            dumpTimeSec(printWriter, dayStats.failureTime);
            printWriter.print(" avg=");
            dumpTimeSec(printWriter, dayStats.failureTime / dayStats.failureCount);
        }
        printWriter.println(")");
    }

    public void dumpSyncHistory(PrintWriter printWriter) {
        dumpRecentHistory(printWriter);
        dumpDayStatistics(printWriter);
    }

    /* JADX WARN: Code restructure failed: missing block: B:58:0x036c, code lost:
        if (r9.downstreamActivity != 0) goto L64;
     */
    /* JADX WARN: Removed duplicated region for block: B:55:0x0360  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x036f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void dumpRecentHistory(PrintWriter printWriter) {
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        String str7;
        String str8;
        String str9;
        int i;
        String format;
        String valueOf;
        int i2;
        String str10;
        int i3;
        ArrayList<SyncStorageEngine.SyncHistoryItem> arrayList;
        String str11;
        String str12;
        SyncManager syncManager = this;
        ArrayList<SyncStorageEngine.SyncHistoryItem> syncHistory = syncManager.mSyncStorageEngine.getSyncHistory();
        if (syncHistory == null || syncHistory.size() <= 0) {
            return;
        }
        HashMap newHashMap = Maps.newHashMap();
        int size = syncHistory.size();
        Iterator<SyncStorageEngine.SyncHistoryItem> it = syncHistory.iterator();
        long j = 0;
        long j2 = 0;
        int i4 = 0;
        int i5 = 0;
        while (true) {
            str = " u";
            str2 = "/";
            if (!it.hasNext()) {
                break;
            }
            SyncStorageEngine.SyncHistoryItem next = it.next();
            Iterator<SyncStorageEngine.SyncHistoryItem> it2 = it;
            SyncStorageEngine.AuthorityInfo authority = syncManager.mSyncStorageEngine.getAuthority(next.authorityId);
            if (authority != null) {
                String str13 = authority.target.provider;
                StringBuilder sb = new StringBuilder();
                arrayList = syncHistory;
                sb.append(authority.target.account.name);
                sb.append("/");
                sb.append(authority.target.account.type);
                sb.append(" u");
                sb.append(authority.target.userId);
                str12 = sb.toString();
                str11 = str13;
            } else {
                arrayList = syncHistory;
                str11 = "Unknown";
                str12 = str11;
            }
            int length = str11.length();
            if (length > i4) {
                i4 = length;
            }
            int length2 = str12.length();
            if (length2 > i5) {
                i5 = length2;
            }
            long j3 = next.elapsedTime;
            long j4 = j + j3;
            j2++;
            AuthoritySyncStats authoritySyncStats = (AuthoritySyncStats) newHashMap.get(str11);
            if (authoritySyncStats == null) {
                authoritySyncStats = new AuthoritySyncStats(str11);
                newHashMap.put(str11, authoritySyncStats);
            }
            authoritySyncStats.elapsedTime += j3;
            authoritySyncStats.times++;
            Map<String, AccountSyncStats> map = authoritySyncStats.accountMap;
            AccountSyncStats accountSyncStats = map.get(str12);
            if (accountSyncStats == null) {
                accountSyncStats = new AccountSyncStats(str12);
                map.put(str12, accountSyncStats);
            }
            accountSyncStats.elapsedTime += j3;
            accountSyncStats.times++;
            it = it2;
            syncHistory = arrayList;
            j = j4;
        }
        ArrayList<SyncStorageEngine.SyncHistoryItem> arrayList2 = syncHistory;
        if (j > 0) {
            printWriter.println();
            printWriter.printf("Detailed Statistics (Recent history):  %d (# of times) %ds (sync time)\n", Long.valueOf(j2), Long.valueOf(j / 1000));
            ArrayList arrayList3 = new ArrayList(newHashMap.values());
            Collections.sort(arrayList3, new Comparator<AuthoritySyncStats>() { // from class: com.android.server.content.SyncManager.13
                @Override // java.util.Comparator
                public int compare(AuthoritySyncStats authoritySyncStats2, AuthoritySyncStats authoritySyncStats3) {
                    int compare = Integer.compare(authoritySyncStats3.times, authoritySyncStats2.times);
                    return compare == 0 ? Long.compare(authoritySyncStats3.elapsedTime, authoritySyncStats2.elapsedTime) : compare;
                }
            });
            int max = Math.max(i4, i5 + 3);
            char[] cArr = new char[max + 4 + 2 + 10 + 11];
            Arrays.fill(cArr, '-');
            String str14 = new String(cArr);
            String format2 = String.format("  %%-%ds: %%-9s  %%-11s\n", Integer.valueOf(max + 2));
            String format3 = String.format("    %%-%ds:   %%-9s  %%-11s\n", Integer.valueOf(max));
            printWriter.println(str14);
            Iterator it3 = arrayList3.iterator();
            while (it3.hasNext()) {
                AuthoritySyncStats authoritySyncStats2 = (AuthoritySyncStats) it3.next();
                Iterator it4 = it3;
                String str15 = authoritySyncStats2.name;
                String str16 = str;
                String str17 = str2;
                long j5 = authoritySyncStats2.elapsedTime;
                int i6 = size;
                int i7 = i4;
                String str18 = "%ds/%d%%";
                String str19 = format3;
                printWriter.printf(format2, str15, String.format("%d/%d%%", Integer.valueOf(authoritySyncStats2.times), Long.valueOf((i3 * 100) / j2)), String.format("%ds/%d%%", Long.valueOf(j5 / 1000), Long.valueOf((j5 * 100) / j)));
                ArrayList arrayList4 = new ArrayList(authoritySyncStats2.accountMap.values());
                Collections.sort(arrayList4, new Comparator<AccountSyncStats>() { // from class: com.android.server.content.SyncManager.14
                    @Override // java.util.Comparator
                    public int compare(AccountSyncStats accountSyncStats2, AccountSyncStats accountSyncStats3) {
                        int compare = Integer.compare(accountSyncStats3.times, accountSyncStats2.times);
                        return compare == 0 ? Long.compare(accountSyncStats3.elapsedTime, accountSyncStats2.elapsedTime) : compare;
                    }
                });
                Iterator it5 = arrayList4.iterator();
                while (it5.hasNext()) {
                    AccountSyncStats accountSyncStats2 = (AccountSyncStats) it5.next();
                    String str20 = format2;
                    long j6 = accountSyncStats2.elapsedTime;
                    int i8 = accountSyncStats2.times;
                    Iterator it6 = it5;
                    printWriter.printf(str19, accountSyncStats2.name, String.format("%d/%d%%", Integer.valueOf(i8), Long.valueOf((i8 * 100) / j2)), String.format(str18, Long.valueOf(j6 / 1000), Long.valueOf((j6 * 100) / j)));
                    format2 = str20;
                    str18 = str18;
                    it5 = it6;
                }
                format3 = str19;
                printWriter.println(str14);
                it3 = it4;
                str2 = str17;
                str = str16;
                size = i6;
                i4 = i7;
            }
        }
        int i9 = size;
        String str21 = str;
        String str22 = str2;
        printWriter.println();
        printWriter.println("Recent Sync History");
        String str23 = "(SERVER is now split up to FEED and OTHER)";
        printWriter.println("(SERVER is now split up to FEED and OTHER)");
        String str24 = "  %-" + i5 + "s  %-" + i4 + "s %s\n";
        HashMap newHashMap2 = Maps.newHashMap();
        PackageManager packageManager = syncManager.mContext.getPackageManager();
        int i10 = i9;
        int i11 = 0;
        while (i11 < i10) {
            ArrayList<SyncStorageEngine.SyncHistoryItem> arrayList5 = arrayList2;
            SyncStorageEngine.SyncHistoryItem syncHistoryItem = arrayList5.get(i11);
            SyncStorageEngine.AuthorityInfo authority2 = syncManager.mSyncStorageEngine.getAuthority(syncHistoryItem.authorityId);
            if (authority2 != null) {
                str9 = authority2.target.provider;
                StringBuilder sb2 = new StringBuilder();
                sb2.append(authority2.target.account.name);
                str7 = str22;
                sb2.append(str7);
                sb2.append(authority2.target.account.type);
                sb2.append(str21);
                sb2.append(authority2.target.userId);
                str8 = sb2.toString();
            } else {
                str7 = str22;
                str8 = "Unknown";
                str9 = str8;
            }
            long j7 = syncHistoryItem.elapsedTime;
            String str25 = str23;
            String str26 = str24;
            long j8 = syncHistoryItem.eventTime;
            String str27 = str9 + str7 + str8;
            Long l = (Long) newHashMap2.get(str27);
            if (l == null) {
                valueOf = "";
            } else {
                long longValue = (l.longValue() - j8) / 1000;
                if (longValue < 60) {
                    valueOf = String.valueOf(longValue);
                } else {
                    if (longValue < 3600) {
                        str22 = str7;
                        arrayList2 = arrayList5;
                        i = i10;
                        format = String.format("%02d:%02d", Long.valueOf(longValue / 60), Long.valueOf(longValue % 60));
                    } else {
                        arrayList2 = arrayList5;
                        str22 = str7;
                        long j9 = longValue % 3600;
                        i = i10;
                        format = String.format("%02d:%02d:%02d", Long.valueOf(longValue / 3600), Long.valueOf(j9 / 60), Long.valueOf(j9 % 60));
                    }
                    newHashMap2.put(str27, Long.valueOf(j8));
                    i11++;
                    printWriter.printf("  #%-3d: %s %8s  %5.1fs  %8s", Integer.valueOf(i11), formatTime(j8), SyncStorageEngine.SOURCES[syncHistoryItem.source], Float.valueOf(((float) j7) / 1000.0f), format);
                    printWriter.printf(str26, str8, str9, SyncOperation.reasonToString(packageManager, syncHistoryItem.reason));
                    i2 = syncHistoryItem.event;
                    if (i2 == 1) {
                        if (syncHistoryItem.upstreamActivity != 0) {
                        }
                    }
                    printWriter.printf("    event=%d upstreamActivity=%d downstreamActivity=%d\n", Integer.valueOf(i2), Long.valueOf(syncHistoryItem.upstreamActivity), Long.valueOf(syncHistoryItem.downstreamActivity));
                    str10 = syncHistoryItem.mesg;
                    if (str10 != null && !"success".equals(str10)) {
                        printWriter.printf("    mesg=%s\n", syncHistoryItem.mesg);
                    }
                    syncManager = this;
                    str24 = str26;
                    str23 = str25;
                    i10 = i;
                }
            }
            i = i10;
            str22 = str7;
            format = valueOf;
            arrayList2 = arrayList5;
            newHashMap2.put(str27, Long.valueOf(j8));
            i11++;
            printWriter.printf("  #%-3d: %s %8s  %5.1fs  %8s", Integer.valueOf(i11), formatTime(j8), SyncStorageEngine.SOURCES[syncHistoryItem.source], Float.valueOf(((float) j7) / 1000.0f), format);
            printWriter.printf(str26, str8, str9, SyncOperation.reasonToString(packageManager, syncHistoryItem.reason));
            i2 = syncHistoryItem.event;
            if (i2 == 1) {
            }
            printWriter.printf("    event=%d upstreamActivity=%d downstreamActivity=%d\n", Integer.valueOf(i2), Long.valueOf(syncHistoryItem.upstreamActivity), Long.valueOf(syncHistoryItem.downstreamActivity));
            str10 = syncHistoryItem.mesg;
            if (str10 != null) {
                printWriter.printf("    mesg=%s\n", syncHistoryItem.mesg);
            }
            syncManager = this;
            str24 = str26;
            str23 = str25;
            i10 = i;
        }
        String str28 = str23;
        String str29 = str24;
        int i12 = i10;
        printWriter.println();
        printWriter.println("Recent Sync History Extras");
        printWriter.println(str28);
        int i13 = 0;
        while (i13 < i12) {
            ArrayList<SyncStorageEngine.SyncHistoryItem> arrayList6 = arrayList2;
            SyncStorageEngine.SyncHistoryItem syncHistoryItem2 = arrayList6.get(i13);
            Bundle bundle = syncHistoryItem2.extras;
            if (bundle == null || bundle.size() == 0) {
                str3 = str22;
                str4 = str21;
            } else {
                SyncStorageEngine.AuthorityInfo authority3 = this.mSyncStorageEngine.getAuthority(syncHistoryItem2.authorityId);
                if (authority3 != null) {
                    str6 = authority3.target.provider;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(authority3.target.account.name);
                    str3 = str22;
                    sb3.append(str3);
                    sb3.append(authority3.target.account.type);
                    str4 = str21;
                    sb3.append(str4);
                    sb3.append(authority3.target.userId);
                    str5 = sb3.toString();
                } else {
                    str3 = str22;
                    str4 = str21;
                    str5 = "Unknown";
                    str6 = str5;
                }
                printWriter.printf("  #%-3d: %s %8s ", Integer.valueOf(i13 + 1), formatTime(syncHistoryItem2.eventTime), SyncStorageEngine.SOURCES[syncHistoryItem2.source]);
                printWriter.printf(str29, str5, str6, bundle);
            }
            i13++;
            arrayList2 = arrayList6;
            str22 = str3;
            str21 = str4;
        }
    }

    public final void dumpDayStatistics(PrintWriter printWriter) {
        SyncStorageEngine.DayStats dayStats;
        int i;
        SyncStorageEngine.DayStats[] dayStatistics = this.mSyncStorageEngine.getDayStatistics();
        if (dayStatistics == null || dayStatistics[0] == null) {
            return;
        }
        printWriter.println();
        printWriter.println("Sync Statistics");
        printWriter.print("  Today:  ");
        dumpDayStatistic(printWriter, dayStatistics[0]);
        int i2 = dayStatistics[0].day;
        int i3 = 1;
        while (i3 <= 6 && i3 < dayStatistics.length && (dayStats = dayStatistics[i3]) != null && (i = i2 - dayStats.day) <= 6) {
            printWriter.print("  Day-");
            printWriter.print(i);
            printWriter.print(":  ");
            dumpDayStatistic(printWriter, dayStats);
            i3++;
        }
        int i4 = i2;
        while (i3 < dayStatistics.length) {
            i4 -= 7;
            SyncStorageEngine.DayStats dayStats2 = null;
            while (true) {
                if (i3 >= dayStatistics.length) {
                    break;
                }
                SyncStorageEngine.DayStats dayStats3 = dayStatistics[i3];
                if (dayStats3 == null) {
                    i3 = dayStatistics.length;
                    break;
                } else if (i4 - dayStats3.day > 6) {
                    break;
                } else {
                    i3++;
                    if (dayStats2 == null) {
                        dayStats2 = new SyncStorageEngine.DayStats(i4);
                    }
                    dayStats2.successCount += dayStats3.successCount;
                    dayStats2.successTime += dayStats3.successTime;
                    dayStats2.failureCount += dayStats3.failureCount;
                    dayStats2.failureTime += dayStats3.failureTime;
                }
            }
            if (dayStats2 != null) {
                printWriter.print("  Week-");
                printWriter.print((i2 - i4) / 7);
                printWriter.print(": ");
                dumpDayStatistic(printWriter, dayStats2);
            }
        }
    }

    public final void dumpSyncAdapters(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.println();
        List<UserInfo> allUsers = getAllUsers();
        if (allUsers != null) {
            for (UserInfo userInfo : allUsers) {
                indentingPrintWriter.println("Sync adapters for " + userInfo + XmlUtils.STRING_ARRAY_SEPARATOR);
                indentingPrintWriter.increaseIndent();
                for (RegisteredServicesCache.ServiceInfo serviceInfo : this.mSyncAdapters.getAllServices(userInfo.id)) {
                    indentingPrintWriter.println(serviceInfo);
                }
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.println();
            }
        }
    }

    /* loaded from: classes.dex */
    public static class AuthoritySyncStats {
        public Map<String, AccountSyncStats> accountMap;
        public long elapsedTime;
        public String name;
        public int times;

        public AuthoritySyncStats(String str) {
            this.accountMap = Maps.newHashMap();
            this.name = str;
        }
    }

    /* loaded from: classes.dex */
    public static class AccountSyncStats {
        public long elapsedTime;
        public String name;
        public int times;

        public AccountSyncStats(String str) {
            this.name = str;
        }
    }

    public static void sendOnUnsyncableAccount(final Context context, RegisteredServicesCache.ServiceInfo<SyncAdapterType> serviceInfo, int i, OnReadyCallback onReadyCallback) {
        final OnUnsyncableAccountCheck onUnsyncableAccountCheck = new OnUnsyncableAccountCheck(serviceInfo, onReadyCallback);
        if (context.bindServiceAsUser(getAdapterBindIntent(context, serviceInfo.componentName, i), onUnsyncableAccountCheck, 21, UserHandle.of(i))) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: com.android.server.content.SyncManager$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    context.unbindService(onUnsyncableAccountCheck);
                }
            }, 5000L);
        } else {
            onUnsyncableAccountCheck.onReady();
        }
    }

    /* loaded from: classes.dex */
    public static class OnUnsyncableAccountCheck implements ServiceConnection {
        public final OnReadyCallback mOnReadyCallback;
        public final RegisteredServicesCache.ServiceInfo<SyncAdapterType> mSyncAdapterInfo;

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
        }

        public OnUnsyncableAccountCheck(RegisteredServicesCache.ServiceInfo<SyncAdapterType> serviceInfo, OnReadyCallback onReadyCallback) {
            this.mSyncAdapterInfo = serviceInfo;
            this.mOnReadyCallback = onReadyCallback;
        }

        public final void onReady() {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                this.mOnReadyCallback.onReady();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                ISyncAdapter.Stub.asInterface(iBinder).onUnsyncableAccount(new ISyncAdapterUnsyncableAccountCallback.Stub() { // from class: com.android.server.content.SyncManager.OnUnsyncableAccountCheck.1
                    public void onUnsyncableAccountDone(boolean z) {
                        if (z) {
                            OnUnsyncableAccountCheck.this.onReady();
                        }
                    }
                });
            } catch (RemoteException e) {
                Slog.e("SyncManager", "Could not call onUnsyncableAccountDone " + this.mSyncAdapterInfo, e);
                onReady();
            }
        }
    }

    /* loaded from: classes.dex */
    public class SyncTimeTracker {
        public boolean mLastWasSyncing;
        public long mTimeSpentSyncing;
        public long mWhenSyncStarted;

        public SyncTimeTracker() {
            this.mLastWasSyncing = false;
            this.mWhenSyncStarted = 0L;
        }

        public synchronized void update() {
            boolean z = !SyncManager.this.mActiveSyncContexts.isEmpty();
            if (z == this.mLastWasSyncing) {
                return;
            }
            long elapsedRealtime = SystemClock.elapsedRealtime();
            if (z) {
                this.mWhenSyncStarted = elapsedRealtime;
            } else {
                this.mTimeSpentSyncing += elapsedRealtime - this.mWhenSyncStarted;
            }
            this.mLastWasSyncing = z;
        }

        public synchronized long timeSpentSyncing() {
            if (!this.mLastWasSyncing) {
                return this.mTimeSpentSyncing;
            }
            long elapsedRealtime = SystemClock.elapsedRealtime();
            return this.mTimeSpentSyncing + (elapsedRealtime - this.mWhenSyncStarted);
        }
    }

    /* loaded from: classes.dex */
    public class ServiceConnectionData {
        public final ActiveSyncContext activeSyncContext;
        public final IBinder adapter;

        public ServiceConnectionData(ActiveSyncContext activeSyncContext, IBinder iBinder) {
            this.activeSyncContext = activeSyncContext;
            this.adapter = iBinder;
        }
    }

    public static SyncManager getInstance() {
        SyncManager syncManager;
        synchronized (SyncManager.class) {
            if (sInstance == null) {
                Slog.wtf("SyncManager", "sInstance == null");
            }
            syncManager = sInstance;
        }
        return syncManager;
    }

    public static boolean readyToSync(int i) {
        SyncManager syncManager = getInstance();
        return syncManager != null && SyncJobService.isReady() && syncManager.mProvisioned && syncManager.isUserUnlocked(i);
    }

    public static void sendMessage(Message message) {
        SyncManager syncManager = getInstance();
        if (syncManager != null) {
            syncManager.mSyncHandler.sendMessage(message);
        }
    }

    /* loaded from: classes.dex */
    public class SyncHandler extends Handler {
        public final SyncTimeTracker mSyncTimeTracker;
        public final HashMap<String, PowerManager.WakeLock> mWakeLocks;

        public SyncHandler(Looper looper) {
            super(looper);
            this.mSyncTimeTracker = new SyncTimeTracker();
            this.mWakeLocks = Maps.newHashMap();
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            SyncManager.this.mSyncManagerWakeLock.acquire();
            try {
                handleSyncMessage(message);
            } finally {
                SyncManager.this.mSyncManagerWakeLock.release();
            }
        }

        public final void handleSyncMessage(Message message) {
            boolean isLoggable = Log.isLoggable("SyncManager", 2);
            try {
                SyncManager syncManager = SyncManager.this;
                syncManager.mDataConnectionIsConnected = syncManager.readDataConnectionState();
                int i = message.what;
                boolean z = true;
                if (i == 1) {
                    SyncFinishedOrCancelledMessagePayload syncFinishedOrCancelledMessagePayload = (SyncFinishedOrCancelledMessagePayload) message.obj;
                    if (SyncManager.this.isSyncStillActiveH(syncFinishedOrCancelledMessagePayload.activeSyncContext)) {
                        if (isLoggable) {
                            Slog.v("SyncManager", "syncFinished" + syncFinishedOrCancelledMessagePayload.activeSyncContext.mSyncOperation);
                        }
                        SyncJobService.callJobFinished(syncFinishedOrCancelledMessagePayload.activeSyncContext.mSyncOperation.jobId, false, "sync finished");
                        runSyncFinishedOrCanceledH(syncFinishedOrCancelledMessagePayload.syncResult, syncFinishedOrCancelledMessagePayload.activeSyncContext);
                    } else if (isLoggable) {
                        Log.d("SyncManager", "handleSyncHandlerMessage: dropping since the sync is no longer active: " + syncFinishedOrCancelledMessagePayload.activeSyncContext);
                    }
                } else if (i == 4) {
                    ServiceConnectionData serviceConnectionData = (ServiceConnectionData) message.obj;
                    if (isLoggable) {
                        Log.d("SyncManager", "handleSyncHandlerMessage: MESSAGE_SERVICE_CONNECTED: " + serviceConnectionData.activeSyncContext);
                    }
                    if (SyncManager.this.isSyncStillActiveH(serviceConnectionData.activeSyncContext)) {
                        runBoundToAdapterH(serviceConnectionData.activeSyncContext, serviceConnectionData.adapter);
                    }
                } else if (i == 5) {
                    ActiveSyncContext activeSyncContext = ((ServiceConnectionData) message.obj).activeSyncContext;
                    if (isLoggable) {
                        Log.d("SyncManager", "handleSyncHandlerMessage: MESSAGE_SERVICE_DISCONNECTED: " + activeSyncContext);
                    }
                    if (SyncManager.this.isSyncStillActiveH(activeSyncContext)) {
                        try {
                            if (activeSyncContext.mSyncAdapter != null) {
                                SyncManager.this.mLogger.log("Calling cancelSync for SERVICE_DISCONNECTED ", activeSyncContext, " adapter=", activeSyncContext.mSyncAdapter);
                                activeSyncContext.mSyncAdapter.cancelSync(activeSyncContext);
                                SyncManager.this.mLogger.log("Canceled");
                            }
                        } catch (RemoteException e) {
                            SyncManager.this.mLogger.log("RemoteException ", Log.getStackTraceString(e));
                        }
                        SyncResult syncResult = new SyncResult();
                        syncResult.stats.numIoExceptions++;
                        SyncJobService.callJobFinished(activeSyncContext.mSyncOperation.jobId, false, "service disconnected");
                        runSyncFinishedOrCanceledH(syncResult, activeSyncContext);
                    }
                } else if (i == 6) {
                    SyncStorageEngine.EndPoint endPoint = (SyncStorageEngine.EndPoint) message.obj;
                    Bundle peekData = message.peekData();
                    if (isLoggable) {
                        Log.d("SyncManager", "handleSyncHandlerMessage: MESSAGE_CANCEL: " + endPoint + " bundle: " + peekData);
                    }
                    cancelActiveSyncH(endPoint, peekData, "MESSAGE_CANCEL");
                } else {
                    switch (i) {
                        case 8:
                            ActiveSyncContext activeSyncContext2 = (ActiveSyncContext) message.obj;
                            if (isLoggable) {
                                Log.d("SyncManager", "handleSyncHandlerMessage: MESSAGE_MONITOR_SYNC: " + activeSyncContext2.mSyncOperation.target);
                            }
                            if (isSyncNotUsingNetworkH(activeSyncContext2)) {
                                Log.w("SyncManager", String.format("Detected sync making no progress for %s. cancelling.", SyncLogger.logSafe(activeSyncContext2)));
                                SyncJobService.callJobFinished(activeSyncContext2.mSyncOperation.jobId, false, "no network activity");
                                runSyncFinishedOrCanceledH(null, activeSyncContext2);
                                break;
                            } else {
                                SyncManager.this.postMonitorSyncProgressMessage(activeSyncContext2);
                                break;
                            }
                        case 9:
                            if (Log.isLoggable("SyncManager", 2)) {
                                Slog.v("SyncManager", "handleSyncHandlerMessage: MESSAGE_ACCOUNTS_UPDATED");
                            }
                            updateRunningAccountsH((SyncStorageEngine.EndPoint) message.obj);
                            break;
                        case 10:
                            startSyncH((SyncOperation) message.obj);
                            break;
                        case 11:
                            SyncOperation syncOperation = (SyncOperation) message.obj;
                            if (isLoggable) {
                                Slog.v("SyncManager", "Stop sync received.");
                            }
                            ActiveSyncContext findActiveSyncContextH = findActiveSyncContextH(syncOperation.jobId);
                            if (findActiveSyncContextH != null) {
                                runSyncFinishedOrCanceledH(null, findActiveSyncContextH);
                                boolean z2 = message.arg1 != 0;
                                if (message.arg2 == 0) {
                                    z = false;
                                }
                                if (isLoggable) {
                                    Slog.v("SyncManager", "Stopping sync. Reschedule: " + z2 + "Backoff: " + z);
                                }
                                if (z) {
                                    SyncManager.this.increaseBackoffSetting(syncOperation.target);
                                }
                                if (z2) {
                                    deferStoppedSyncH(syncOperation, 0L);
                                    break;
                                }
                            }
                            break;
                        case 12:
                            ScheduleSyncMessagePayload scheduleSyncMessagePayload = (ScheduleSyncMessagePayload) message.obj;
                            SyncManager.this.scheduleSyncOperationH(scheduleSyncMessagePayload.syncOperation, scheduleSyncMessagePayload.minDelayMillis);
                            break;
                        case 13:
                            UpdatePeriodicSyncMessagePayload updatePeriodicSyncMessagePayload = (UpdatePeriodicSyncMessagePayload) message.obj;
                            updateOrAddPeriodicSyncH(updatePeriodicSyncMessagePayload.target, updatePeriodicSyncMessagePayload.pollFrequency, updatePeriodicSyncMessagePayload.flex, updatePeriodicSyncMessagePayload.extras);
                            break;
                        case 14:
                            Pair pair = (Pair) message.obj;
                            removePeriodicSyncH((SyncStorageEngine.EndPoint) pair.first, message.getData(), (String) pair.second);
                            break;
                    }
                }
            } finally {
                this.mSyncTimeTracker.update();
            }
        }

        public final PowerManager.WakeLock getSyncWakeLock(SyncOperation syncOperation) {
            String wakeLockName = syncOperation.wakeLockName();
            PowerManager.WakeLock wakeLock = this.mWakeLocks.get(wakeLockName);
            if (wakeLock == null) {
                PowerManager.WakeLock newWakeLock = SyncManager.this.mPowerManager.newWakeLock(1, "*sync*/" + wakeLockName);
                newWakeLock.setReferenceCounted(false);
                this.mWakeLocks.put(wakeLockName, newWakeLock);
                return newWakeLock;
            }
            return wakeLock;
        }

        public final void deferSyncH(SyncOperation syncOperation, long j, String str) {
            SyncLogger syncLogger = SyncManager.this.mLogger;
            Object[] objArr = new Object[8];
            objArr[0] = "deferSyncH() ";
            objArr[1] = syncOperation.isPeriodic ? "periodic " : "";
            objArr[2] = "sync.  op=";
            objArr[3] = syncOperation;
            objArr[4] = " delay=";
            objArr[5] = Long.valueOf(j);
            objArr[6] = " why=";
            objArr[7] = str;
            syncLogger.log(objArr);
            SyncJobService.callJobFinished(syncOperation.jobId, false, str);
            if (syncOperation.isPeriodic) {
                SyncManager.this.scheduleSyncOperationH(syncOperation.createOneTimeSyncOperation(), j);
                return;
            }
            SyncManager.this.cancelJob(syncOperation, "deferSyncH()");
            SyncManager.this.scheduleSyncOperationH(syncOperation, j);
        }

        public final void deferStoppedSyncH(SyncOperation syncOperation, long j) {
            if (syncOperation.isPeriodic) {
                SyncManager.this.scheduleSyncOperationH(syncOperation.createOneTimeSyncOperation(), j);
            } else {
                SyncManager.this.scheduleSyncOperationH(syncOperation, j);
            }
        }

        public final void deferActiveSyncH(ActiveSyncContext activeSyncContext, String str) {
            SyncOperation syncOperation = activeSyncContext.mSyncOperation;
            runSyncFinishedOrCanceledH(null, activeSyncContext);
            deferSyncH(syncOperation, 10000L, str);
        }

        public final void startSyncH(SyncOperation syncOperation) {
            boolean isLoggable = Log.isLoggable("SyncManager", 2);
            if (isLoggable) {
                Slog.v("SyncManager", syncOperation.toString());
            }
            SyncManager.this.mSyncStorageEngine.setClockValid();
            SyncJobService.markSyncStarted(syncOperation.jobId);
            if (syncOperation.isPeriodic) {
                for (SyncOperation syncOperation2 : SyncManager.this.getAllPendingSyncs()) {
                    int i = syncOperation2.sourcePeriodicId;
                    int i2 = syncOperation.jobId;
                    if (i == i2) {
                        SyncJobService.callJobFinished(i2, false, "periodic sync, pending");
                        return;
                    }
                }
                Iterator<ActiveSyncContext> it = SyncManager.this.mActiveSyncContexts.iterator();
                while (it.hasNext()) {
                    int i3 = it.next().mSyncOperation.sourcePeriodicId;
                    int i4 = syncOperation.jobId;
                    if (i3 == i4) {
                        SyncJobService.callJobFinished(i4, false, "periodic sync, already running");
                        return;
                    }
                }
                if (SyncManager.this.isAdapterDelayed(syncOperation.target)) {
                    deferSyncH(syncOperation, 0L, "backing off");
                    return;
                }
            }
            Iterator<ActiveSyncContext> it2 = SyncManager.this.mActiveSyncContexts.iterator();
            while (true) {
                if (!it2.hasNext()) {
                    break;
                }
                ActiveSyncContext next = it2.next();
                if (next.mSyncOperation.isConflict(syncOperation)) {
                    if (next.mSyncOperation.getJobBias() >= syncOperation.getJobBias()) {
                        if (isLoggable) {
                            Slog.v("SyncManager", "Rescheduling sync due to conflict " + syncOperation.toString());
                        }
                        deferSyncH(syncOperation, 10000L, "delay on conflict");
                        return;
                    }
                    if (isLoggable) {
                        Slog.v("SyncManager", "Pushing back running sync due to a higher priority sync");
                    }
                    deferActiveSyncH(next, "preempted");
                }
            }
            int computeSyncOpState = computeSyncOpState(syncOperation);
            if (computeSyncOpState != 0) {
                int i5 = syncOperation.jobId;
                SyncJobService.callJobFinished(i5, false, "invalid op state: " + computeSyncOpState);
                return;
            }
            if (!dispatchSyncOperation(syncOperation)) {
                SyncJobService.callJobFinished(syncOperation.jobId, false, "dispatchSyncOperation() failed");
            }
            SyncManager.this.setAuthorityPendingState(syncOperation.target);
        }

        public final ActiveSyncContext findActiveSyncContextH(int i) {
            Iterator<ActiveSyncContext> it = SyncManager.this.mActiveSyncContexts.iterator();
            while (it.hasNext()) {
                ActiveSyncContext next = it.next();
                SyncOperation syncOperation = next.mSyncOperation;
                if (syncOperation != null && syncOperation.jobId == i) {
                    return next;
                }
            }
            return null;
        }

        public final void updateRunningAccountsH(SyncStorageEngine.EndPoint endPoint) {
            int i;
            synchronized (SyncManager.this.mAccountsLock) {
                AccountAndUser[] accountAndUserArr = SyncManager.this.mRunningAccounts;
                SyncManager.this.mRunningAccounts = AccountManagerService.getSingleton().getRunningAccountsForSystem();
                if (Log.isLoggable("SyncManager", 2)) {
                    Slog.v("SyncManager", "Accounts list: ");
                    for (AccountAndUser accountAndUser : SyncManager.this.mRunningAccounts) {
                        Slog.v("SyncManager", accountAndUser.toString());
                    }
                }
                if (SyncManager.this.mLogger.enabled()) {
                    SyncManager.this.mLogger.log("updateRunningAccountsH: ", Arrays.toString(SyncManager.this.mRunningAccounts));
                }
                SyncManager.this.removeStaleAccounts();
                AccountAndUser[] accountAndUserArr2 = SyncManager.this.mRunningAccounts;
                int size = SyncManager.this.mActiveSyncContexts.size();
                for (int i2 = 0; i2 < size; i2++) {
                    ActiveSyncContext activeSyncContext = SyncManager.this.mActiveSyncContexts.get(i2);
                    SyncManager syncManager = SyncManager.this;
                    SyncStorageEngine.EndPoint endPoint2 = activeSyncContext.mSyncOperation.target;
                    if (!syncManager.containsAccountAndUser(accountAndUserArr2, endPoint2.account, endPoint2.userId)) {
                        Log.d("SyncManager", "canceling sync since the account is no longer running");
                        SyncManager.this.sendSyncFinishedOrCanceledMessage(activeSyncContext, null);
                    }
                }
                if (endPoint != null) {
                    int length = SyncManager.this.mRunningAccounts.length;
                    int i3 = 0;
                    while (true) {
                        if (i3 >= length) {
                            break;
                        }
                        AccountAndUser accountAndUser2 = SyncManager.this.mRunningAccounts[i3];
                        if (SyncManager.this.containsAccountAndUser(accountAndUserArr, accountAndUser2.account, accountAndUser2.userId)) {
                            i3++;
                        } else {
                            if (Log.isLoggable("SyncManager", 3)) {
                                Log.d("SyncManager", "Account " + accountAndUser2.account + " added, checking sync restore data");
                            }
                            AccountSyncSettingsBackupHelper.accountAdded(SyncManager.this.mContext, endPoint.userId);
                        }
                    }
                }
            }
            AccountAndUser[] allAccountsForSystemProcess = AccountManagerService.getSingleton().getAllAccountsForSystemProcess();
            List allPendingSyncs = SyncManager.this.getAllPendingSyncs();
            int size2 = allPendingSyncs.size();
            for (i = 0; i < size2; i++) {
                SyncOperation syncOperation = (SyncOperation) allPendingSyncs.get(i);
                SyncManager syncManager2 = SyncManager.this;
                SyncStorageEngine.EndPoint endPoint3 = syncOperation.target;
                if (!syncManager2.containsAccountAndUser(allAccountsForSystemProcess, endPoint3.account, endPoint3.userId)) {
                    SyncManager.this.mLogger.log("canceling: ", syncOperation);
                    SyncManager.this.cancelJob(syncOperation, "updateRunningAccountsH()");
                }
            }
            if (endPoint != null) {
                SyncManager.this.scheduleSync(endPoint.account, endPoint.userId, -2, endPoint.provider, null, -1, 0, Process.myUid(), -4, null);
            }
        }

        public final void maybeUpdateSyncPeriodH(SyncOperation syncOperation, long j, long j2) {
            if (j == syncOperation.periodMillis && j2 == syncOperation.flexMillis) {
                return;
            }
            if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "updating period " + syncOperation + " to " + j + " and flex to " + j2);
            }
            SyncOperation syncOperation2 = new SyncOperation(syncOperation, j, j2);
            syncOperation2.jobId = syncOperation.jobId;
            SyncManager.this.scheduleSyncOperationH(syncOperation2);
        }

        public final void updateOrAddPeriodicSyncH(final SyncStorageEngine.EndPoint endPoint, final long j, final long j2, final Bundle bundle) {
            boolean isLoggable = Log.isLoggable("SyncManager", 2);
            SyncManager.this.verifyJobScheduler();
            long j3 = j * 1000;
            long j4 = j2 * 1000;
            if (isLoggable) {
                Slog.v("SyncManager", "Addition to periodic syncs requested: " + endPoint + " period: " + j + " flexMillis: " + j2 + " extras: " + bundle.toString());
            }
            for (SyncOperation syncOperation : SyncManager.this.getAllPendingSyncs()) {
                if (syncOperation.isPeriodic && syncOperation.target.matchesSpec(endPoint)) {
                    if (syncOperation.areExtrasEqual(bundle, true)) {
                        maybeUpdateSyncPeriodH(syncOperation, j3, j4);
                        return;
                    }
                }
            }
            if (isLoggable) {
                Slog.v("SyncManager", "Adding new periodic sync: " + endPoint + " period: " + j + " flexMillis: " + j2 + " extras: " + bundle.toString());
            }
            RegisteredServicesCache.ServiceInfo serviceInfo = SyncManager.this.mSyncAdapters.getServiceInfo(SyncAdapterType.newKey(endPoint.provider, endPoint.account.type), endPoint.userId);
            if (serviceInfo == null) {
                return;
            }
            SyncOperation syncOperation2 = new SyncOperation(endPoint, serviceInfo.uid, serviceInfo.componentName.getPackageName(), -4, 4, bundle, ((SyncAdapterType) serviceInfo.type).allowParallelSyncs(), true, -1, j3, j4, 0);
            int computeSyncOpState = computeSyncOpState(syncOperation2);
            if (computeSyncOpState != 2) {
                if (computeSyncOpState != 0) {
                    SyncManager.this.mLogger.log("syncOpState=", Integer.valueOf(computeSyncOpState));
                    return;
                }
                SyncManager.this.scheduleSyncOperationH(syncOperation2);
                SyncManager.this.mSyncStorageEngine.reportChange(1, syncOperation2.owningPackage, endPoint.userId);
                return;
            }
            String str = syncOperation2.owningPackage;
            int userId = UserHandle.getUserId(syncOperation2.owningUid);
            if (SyncManager.this.wasPackageEverLaunched(str, userId)) {
                SyncManager.this.mLogger.log("requestAccountAccess for SYNC_OP_STATE_INVALID_NO_ACCOUNT_ACCESS");
                SyncManager.this.mAccountManagerInternal.requestAccountAccess(syncOperation2.target.account, str, userId, new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: com.android.server.content.SyncManager$SyncHandler$$ExternalSyntheticLambda0
                    public final void onResult(Bundle bundle2) {
                        SyncManager.SyncHandler.this.lambda$updateOrAddPeriodicSyncH$0(endPoint, j, j2, bundle, bundle2);
                    }
                }));
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$updateOrAddPeriodicSyncH$0(SyncStorageEngine.EndPoint endPoint, long j, long j2, Bundle bundle, Bundle bundle2) {
            if (bundle2 == null || !bundle2.getBoolean("booleanResult")) {
                return;
            }
            SyncManager.this.updateOrAddPeriodicSync(endPoint, j, j2, bundle);
        }

        public final void removePeriodicSyncInternalH(SyncOperation syncOperation, String str) {
            for (SyncOperation syncOperation2 : SyncManager.this.getAllPendingSyncs()) {
                int i = syncOperation2.sourcePeriodicId;
                int i2 = syncOperation.jobId;
                if (i == i2 || syncOperation2.jobId == i2) {
                    ActiveSyncContext findActiveSyncContextH = findActiveSyncContextH(i2);
                    if (findActiveSyncContextH != null) {
                        SyncJobService.callJobFinished(syncOperation.jobId, false, "removePeriodicSyncInternalH");
                        runSyncFinishedOrCanceledH(null, findActiveSyncContextH);
                    }
                    SyncManager.this.mLogger.log("removePeriodicSyncInternalH-canceling: ", syncOperation2);
                    SyncManager.this.cancelJob(syncOperation2, str);
                }
            }
        }

        public final void removePeriodicSyncH(SyncStorageEngine.EndPoint endPoint, Bundle bundle, String str) {
            SyncManager.this.verifyJobScheduler();
            for (SyncOperation syncOperation : SyncManager.this.getAllPendingSyncs()) {
                if (syncOperation.isPeriodic && syncOperation.target.matchesSpec(endPoint) && syncOperation.areExtrasEqual(bundle, true)) {
                    removePeriodicSyncInternalH(syncOperation, str);
                }
            }
        }

        public final boolean isSyncNotUsingNetworkH(ActiveSyncContext activeSyncContext) {
            long totalBytesTransferredByUid = SyncManager.this.getTotalBytesTransferredByUid(activeSyncContext.mSyncAdapterUid) - activeSyncContext.mBytesTransferredAtLastPoll;
            if (Log.isLoggable("SyncManager", 3)) {
                long j = totalBytesTransferredByUid % 1048576;
                Log.d("SyncManager", String.format("Time since last update: %ds. Delta transferred: %dMBs,%dKBs,%dBs", Long.valueOf((SystemClock.elapsedRealtime() - activeSyncContext.mLastPolledTimeElapsed) / 1000), Long.valueOf(totalBytesTransferredByUid / 1048576), Long.valueOf(j / 1024), Long.valueOf(j % 1024)));
            }
            return totalBytesTransferredByUid <= 10;
        }

        public final int computeSyncOpState(SyncOperation syncOperation) {
            boolean isLoggable = Log.isLoggable("SyncManager", 2);
            SyncStorageEngine.EndPoint endPoint = syncOperation.target;
            synchronized (SyncManager.this.mAccountsLock) {
                if (!SyncManager.this.containsAccountAndUser(SyncManager.this.mRunningAccounts, endPoint.account, endPoint.userId)) {
                    if (isLoggable) {
                        Slog.v("SyncManager", "    Dropping sync operation: account doesn't exist.");
                    }
                    logAccountError("SYNC_OP_STATE_INVALID: account doesn't exist.");
                    return 3;
                }
                boolean z = true;
                int computeSyncable = SyncManager.this.computeSyncable(endPoint.account, endPoint.userId, endPoint.provider, true);
                if (computeSyncable == 3) {
                    if (isLoggable) {
                        Slog.v("SyncManager", "    Dropping sync operation: isSyncable == SYNCABLE_NO_ACCOUNT_ACCESS");
                    }
                    logAccountError("SYNC_OP_STATE_INVALID_NO_ACCOUNT_ACCESS");
                    return 2;
                } else if (computeSyncable == 0) {
                    if (isLoggable) {
                        Slog.v("SyncManager", "    Dropping sync operation: isSyncable == NOT_SYNCABLE");
                    }
                    logAccountError("SYNC_OP_STATE_INVALID: NOT_SYNCABLE");
                    return 4;
                } else {
                    boolean z2 = SyncManager.this.mSyncStorageEngine.getMasterSyncAutomatically(endPoint.userId) && SyncManager.this.mSyncStorageEngine.getSyncAutomatically(endPoint.account, endPoint.userId, endPoint.provider);
                    if (!syncOperation.isIgnoreSettings() && computeSyncable >= 0) {
                        z = false;
                    }
                    if (z2 || z) {
                        return 0;
                    }
                    if (isLoggable) {
                        Slog.v("SyncManager", "    Dropping sync operation: disallowed by settings/network.");
                    }
                    logAccountError("SYNC_OP_STATE_INVALID: disallowed by settings/network");
                    return 5;
                }
            }
        }

        public final void logAccountError(String str) {
            Slog.wtf("SyncManager", str);
        }

        public final boolean dispatchSyncOperation(SyncOperation syncOperation) {
            UsageStatsManagerInternal usageStatsManagerInternal;
            if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "dispatchSyncOperation: we are going to sync " + syncOperation);
                Slog.v("SyncManager", "num active syncs: " + SyncManager.this.mActiveSyncContexts.size());
                Iterator<ActiveSyncContext> it = SyncManager.this.mActiveSyncContexts.iterator();
                while (it.hasNext()) {
                    Slog.v("SyncManager", it.next().toString());
                }
            }
            if (syncOperation.isAppStandbyExempted() && (usageStatsManagerInternal = (UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class)) != null) {
                usageStatsManagerInternal.reportExemptedSyncStart(syncOperation.owningPackage, UserHandle.getUserId(syncOperation.owningUid));
            }
            SyncStorageEngine.EndPoint endPoint = syncOperation.target;
            SyncAdapterType newKey = SyncAdapterType.newKey(endPoint.provider, endPoint.account.type);
            RegisteredServicesCache.ServiceInfo serviceInfo = SyncManager.this.mSyncAdapters.getServiceInfo(newKey, endPoint.userId);
            if (serviceInfo == null) {
                SyncManager.this.mLogger.log("dispatchSyncOperation() failed: no sync adapter info for ", newKey);
                Log.d("SyncManager", "can't find a sync adapter for " + newKey + ", removing settings for it");
                SyncManager.this.mSyncStorageEngine.removeAuthority(endPoint);
                return false;
            }
            int i = serviceInfo.uid;
            ComponentName componentName = serviceInfo.componentName;
            ActiveSyncContext activeSyncContext = new ActiveSyncContext(syncOperation, insertStartSyncEvent(syncOperation), i);
            if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "dispatchSyncOperation: starting " + activeSyncContext);
            }
            activeSyncContext.mSyncInfo = SyncManager.this.mSyncStorageEngine.addActiveSync(activeSyncContext);
            SyncManager.this.mActiveSyncContexts.add(activeSyncContext);
            SyncManager.this.postMonitorSyncProgressMessage(activeSyncContext);
            if (activeSyncContext.bindToSyncAdapter(componentName, endPoint.userId)) {
                return true;
            }
            SyncManager.this.mLogger.log("dispatchSyncOperation() failed: bind failed. target: ", componentName);
            Slog.e("SyncManager", "Bind attempt failed - target: " + componentName);
            closeActiveSyncContext(activeSyncContext);
            return false;
        }

        public final void runBoundToAdapterH(ActiveSyncContext activeSyncContext, IBinder iBinder) {
            SyncOperation syncOperation = activeSyncContext.mSyncOperation;
            try {
                activeSyncContext.mIsLinkedToDeath = true;
                iBinder.linkToDeath(activeSyncContext, 0);
                if (SyncManager.this.mLogger.enabled()) {
                    SyncLogger syncLogger = SyncManager.this.mLogger;
                    syncLogger.log("Sync start: account=" + syncOperation.target.account, " authority=", syncOperation.target.provider, " reason=", SyncOperation.reasonToString(null, syncOperation.reason), " extras=", syncOperation.getExtrasAsString(), " adapter=", activeSyncContext.mSyncAdapter);
                }
                ISyncAdapter asInterface = ISyncAdapter.Stub.asInterface(iBinder);
                activeSyncContext.mSyncAdapter = asInterface;
                SyncStorageEngine.EndPoint endPoint = syncOperation.target;
                asInterface.startSync(activeSyncContext, endPoint.provider, endPoint.account, syncOperation.getClonedExtras());
                SyncManager.this.mLogger.log("Sync is running now...");
            } catch (RemoteException e) {
                SyncManager.this.mLogger.log("Sync failed with RemoteException: ", e.toString());
                Log.d("SyncManager", "maybeStartNextSync: caught a RemoteException, rescheduling", e);
                closeActiveSyncContext(activeSyncContext);
                SyncManager.this.increaseBackoffSetting(syncOperation.target);
                SyncManager.this.scheduleSyncOperationH(syncOperation);
            } catch (RuntimeException e2) {
                SyncManager.this.mLogger.log("Sync failed with RuntimeException: ", e2.toString());
                closeActiveSyncContext(activeSyncContext);
                Slog.e("SyncManager", "Caught RuntimeException while starting the sync " + SyncLogger.logSafe(syncOperation), e2);
            }
        }

        public final void cancelActiveSyncH(SyncStorageEngine.EndPoint endPoint, Bundle bundle, String str) {
            Iterator it = new ArrayList(SyncManager.this.mActiveSyncContexts).iterator();
            while (it.hasNext()) {
                ActiveSyncContext activeSyncContext = (ActiveSyncContext) it.next();
                if (activeSyncContext != null && activeSyncContext.mSyncOperation.target.matchesSpec(endPoint) && (bundle == null || activeSyncContext.mSyncOperation.areExtrasEqual(bundle, false))) {
                    SyncJobService.callJobFinished(activeSyncContext.mSyncOperation.jobId, false, str);
                    runSyncFinishedOrCanceledH(null, activeSyncContext);
                }
            }
        }

        public final void reschedulePeriodicSyncH(SyncOperation syncOperation) {
            SyncOperation syncOperation2;
            Iterator it = SyncManager.this.getAllPendingSyncs().iterator();
            while (true) {
                if (!it.hasNext()) {
                    syncOperation2 = null;
                    break;
                }
                syncOperation2 = (SyncOperation) it.next();
                if (syncOperation2.isPeriodic && syncOperation.matchesPeriodicOperation(syncOperation2)) {
                    break;
                }
            }
            if (syncOperation2 == null) {
                return;
            }
            SyncManager.this.scheduleSyncOperationH(syncOperation2);
        }

        public final void runSyncFinishedOrCanceledH(SyncResult syncResult, ActiveSyncContext activeSyncContext) {
            String str;
            boolean isLoggable = Log.isLoggable("SyncManager", 2);
            SyncOperation syncOperation = activeSyncContext.mSyncOperation;
            SyncStorageEngine.EndPoint endPoint = syncOperation.target;
            if (activeSyncContext.mIsLinkedToDeath) {
                activeSyncContext.mSyncAdapter.asBinder().unlinkToDeath(activeSyncContext, 0);
                activeSyncContext.mIsLinkedToDeath = false;
            }
            long elapsedRealtime = SystemClock.elapsedRealtime() - activeSyncContext.mStartTime;
            SyncManager.this.mLogger.log("runSyncFinishedOrCanceledH() op=", syncOperation, " result=", syncResult);
            if (syncResult != null) {
                if (isLoggable) {
                    Slog.v("SyncManager", "runSyncFinishedOrCanceled [finished]: " + syncOperation + ", result " + syncResult);
                }
                closeActiveSyncContext(activeSyncContext);
                if (!syncOperation.isPeriodic) {
                    SyncManager.this.cancelJob(syncOperation, "runSyncFinishedOrCanceledH()-finished");
                }
                if (!syncResult.hasError()) {
                    SyncManager.this.clearBackoffSetting(syncOperation.target, "sync success");
                    if (syncOperation.isDerivedFromFailedPeriodicSync()) {
                        reschedulePeriodicSyncH(syncOperation);
                    }
                    str = "success";
                } else {
                    Log.w("SyncManager", "failed sync operation " + SyncLogger.logSafe(syncOperation) + ", " + syncResult);
                    int i = syncOperation.retries + 1;
                    syncOperation.retries = i;
                    if (i > SyncManager.this.mConstants.getMaxRetriesWithAppStandbyExemption()) {
                        syncOperation.syncExemptionFlag = 0;
                    }
                    SyncManager.this.increaseBackoffSetting(syncOperation.target);
                    if (!syncOperation.isPeriodic) {
                        SyncManager.this.maybeRescheduleSync(syncResult, syncOperation);
                    } else {
                        SyncManager.this.postScheduleSyncMessage(syncOperation.createOneTimeSyncOperation(), 0L);
                    }
                    str = ContentResolver.syncErrorToString(syncResultToErrorNumber(syncResult));
                }
                SyncManager.this.setDelayUntilTime(syncOperation.target, syncResult.delayUntil);
            } else {
                if (isLoggable) {
                    Slog.v("SyncManager", "runSyncFinishedOrCanceled [canceled]: " + syncOperation);
                }
                if (!syncOperation.isPeriodic) {
                    SyncManager.this.cancelJob(syncOperation, "runSyncFinishedOrCanceledH()-canceled");
                }
                if (activeSyncContext.mSyncAdapter != null) {
                    try {
                        SyncManager.this.mLogger.log("Calling cancelSync for runSyncFinishedOrCanceled ", activeSyncContext, "  adapter=", activeSyncContext.mSyncAdapter);
                        activeSyncContext.mSyncAdapter.cancelSync(activeSyncContext);
                        SyncManager.this.mLogger.log("Canceled");
                    } catch (RemoteException e) {
                        SyncManager.this.mLogger.log("RemoteException ", Log.getStackTraceString(e));
                    }
                }
                closeActiveSyncContext(activeSyncContext);
                str = "canceled";
            }
            stopSyncEvent(activeSyncContext.mHistoryRowId, syncOperation, str, 0, 0, elapsedRealtime);
            if (syncResult != null && syncResult.tooManyDeletions) {
                installHandleTooManyDeletesNotification(endPoint.account, endPoint.provider, syncResult.stats.numDeletes, endPoint.userId);
            } else {
                SyncManager.this.mNotificationMgr.cancelAsUser(Integer.toString(endPoint.account.hashCode() ^ endPoint.provider.hashCode()), 18, new UserHandle(endPoint.userId));
            }
            if (syncResult == null || !syncResult.fullSyncRequested) {
                return;
            }
            SyncManager.this.scheduleSyncOperationH(new SyncOperation(endPoint.account, endPoint.userId, syncOperation.owningUid, syncOperation.owningPackage, syncOperation.reason, syncOperation.syncSource, endPoint.provider, new Bundle(), syncOperation.allowParallelSyncs, syncOperation.syncExemptionFlag));
        }

        public final void closeActiveSyncContext(ActiveSyncContext activeSyncContext) {
            activeSyncContext.close();
            SyncManager.this.mActiveSyncContexts.remove(activeSyncContext);
            SyncManager.this.mSyncStorageEngine.removeActiveSync(activeSyncContext.mSyncInfo, activeSyncContext.mSyncOperation.target.userId);
            if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "removing all MESSAGE_MONITOR_SYNC & MESSAGE_SYNC_EXPIRED for " + activeSyncContext.toString());
            }
            SyncManager.this.mSyncHandler.removeMessages(8, activeSyncContext);
            SyncManager.this.mLogger.log("closeActiveSyncContext: ", activeSyncContext);
        }

        public final int syncResultToErrorNumber(SyncResult syncResult) {
            if (syncResult.syncAlreadyInProgress) {
                return 1;
            }
            SyncStats syncStats = syncResult.stats;
            if (syncStats.numAuthExceptions > 0) {
                return 2;
            }
            if (syncStats.numIoExceptions > 0) {
                return 3;
            }
            if (syncStats.numParseExceptions > 0) {
                return 4;
            }
            if (syncStats.numConflictDetectedExceptions > 0) {
                return 5;
            }
            if (syncResult.tooManyDeletions) {
                return 6;
            }
            if (syncResult.tooManyRetries) {
                return 7;
            }
            if (syncResult.databaseError) {
                return 8;
            }
            throw new IllegalStateException("we are not in an error state, " + syncResult);
        }

        public final void installHandleTooManyDeletesNotification(Account account, String str, long j, int i) {
            ProviderInfo resolveContentProvider;
            if (SyncManager.this.mNotificationMgr == null || (resolveContentProvider = SyncManager.this.mContext.getPackageManager().resolveContentProvider(str, 0)) == null) {
                return;
            }
            CharSequence loadLabel = resolveContentProvider.loadLabel(SyncManager.this.mContext.getPackageManager());
            Intent intent = new Intent(SyncManager.this.mContext, SyncActivityTooManyDeletes.class);
            intent.putExtra("account", account);
            intent.putExtra("authority", str);
            intent.putExtra("provider", loadLabel.toString());
            intent.putExtra("numDeletes", j);
            if (!isActivityAvailable(intent)) {
                Log.w("SyncManager", "No activity found to handle too many deletes.");
                return;
            }
            UserHandle userHandle = new UserHandle(i);
            PendingIntent activityAsUser = PendingIntent.getActivityAsUser(SyncManager.this.mContext, 0, intent, 335544320, null, userHandle);
            CharSequence text = SyncManager.this.mContext.getResources().getText(17040050);
            Context contextForUser = SyncManager.this.getContextForUser(userHandle);
            Notification build = new Notification.Builder(contextForUser, SystemNotificationChannels.ACCOUNT).setSmallIcon(17303594).setTicker(SyncManager.this.mContext.getString(17040048)).setWhen(System.currentTimeMillis()).setColor(contextForUser.getColor(17170460)).setContentTitle(contextForUser.getString(17040049)).setContentText(String.format(text.toString(), loadLabel)).setContentIntent(activityAsUser).build();
            build.flags |= 2;
            SyncManager.this.mNotificationMgr.notifyAsUser(Integer.toString(account.hashCode() ^ str.hashCode()), 18, build, userHandle);
        }

        public final boolean isActivityAvailable(Intent intent) {
            List<ResolveInfo> queryIntentActivities = SyncManager.this.mContext.getPackageManager().queryIntentActivities(intent, 0);
            int size = queryIntentActivities.size();
            for (int i = 0; i < size; i++) {
                if ((queryIntentActivities.get(i).activityInfo.applicationInfo.flags & 1) != 0) {
                    return true;
                }
            }
            return false;
        }

        public long insertStartSyncEvent(SyncOperation syncOperation) {
            long currentTimeMillis = System.currentTimeMillis();
            EventLog.writeEvent(2720, syncOperation.toEventLog(0));
            return SyncManager.this.mSyncStorageEngine.insertStartSyncEvent(syncOperation, currentTimeMillis);
        }

        public void stopSyncEvent(long j, SyncOperation syncOperation, String str, int i, int i2, long j2) {
            EventLog.writeEvent(2720, syncOperation.toEventLog(1));
            SyncManager.this.mSyncStorageEngine.stopSyncEvent(j, j2, str, i2, i, syncOperation.owningPackage, syncOperation.target.userId);
        }
    }

    public final boolean isSyncStillActiveH(ActiveSyncContext activeSyncContext) {
        Iterator<ActiveSyncContext> it = this.mActiveSyncContexts.iterator();
        while (it.hasNext()) {
            if (it.next() == activeSyncContext) {
                return true;
            }
        }
        return false;
    }

    public static boolean syncExtrasEquals(Bundle bundle, Bundle bundle2, boolean z) {
        if (bundle == bundle2) {
            return true;
        }
        if (!z || bundle.size() == bundle2.size()) {
            Bundle bundle3 = bundle.size() > bundle2.size() ? bundle : bundle2;
            if (bundle.size() > bundle2.size()) {
                bundle = bundle2;
            }
            for (String str : bundle3.keySet()) {
                if (z || !isSyncSetting(str)) {
                    if (!bundle.containsKey(str) || !Objects.equals(bundle3.get(str), bundle.get(str))) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public static boolean isSyncSetting(String str) {
        if (str == null) {
            return false;
        }
        return str.equals("expedited") || str.equals("schedule_as_expedited_job") || str.equals("ignore_settings") || str.equals("ignore_backoff") || str.equals("do_not_retry") || str.equals("force") || str.equals("upload") || str.equals("deletions_override") || str.equals("discard_deletions") || str.equals("expected_upload") || str.equals("expected_download") || str.equals("sync_priority") || str.equals("allow_metered") || str.equals("initialize");
    }

    /* loaded from: classes.dex */
    public static class PrintTable {
        public final int mCols;
        public ArrayList<String[]> mTable = Lists.newArrayList();

        public PrintTable(int i) {
            this.mCols = i;
        }

        public void set(int i, int i2, Object... objArr) {
            int i3;
            if (objArr.length + i2 > this.mCols) {
                throw new IndexOutOfBoundsException("Table only has " + this.mCols + " columns. can't set " + objArr.length + " at column " + i2);
            }
            int size = this.mTable.size();
            while (true) {
                i3 = 0;
                if (size > i) {
                    break;
                }
                String[] strArr = new String[this.mCols];
                this.mTable.add(strArr);
                while (i3 < this.mCols) {
                    strArr[i3] = "";
                    i3++;
                }
                size++;
            }
            String[] strArr2 = this.mTable.get(i);
            while (i3 < objArr.length) {
                Object obj = objArr[i3];
                strArr2[i2 + i3] = obj == null ? "" : obj.toString();
                i3++;
            }
        }

        public void writeTo(PrintWriter printWriter) {
            int i;
            String[] strArr = new String[this.mCols];
            int i2 = 0;
            int i3 = 0;
            while (true) {
                i = this.mCols;
                if (i2 >= i) {
                    break;
                }
                Iterator<String[]> it = this.mTable.iterator();
                int i4 = 0;
                while (it.hasNext()) {
                    int length = it.next()[i2].toString().length();
                    if (length > i4) {
                        i4 = length;
                    }
                }
                i3 += i4;
                strArr[i2] = String.format("%%-%ds", Integer.valueOf(i4));
                i2++;
            }
            strArr[i - 1] = "%s";
            printRow(printWriter, strArr, this.mTable.get(0));
            int i5 = i3 + ((this.mCols - 1) * 2);
            for (int i6 = 0; i6 < i5; i6++) {
                printWriter.print(PackageManagerShellCommandDataLoader.STDIN_PATH);
            }
            printWriter.println();
            int size = this.mTable.size();
            for (int i7 = 1; i7 < size; i7++) {
                printRow(printWriter, strArr, this.mTable.get(i7));
            }
        }

        public final void printRow(PrintWriter printWriter, String[] strArr, Object[] objArr) {
            int length = objArr.length;
            for (int i = 0; i < length; i++) {
                printWriter.printf(String.format(strArr[i], objArr[i].toString()), new Object[0]);
                printWriter.print("  ");
            }
            printWriter.println();
        }

        public int getNumRows() {
            return this.mTable.size();
        }
    }

    public final Context getContextForUser(UserHandle userHandle) {
        try {
            Context context = this.mContext;
            return context.createPackageContextAsUser(context.getPackageName(), 0, userHandle);
        } catch (PackageManager.NameNotFoundException unused) {
            return this.mContext;
        }
    }

    public final void cancelJob(SyncOperation syncOperation, String str) {
        if (syncOperation == null) {
            Slog.wtf("SyncManager", "Null sync operation detected.");
            return;
        }
        if (syncOperation.isPeriodic) {
            this.mLogger.log("Removing periodic sync ", syncOperation, " for ", str);
        }
        getJobScheduler().cancel(syncOperation.jobId);
    }

    public void resetTodayStats() {
        this.mSyncStorageEngine.resetTodayStats(true);
    }

    public final boolean wasPackageEverLaunched(String str, int i) {
        try {
            return this.mPackageManagerInternal.wasPackageEverLaunched(str, i);
        } catch (IllegalArgumentException unused) {
            return false;
        }
    }
}
