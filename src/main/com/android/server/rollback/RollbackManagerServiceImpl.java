package com.android.server.rollback;

import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.VersionedPackage;
import android.content.pm.parsing.ApkLite;
import android.content.pm.parsing.ApkLiteParseUtils;
import android.content.pm.parsing.result.ParseResult;
import android.content.pm.parsing.result.ParseTypeImpl;
import android.content.rollback.IRollbackManager;
import android.content.rollback.RollbackInfo;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.ext.SdkExtensions;
import android.p005os.IInstalld;
import android.provider.DeviceConfig;
import android.util.Log;
import android.util.LongArrayQueue;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.android.server.LocalServices;
import com.android.server.PackageWatchdog;
import com.android.server.SystemConfig;
import com.android.server.Watchdog;
import com.android.server.p011pm.ApexManager;
import com.android.server.p011pm.Installer;
import com.android.server.rollback.RollbackManagerServiceImpl;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
/* loaded from: classes2.dex */
public class RollbackManagerServiceImpl extends IRollbackManager.Stub implements RollbackManagerInternal {
    public final AppDataRollbackHelper mAppDataRollbackHelper;
    public final Context mContext;
    public final Executor mExecutor;
    public final Handler mHandler;
    public final Installer mInstaller;
    public final RollbackPackageHealthObserver mPackageHealthObserver;
    public final RollbackStore mRollbackStore;
    public static final boolean LOCAL_LOGV = Log.isLoggable("RollbackManager", 2);
    public static final long DEFAULT_ROLLBACK_LIFETIME_DURATION_MILLIS = TimeUnit.DAYS.toMillis(14);
    public static final long HANDLER_THREAD_TIMEOUT_DURATION_MILLIS = TimeUnit.MINUTES.toMillis(10);
    public long mRollbackLifetimeDurationInMillis = DEFAULT_ROLLBACK_LIFETIME_DURATION_MILLIS;
    public final Random mRandom = new SecureRandom();
    public final SparseBooleanArray mAllocatedRollbackIds = new SparseBooleanArray();
    public final List<Rollback> mRollbacks = new ArrayList();
    public final Runnable mRunExpiration = new Runnable() { // from class: com.android.server.rollback.RollbackManagerServiceImpl$$ExternalSyntheticLambda10
        @Override // java.lang.Runnable
        public final void run() {
            RollbackManagerServiceImpl.this.runExpiration();
        }
    };
    public final LongArrayQueue mSleepDuration = new LongArrayQueue();
    public long mRelativeBootTime = calculateRelativeBootTime();

    public RollbackManagerServiceImpl(final Context context) {
        this.mContext = context;
        Installer installer = new Installer(context);
        this.mInstaller = installer;
        installer.onStart();
        this.mRollbackStore = new RollbackStore(new File(Environment.getDataDirectory(), "rollback"), new File(Environment.getDataDirectory(), "rollback-history"));
        this.mPackageHealthObserver = new RollbackPackageHealthObserver(context);
        this.mAppDataRollbackHelper = new AppDataRollbackHelper(installer);
        HandlerThread handlerThread = new HandlerThread("RollbackManagerServiceHandler");
        handlerThread.start();
        this.mHandler = new Handler(handlerThread.getLooper());
        Watchdog.getInstance().addThread(getHandler(), HANDLER_THREAD_TIMEOUT_DURATION_MILLIS);
        this.mExecutor = new HandlerExecutor(getHandler());
        getHandler().post(new Runnable() { // from class: com.android.server.rollback.RollbackManagerServiceImpl$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                RollbackManagerServiceImpl.this.lambda$new$0(context);
            }
        });
        for (UserHandle userHandle : ((UserManager) context.getSystemService(UserManager.class)).getUserHandles(true)) {
            registerUserCallbacks(userHandle);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ENABLE_ROLLBACK");
        try {
            intentFilter.addDataType("application/vnd.android.package-archive");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Slog.e("RollbackManager", "addDataType", e);
        }
        this.mContext.registerReceiver(new C15701(), intentFilter, null, getHandler());
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.CANCEL_ENABLE_ROLLBACK");
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.rollback.RollbackManagerServiceImpl.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                RollbackManagerServiceImpl.this.assertInWorkerThread();
                if ("android.intent.action.CANCEL_ENABLE_ROLLBACK".equals(intent.getAction())) {
                    int intExtra = intent.getIntExtra("android.content.pm.extra.ENABLE_ROLLBACK_SESSION_ID", -1);
                    if (RollbackManagerServiceImpl.LOCAL_LOGV) {
                        Slog.v("RollbackManager", "broadcast=ACTION_CANCEL_ENABLE_ROLLBACK id=" + intExtra);
                    }
                    Rollback rollbackForSession = RollbackManagerServiceImpl.this.getRollbackForSession(intExtra);
                    if (rollbackForSession == null || !rollbackForSession.isEnabling()) {
                        return;
                    }
                    RollbackManagerServiceImpl.this.mRollbacks.remove(rollbackForSession);
                    RollbackManagerServiceImpl.this.deleteRollback(rollbackForSession, "Rollback canceled");
                }
            }
        }, intentFilter2, null, getHandler());
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.rollback.RollbackManagerServiceImpl.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                int intExtra;
                RollbackManagerServiceImpl.this.assertInWorkerThread();
                if (!"android.intent.action.USER_ADDED".equals(intent.getAction()) || (intExtra = intent.getIntExtra("android.intent.extra.user_handle", -1)) == -1) {
                    return;
                }
                RollbackManagerServiceImpl.this.registerUserCallbacks(UserHandle.of(intExtra));
            }
        }, new IntentFilter("android.intent.action.USER_ADDED"), null, getHandler());
        registerTimeChangeReceiver();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(Context context) {
        this.mRollbacks.addAll(this.mRollbackStore.loadRollbacks());
        if (!context.getPackageManager().isDeviceUpgrading()) {
            for (Rollback rollback : this.mRollbacks) {
                this.mAllocatedRollbackIds.put(rollback.info.getRollbackId(), true);
            }
            return;
        }
        for (Rollback rollback2 : this.mRollbacks) {
            deleteRollback(rollback2, "Fingerprint changed");
        }
        this.mRollbacks.clear();
    }

    /* renamed from: com.android.server.rollback.RollbackManagerServiceImpl$1 */
    /* loaded from: classes2.dex */
    public class C15701 extends BroadcastReceiver {
        public C15701() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            RollbackManagerServiceImpl.this.assertInWorkerThread();
            if ("android.intent.action.PACKAGE_ENABLE_ROLLBACK".equals(intent.getAction())) {
                final int intExtra = intent.getIntExtra("android.content.pm.extra.ENABLE_ROLLBACK_TOKEN", -1);
                final int intExtra2 = intent.getIntExtra("android.content.pm.extra.ENABLE_ROLLBACK_SESSION_ID", -1);
                RollbackManagerServiceImpl.this.queueSleepIfNeeded();
                RollbackManagerServiceImpl.this.getHandler().post(new Runnable() { // from class: com.android.server.rollback.RollbackManagerServiceImpl$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        RollbackManagerServiceImpl.C15701.this.lambda$onReceive$0(intExtra2, intExtra);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onReceive$0(int i, int i2) {
            RollbackManagerServiceImpl.this.assertInWorkerThread();
            ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).setEnableRollbackCode(i2, !RollbackManagerServiceImpl.this.enableRollback(i) ? -1 : 1);
        }
    }

    public final <U> U awaitResult(Supplier<U> supplier) {
        assertNotInWorkerThread();
        try {
            return (U) CompletableFuture.supplyAsync(supplier, this.mExecutor).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public final void awaitResult(Runnable runnable) {
        assertNotInWorkerThread();
        try {
            CompletableFuture.runAsync(runnable, this.mExecutor).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public final void assertInWorkerThread() {
        Preconditions.checkState(getHandler().getLooper().isCurrentThread());
    }

    public final void assertNotInWorkerThread() {
        Preconditions.checkState(!getHandler().getLooper().isCurrentThread());
    }

    public final void registerUserCallbacks(UserHandle userHandle) {
        Context contextAsUser = getContextAsUser(userHandle);
        if (contextAsUser == null) {
            Slog.e("RollbackManager", "Unable to register user callbacks for user " + userHandle);
            return;
        }
        contextAsUser.getPackageManager().getPackageInstaller().registerSessionCallback(new SessionCallback(), getHandler());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addAction("android.intent.action.PACKAGE_FULLY_REMOVED");
        intentFilter.addDataScheme("package");
        contextAsUser.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.rollback.RollbackManagerServiceImpl.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                RollbackManagerServiceImpl.this.assertInWorkerThread();
                String action = intent.getAction();
                if ("android.intent.action.PACKAGE_REPLACED".equals(action)) {
                    String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
                    if (RollbackManagerServiceImpl.LOCAL_LOGV) {
                        Slog.v("RollbackManager", "broadcast=ACTION_PACKAGE_REPLACED pkg=" + schemeSpecificPart);
                    }
                    RollbackManagerServiceImpl.this.onPackageReplaced(schemeSpecificPart);
                }
                if ("android.intent.action.PACKAGE_FULLY_REMOVED".equals(action)) {
                    String schemeSpecificPart2 = intent.getData().getSchemeSpecificPart();
                    Slog.i("RollbackManager", "broadcast=ACTION_PACKAGE_FULLY_REMOVED pkg=" + schemeSpecificPart2);
                    RollbackManagerServiceImpl.this.onPackageFullyRemoved(schemeSpecificPart2);
                }
            }
        }, intentFilter, null, getHandler());
    }

    public ParceledListSlice getAvailableRollbacks() {
        assertNotInWorkerThread();
        enforceManageRollbacks("getAvailableRollbacks");
        return (ParceledListSlice) awaitResult(new Supplier() { // from class: com.android.server.rollback.RollbackManagerServiceImpl$$ExternalSyntheticLambda12
            @Override // java.util.function.Supplier
            public final Object get() {
                ParceledListSlice lambda$getAvailableRollbacks$1;
                lambda$getAvailableRollbacks$1 = RollbackManagerServiceImpl.this.lambda$getAvailableRollbacks$1();
                return lambda$getAvailableRollbacks$1;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ParceledListSlice lambda$getAvailableRollbacks$1() {
        assertInWorkerThread();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.mRollbacks.size(); i++) {
            Rollback rollback = this.mRollbacks.get(i);
            if (rollback.isAvailable()) {
                arrayList.add(rollback.info);
            }
        }
        return new ParceledListSlice(arrayList);
    }

    public ParceledListSlice<RollbackInfo> getRecentlyCommittedRollbacks() {
        assertNotInWorkerThread();
        enforceManageRollbacks("getRecentlyCommittedRollbacks");
        return (ParceledListSlice) awaitResult(new Supplier() { // from class: com.android.server.rollback.RollbackManagerServiceImpl$$ExternalSyntheticLambda8
            @Override // java.util.function.Supplier
            public final Object get() {
                ParceledListSlice lambda$getRecentlyCommittedRollbacks$2;
                lambda$getRecentlyCommittedRollbacks$2 = RollbackManagerServiceImpl.this.lambda$getRecentlyCommittedRollbacks$2();
                return lambda$getRecentlyCommittedRollbacks$2;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ParceledListSlice lambda$getRecentlyCommittedRollbacks$2() {
        assertInWorkerThread();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.mRollbacks.size(); i++) {
            Rollback rollback = this.mRollbacks.get(i);
            if (rollback.isCommitted()) {
                arrayList.add(rollback.info);
            }
        }
        return new ParceledListSlice(arrayList);
    }

    public void commitRollback(final int i, final ParceledListSlice parceledListSlice, final String str, final IntentSender intentSender) {
        assertNotInWorkerThread();
        enforceManageRollbacks("commitRollback");
        ((AppOpsManager) this.mContext.getSystemService(AppOpsManager.class)).checkPackage(Binder.getCallingUid(), str);
        getHandler().post(new Runnable() { // from class: com.android.server.rollback.RollbackManagerServiceImpl$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                RollbackManagerServiceImpl.this.lambda$commitRollback$3(i, parceledListSlice, str, intentSender);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$commitRollback$3(int i, ParceledListSlice parceledListSlice, String str, IntentSender intentSender) {
        commitRollbackInternal(i, parceledListSlice.getList(), str, intentSender);
    }

    public final void registerTimeChangeReceiver() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.rollback.RollbackManagerServiceImpl.5
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                RollbackManagerServiceImpl.this.assertInWorkerThread();
                long j = RollbackManagerServiceImpl.this.mRelativeBootTime;
                RollbackManagerServiceImpl.this.mRelativeBootTime = RollbackManagerServiceImpl.calculateRelativeBootTime();
                long j2 = RollbackManagerServiceImpl.this.mRelativeBootTime - j;
                for (Rollback rollback : RollbackManagerServiceImpl.this.mRollbacks) {
                    rollback.setTimestamp(rollback.getTimestamp().plusMillis(j2));
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIME_SET");
        this.mContext.registerReceiver(broadcastReceiver, intentFilter, null, getHandler());
    }

    public static long calculateRelativeBootTime() {
        return System.currentTimeMillis() - SystemClock.elapsedRealtime();
    }

    public final void commitRollbackInternal(int i, List<VersionedPackage> list, String str, IntentSender intentSender) {
        assertInWorkerThread();
        Slog.i("RollbackManager", "commitRollback id=" + i + " caller=" + str);
        Rollback rollbackForId = getRollbackForId(i);
        if (rollbackForId == null) {
            sendFailure(this.mContext, intentSender, 2, "Rollback unavailable");
        } else {
            rollbackForId.commit(this.mContext, list, str, intentSender);
        }
    }

    public void reloadPersistedData() {
        assertNotInWorkerThread();
        this.mContext.enforceCallingOrSelfPermission("android.permission.TEST_MANAGE_ROLLBACKS", "reloadPersistedData");
        awaitResult(new Runnable() { // from class: com.android.server.rollback.RollbackManagerServiceImpl$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                RollbackManagerServiceImpl.this.lambda$reloadPersistedData$4();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$reloadPersistedData$4() {
        assertInWorkerThread();
        this.mRollbacks.clear();
        this.mRollbacks.addAll(this.mRollbackStore.loadRollbacks());
    }

    public final void expireRollbackForPackageInternal(String str, String str2) {
        assertInWorkerThread();
        Iterator<Rollback> it = this.mRollbacks.iterator();
        while (it.hasNext()) {
            Rollback next = it.next();
            if (next.includesPackage(str)) {
                it.remove();
                deleteRollback(next, str2);
            }
        }
    }

    public void expireRollbackForPackage(final String str) {
        assertNotInWorkerThread();
        this.mContext.enforceCallingOrSelfPermission("android.permission.TEST_MANAGE_ROLLBACKS", "expireRollbackForPackage");
        awaitResult(new Runnable() { // from class: com.android.server.rollback.RollbackManagerServiceImpl$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                RollbackManagerServiceImpl.this.lambda$expireRollbackForPackage$5(str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$expireRollbackForPackage$5(String str) {
        expireRollbackForPackageInternal(str, "Expired by API");
    }

    public void blockRollbackManager(final long j) {
        assertNotInWorkerThread();
        this.mContext.enforceCallingOrSelfPermission("android.permission.TEST_MANAGE_ROLLBACKS", "blockRollbackManager");
        getHandler().post(new Runnable() { // from class: com.android.server.rollback.RollbackManagerServiceImpl$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                RollbackManagerServiceImpl.this.lambda$blockRollbackManager$6(j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$blockRollbackManager$6(long j) {
        assertInWorkerThread();
        this.mSleepDuration.addLast(j);
    }

    public final void queueSleepIfNeeded() {
        assertInWorkerThread();
        if (this.mSleepDuration.size() == 0) {
            return;
        }
        final long removeFirst = this.mSleepDuration.removeFirst();
        if (removeFirst <= 0) {
            return;
        }
        getHandler().post(new Runnable() { // from class: com.android.server.rollback.RollbackManagerServiceImpl$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                RollbackManagerServiceImpl.this.lambda$queueSleepIfNeeded$7(removeFirst);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$queueSleepIfNeeded$7(long j) {
        assertInWorkerThread();
        try {
            Thread.sleep(j);
        } catch (InterruptedException unused) {
            throw new IllegalStateException("RollbackManagerHandlerThread interrupted");
        }
    }

    public void onUnlockUser(final int i) {
        assertNotInWorkerThread();
        if (LOCAL_LOGV) {
            Slog.v("RollbackManager", "onUnlockUser id=" + i);
        }
        awaitResult(new Runnable() { // from class: com.android.server.rollback.RollbackManagerServiceImpl$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                RollbackManagerServiceImpl.this.lambda$onUnlockUser$8(i);
            }
        });
        getHandler().post(new Runnable() { // from class: com.android.server.rollback.RollbackManagerServiceImpl$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                RollbackManagerServiceImpl.this.lambda$onUnlockUser$9(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onUnlockUser$8(int i) {
        assertInWorkerThread();
        ArrayList arrayList = new ArrayList(this.mRollbacks);
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            ((Rollback) arrayList.get(i2)).commitPendingBackupAndRestoreForUser(i, this.mAppDataRollbackHelper);
        }
    }

    /* renamed from: destroyCeSnapshotsForExpiredRollbacks */
    public final void lambda$onUnlockUser$9(int i) {
        int size = this.mRollbacks.size();
        int[] iArr = new int[size];
        for (int i2 = 0; i2 < size; i2++) {
            iArr[i2] = this.mRollbacks.get(i2).info.getRollbackId();
        }
        ApexManager.getInstance().destroyCeSnapshotsNotSpecified(i, iArr);
        try {
            this.mInstaller.destroyCeSnapshotsNotSpecified(i, iArr);
        } catch (Installer.InstallerException e) {
            Slog.e("RollbackManager", "Failed to delete snapshots for user: " + i, e);
        }
    }

    public final void updateRollbackLifetimeDurationInMillis() {
        assertInWorkerThread();
        long j = DEFAULT_ROLLBACK_LIFETIME_DURATION_MILLIS;
        long j2 = DeviceConfig.getLong("rollback_boot", "rollback_lifetime_in_millis", j);
        this.mRollbackLifetimeDurationInMillis = j2;
        if (j2 < 0) {
            this.mRollbackLifetimeDurationInMillis = j;
        }
        Slog.d("RollbackManager", "mRollbackLifetimeDurationInMillis=" + this.mRollbackLifetimeDurationInMillis);
        runExpiration();
    }

    public void onBootCompleted() {
        DeviceConfig.addOnPropertiesChangedListener("rollback_boot", this.mExecutor, new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.rollback.RollbackManagerServiceImpl$$ExternalSyntheticLambda2
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                RollbackManagerServiceImpl.this.lambda$onBootCompleted$10(properties);
            }
        });
        getHandler().post(new Runnable() { // from class: com.android.server.rollback.RollbackManagerServiceImpl$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                RollbackManagerServiceImpl.this.lambda$onBootCompleted$11();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBootCompleted$10(DeviceConfig.Properties properties) {
        updateRollbackLifetimeDurationInMillis();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBootCompleted$11() {
        assertInWorkerThread();
        updateRollbackLifetimeDurationInMillis();
        runExpiration();
        ArrayList<Rollback> arrayList = new ArrayList();
        ArrayList<Rollback> arrayList2 = new ArrayList();
        HashSet<String> hashSet = new HashSet();
        Iterator<Rollback> it = this.mRollbacks.iterator();
        while (it.hasNext()) {
            Rollback next = it.next();
            if (next.isStaged()) {
                PackageInstaller.SessionInfo sessionInfo = this.mContext.getPackageManager().getPackageInstaller().getSessionInfo(next.getOriginalSessionId());
                if (sessionInfo == null || sessionInfo.isStagedSessionFailed()) {
                    if (next.isEnabling()) {
                        it.remove();
                        deleteRollback(next, "Session " + next.getOriginalSessionId() + " not existed or failed");
                    }
                } else {
                    if (sessionInfo.isStagedSessionApplied()) {
                        if (next.isEnabling()) {
                            arrayList.add(next);
                        } else if (next.isRestoreUserDataInProgress()) {
                            arrayList2.add(next);
                        }
                    }
                    hashSet.addAll(next.getApexPackageNames());
                }
            }
        }
        for (Rollback rollback : arrayList) {
            makeRollbackAvailable(rollback);
        }
        for (Rollback rollback2 : arrayList2) {
            rollback2.setRestoreUserDataInProgress(false);
        }
        for (String str : hashSet) {
            onPackageReplaced(str);
        }
        this.mPackageHealthObserver.onBootCompletedAsync();
    }

    public final void onPackageReplaced(String str) {
        assertInWorkerThread();
        long installedPackageVersion = getInstalledPackageVersion(str);
        Iterator<Rollback> it = this.mRollbacks.iterator();
        while (it.hasNext()) {
            Rollback next = it.next();
            if (next.isAvailable() && next.includesPackageWithDifferentVersion(str, installedPackageVersion)) {
                it.remove();
                deleteRollback(next, "Package " + str + " replaced");
            }
        }
    }

    public final void onPackageFullyRemoved(String str) {
        assertInWorkerThread();
        expireRollbackForPackageInternal(str, "Package " + str + " removed");
    }

    public static void sendFailure(Context context, IntentSender intentSender, int i, String str) {
        Slog.e("RollbackManager", str);
        try {
            Intent intent = new Intent();
            intent.putExtra("android.content.rollback.extra.STATUS", i);
            intent.putExtra("android.content.rollback.extra.STATUS_MESSAGE", str);
            intentSender.sendIntent(context, 0, intent, null, null);
        } catch (IntentSender.SendIntentException unused) {
        }
    }

    public final void runExpiration() {
        getHandler().removeCallbacks(this.mRunExpiration);
        assertInWorkerThread();
        Instant now = Instant.now();
        Iterator<Rollback> it = this.mRollbacks.iterator();
        Instant instant = null;
        while (it.hasNext()) {
            Rollback next = it.next();
            if (next.isAvailable() || next.isCommitted()) {
                Instant timestamp = next.getTimestamp();
                if (!now.isBefore(timestamp.plusMillis(this.mRollbackLifetimeDurationInMillis))) {
                    Slog.i("RollbackManager", "runExpiration id=" + next.info.getRollbackId());
                    it.remove();
                    deleteRollback(next, "Expired by timeout");
                } else if (instant == null || instant.isAfter(timestamp)) {
                    instant = timestamp;
                }
            }
        }
        if (instant != null) {
            getHandler().postDelayed(this.mRunExpiration, now.until(instant.plusMillis(this.mRollbackLifetimeDurationInMillis), ChronoUnit.MILLIS));
        }
    }

    public final Handler getHandler() {
        return this.mHandler;
    }

    public final Context getContextAsUser(UserHandle userHandle) {
        try {
            Context context = this.mContext;
            return context.createPackageContextAsUser(context.getPackageName(), 0, userHandle);
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    public final boolean enableRollback(int i) {
        assertInWorkerThread();
        if (LOCAL_LOGV) {
            Slog.v("RollbackManager", "enableRollback sessionId=" + i);
        }
        PackageInstaller packageInstaller = this.mContext.getPackageManager().getPackageInstaller();
        PackageInstaller.SessionInfo sessionInfo = packageInstaller.getSessionInfo(i);
        if (sessionInfo == null) {
            Slog.e("RollbackManager", "Unable to find session for enabled rollback.");
            return false;
        }
        PackageInstaller.SessionInfo sessionInfo2 = sessionInfo.hasParentSessionId() ? packageInstaller.getSessionInfo(sessionInfo.getParentSessionId()) : sessionInfo;
        if (sessionInfo2 == null) {
            Slog.e("RollbackManager", "Unable to find parent session for enabled rollback.");
            return false;
        }
        Rollback rollbackForSession = getRollbackForSession(sessionInfo.getSessionId());
        if (rollbackForSession == null) {
            rollbackForSession = createNewRollback(sessionInfo2);
        }
        if (enableRollbackForPackageSession(rollbackForSession, sessionInfo)) {
            if (rollbackForSession.allPackagesEnabled()) {
                return completeEnableRollback(rollbackForSession);
            }
            return true;
        }
        return false;
    }

    public final int computeRollbackDataPolicy(int i, int i2) {
        assertInWorkerThread();
        return i2 != 0 ? i2 : i;
    }

    public final boolean enableRollbackForPackageSession(Rollback rollback, PackageInstaller.SessionInfo sessionInfo) {
        assertInWorkerThread();
        int i = sessionInfo.installFlags;
        if ((262144 & i) == 0) {
            Slog.e("RollbackManager", "Rollback is not enabled.");
            return false;
        } else if ((i & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) != 0) {
            Slog.e("RollbackManager", "Rollbacks not supported for instant app install");
            return false;
        } else if (sessionInfo.resolvedBaseCodePath == null) {
            Slog.e("RollbackManager", "Session code path has not been resolved.");
            return false;
        } else {
            ParseResult parseApkLite = ApkLiteParseUtils.parseApkLite(ParseTypeImpl.forDefaultParsing().reset(), new File(sessionInfo.resolvedBaseCodePath), 0);
            if (parseApkLite.isError()) {
                Slog.e("RollbackManager", "Unable to parse new package: " + parseApkLite.getErrorMessage(), parseApkLite.getException());
                return false;
            }
            ApkLite apkLite = (ApkLite) parseApkLite.getResult();
            String packageName = apkLite.getPackageName();
            int computeRollbackDataPolicy = computeRollbackDataPolicy(sessionInfo.rollbackDataPolicy, apkLite.getRollbackDataPolicy());
            if (!sessionInfo.isStaged() && (i & IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES) != 0 && computeRollbackDataPolicy != 2) {
                Slog.e("RollbackManager", "Only RETAIN is supported for rebootless APEX: " + packageName);
                return false;
            }
            Slog.i("RollbackManager", "Enabling rollback for install of " + packageName + ", session:" + sessionInfo.sessionId + ", rollbackDataPolicy=" + computeRollbackDataPolicy);
            String installerPackageName = sessionInfo.getInstallerPackageName();
            if (!enableRollbackAllowed(installerPackageName, packageName)) {
                Slog.e("RollbackManager", "Installer " + installerPackageName + " is not allowed to enable rollback on " + packageName);
                return false;
            }
            boolean z = (i & IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES) != 0;
            try {
                PackageInfo packageInfo = getPackageInfo(packageName);
                if (z) {
                    for (String str : ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).getApksInApex(packageName)) {
                        try {
                            if (!rollback.enableForPackageInApex(str, getPackageInfo(str).getLongVersionCode(), computeRollbackDataPolicy)) {
                                return false;
                            }
                        } catch (PackageManager.NameNotFoundException unused) {
                            Slog.e("RollbackManager", str + " is not installed");
                            return false;
                        }
                    }
                }
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                return rollback.enableForPackage(packageName, apkLite.getVersionCode(), packageInfo.getLongVersionCode(), z, applicationInfo.sourceDir, applicationInfo.splitSourceDirs, computeRollbackDataPolicy);
            } catch (PackageManager.NameNotFoundException unused2) {
                Slog.e("RollbackManager", packageName + " is not installed");
                return false;
            }
        }
    }

    @Override // com.android.server.rollback.RollbackManagerInternal
    public void snapshotAndRestoreUserData(String str, List<UserHandle> list, int i, long j, String str2, int i2) {
        assertNotInWorkerThread();
        snapshotAndRestoreUserData(str, UserHandle.fromUserHandles(list), i, j, str2, i2);
    }

    public void snapshotAndRestoreUserData(final String str, final int[] iArr, final int i, long j, final String str2, final int i2) {
        assertNotInWorkerThread();
        if (Binder.getCallingUid() != 1000) {
            throw new SecurityException("snapshotAndRestoreUserData may only be called by the system.");
        }
        getHandler().post(new Runnable() { // from class: com.android.server.rollback.RollbackManagerServiceImpl$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                RollbackManagerServiceImpl.this.lambda$snapshotAndRestoreUserData$12(str, iArr, i, str2, i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$snapshotAndRestoreUserData$12(String str, int[] iArr, int i, String str2, int i2) {
        assertInWorkerThread();
        snapshotUserDataInternal(str, iArr);
        restoreUserDataInternal(str, iArr, i, str2);
        if (i2 > 0) {
            ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).finishPackageInstall(i2, false);
        }
    }

    public final void snapshotUserDataInternal(String str, int[] iArr) {
        assertInWorkerThread();
        if (LOCAL_LOGV) {
            Slog.v("RollbackManager", "snapshotUserData pkg=" + str + " users=" + Arrays.toString(iArr));
        }
        for (int i = 0; i < this.mRollbacks.size(); i++) {
            this.mRollbacks.get(i).snapshotUserData(str, iArr, this.mAppDataRollbackHelper);
        }
    }

    public final void restoreUserDataInternal(String str, int[] iArr, int i, String str2) {
        assertInWorkerThread();
        if (LOCAL_LOGV) {
            Slog.v("RollbackManager", "restoreUserData pkg=" + str + " users=" + Arrays.toString(iArr));
        }
        for (int i2 = 0; i2 < this.mRollbacks.size() && !this.mRollbacks.get(i2).restoreUserDataForPackageIfInProgress(str, iArr, i, str2, this.mAppDataRollbackHelper); i2++) {
        }
    }

    @Override // com.android.server.rollback.RollbackManagerInternal
    public int notifyStagedSession(final int i) {
        assertNotInWorkerThread();
        if (Binder.getCallingUid() != 1000) {
            throw new SecurityException("notifyStagedSession may only be called by the system.");
        }
        return ((Integer) awaitResult(new Supplier() { // from class: com.android.server.rollback.RollbackManagerServiceImpl$$ExternalSyntheticLambda9
            @Override // java.util.function.Supplier
            public final Object get() {
                Integer lambda$notifyStagedSession$13;
                lambda$notifyStagedSession$13 = RollbackManagerServiceImpl.this.lambda$notifyStagedSession$13(i);
                return lambda$notifyStagedSession$13;
            }
        })).intValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$notifyStagedSession$13(int i) {
        assertInWorkerThread();
        Rollback rollbackForSession = getRollbackForSession(i);
        return Integer.valueOf(rollbackForSession != null ? rollbackForSession.info.getRollbackId() : -1);
    }

    public final boolean enableRollbackAllowed(String str, String str2) {
        if (str == null) {
            return false;
        }
        PackageManager packageManager = this.mContext.getPackageManager();
        return (isRollbackAllowed(str2) && (packageManager.checkPermission("android.permission.MANAGE_ROLLBACKS", str) == 0)) || (packageManager.checkPermission("android.permission.TEST_MANAGE_ROLLBACKS", str) == 0);
    }

    public final boolean isRollbackAllowed(String str) {
        return SystemConfig.getInstance().getRollbackWhitelistedPackages().contains(str) || isModule(str);
    }

    public final boolean isModule(String str) {
        try {
            return this.mContext.getPackageManager().getModuleInfo(str, 0) != null;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public final long getInstalledPackageVersion(String str) {
        try {
            return getPackageInfo(str).getLongVersionCode();
        } catch (PackageManager.NameNotFoundException unused) {
            return -1L;
        }
    }

    public final PackageInfo getPackageInfo(String str) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = this.mContext.getPackageManager();
        try {
            return packageManager.getPackageInfo(str, 4194304);
        } catch (PackageManager.NameNotFoundException unused) {
            return packageManager.getPackageInfo(str, 1073741824);
        }
    }

    /* loaded from: classes2.dex */
    public class SessionCallback extends PackageInstaller.SessionCallback {
        @Override // android.content.pm.PackageInstaller.SessionCallback
        public void onActiveChanged(int i, boolean z) {
        }

        @Override // android.content.pm.PackageInstaller.SessionCallback
        public void onBadgingChanged(int i) {
        }

        @Override // android.content.pm.PackageInstaller.SessionCallback
        public void onCreated(int i) {
        }

        @Override // android.content.pm.PackageInstaller.SessionCallback
        public void onProgressChanged(int i, float f) {
        }

        public SessionCallback() {
        }

        @Override // android.content.pm.PackageInstaller.SessionCallback
        public void onFinished(int i, boolean z) {
            RollbackManagerServiceImpl.this.assertInWorkerThread();
            if (RollbackManagerServiceImpl.LOCAL_LOGV) {
                Slog.v("RollbackManager", "SessionCallback.onFinished id=" + i + " success=" + z);
            }
            Rollback rollbackForSession = RollbackManagerServiceImpl.this.getRollbackForSession(i);
            if (rollbackForSession != null && rollbackForSession.isEnabling() && i == rollbackForSession.getOriginalSessionId()) {
                if (z) {
                    if (rollbackForSession.isStaged() || !RollbackManagerServiceImpl.this.completeEnableRollback(rollbackForSession)) {
                        return;
                    }
                    RollbackManagerServiceImpl.this.makeRollbackAvailable(rollbackForSession);
                    return;
                }
                Slog.w("RollbackManager", "Delete rollback id=" + rollbackForSession.info.getRollbackId() + " for failed session id=" + i);
                RollbackManagerServiceImpl.this.mRollbacks.remove(rollbackForSession);
                RollbackManagerServiceImpl rollbackManagerServiceImpl = RollbackManagerServiceImpl.this;
                rollbackManagerServiceImpl.deleteRollback(rollbackForSession, "Session " + i + " failed");
            }
        }
    }

    public final boolean completeEnableRollback(Rollback rollback) {
        assertInWorkerThread();
        if (LOCAL_LOGV) {
            Slog.v("RollbackManager", "completeEnableRollback id=" + rollback.info.getRollbackId());
        }
        if (!rollback.allPackagesEnabled()) {
            Slog.e("RollbackManager", "Failed to enable rollback for all packages in session.");
            this.mRollbacks.remove(rollback);
            deleteRollback(rollback, "Failed to enable rollback for all packages in session");
            return false;
        }
        rollback.saveRollback();
        return true;
    }

    @GuardedBy({"rollback.getLock"})
    public final void makeRollbackAvailable(Rollback rollback) {
        assertInWorkerThread();
        Slog.i("RollbackManager", "makeRollbackAvailable id=" + rollback.info.getRollbackId());
        rollback.makeAvailable();
        this.mPackageHealthObserver.notifyRollbackAvailable(rollback.info);
        this.mPackageHealthObserver.startObservingHealth(rollback.getPackageNames(), this.mRollbackLifetimeDurationInMillis);
        runExpiration();
    }

    public final Rollback getRollbackForId(int i) {
        assertInWorkerThread();
        for (int i2 = 0; i2 < this.mRollbacks.size(); i2++) {
            Rollback rollback = this.mRollbacks.get(i2);
            if (rollback.info.getRollbackId() == i) {
                return rollback;
            }
        }
        return null;
    }

    public final int allocateRollbackId() {
        assertInWorkerThread();
        int i = 0;
        while (true) {
            int nextInt = this.mRandom.nextInt(2147483646) + 1;
            if (!this.mAllocatedRollbackIds.get(nextInt, false)) {
                this.mAllocatedRollbackIds.put(nextInt, true);
                return nextInt;
            }
            int i2 = i + 1;
            if (i >= 32) {
                throw new IllegalStateException("Failed to allocate rollback ID");
            }
            i = i2;
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        assertNotInWorkerThread();
        if (DumpUtils.checkDumpPermission(this.mContext, "RollbackManager", printWriter)) {
            final IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
            awaitResult(new Runnable() { // from class: com.android.server.rollback.RollbackManagerServiceImpl$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    RollbackManagerServiceImpl.this.lambda$dump$14(indentingPrintWriter);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$dump$14(IndentingPrintWriter indentingPrintWriter) {
        assertInWorkerThread();
        for (Rollback rollback : this.mRollbacks) {
            rollback.dump(indentingPrintWriter);
        }
        indentingPrintWriter.println();
        List<Rollback> loadHistorialRollbacks = this.mRollbackStore.loadHistorialRollbacks();
        if (!loadHistorialRollbacks.isEmpty()) {
            indentingPrintWriter.println("Historical rollbacks:");
            indentingPrintWriter.increaseIndent();
            for (Rollback rollback2 : loadHistorialRollbacks) {
                rollback2.dump(indentingPrintWriter);
            }
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println();
        }
        PackageWatchdog.getInstance(this.mContext).dump(indentingPrintWriter);
    }

    public final void enforceManageRollbacks(String str) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MANAGE_ROLLBACKS") == 0 || this.mContext.checkCallingOrSelfPermission("android.permission.TEST_MANAGE_ROLLBACKS") == 0) {
            return;
        }
        throw new SecurityException(str + " requires android.permission.MANAGE_ROLLBACKS or android.permission.TEST_MANAGE_ROLLBACKS");
    }

    public final Rollback createNewRollback(PackageInstaller.SessionInfo sessionInfo) {
        int identifier;
        int[] iArr;
        Rollback createNonStagedRollback;
        assertInWorkerThread();
        int allocateRollbackId = allocateRollbackId();
        if (sessionInfo.getUser().equals(UserHandle.ALL)) {
            identifier = UserHandle.SYSTEM.getIdentifier();
        } else {
            identifier = sessionInfo.getUser().getIdentifier();
        }
        int i = identifier;
        String installerPackageName = sessionInfo.getInstallerPackageName();
        int sessionId = sessionInfo.getSessionId();
        if (LOCAL_LOGV) {
            Slog.v("RollbackManager", "createNewRollback id=" + allocateRollbackId + " user=" + i + " installer=" + installerPackageName);
        }
        if (sessionInfo.isMultiPackage()) {
            iArr = sessionInfo.getChildSessionIds();
        } else {
            iArr = new int[]{sessionId};
        }
        int[] iArr2 = iArr;
        if (sessionInfo.isStaged()) {
            createNonStagedRollback = this.mRollbackStore.createStagedRollback(allocateRollbackId, sessionId, i, installerPackageName, iArr2, getExtensionVersions());
        } else {
            createNonStagedRollback = this.mRollbackStore.createNonStagedRollback(allocateRollbackId, sessionId, i, installerPackageName, iArr2, getExtensionVersions());
        }
        this.mRollbacks.add(createNonStagedRollback);
        return createNonStagedRollback;
    }

    public final SparseIntArray getExtensionVersions() {
        Map allExtensionVersions = SdkExtensions.getAllExtensionVersions();
        SparseIntArray sparseIntArray = new SparseIntArray(allExtensionVersions.size());
        for (Integer num : allExtensionVersions.keySet()) {
            int intValue = num.intValue();
            sparseIntArray.put(intValue, ((Integer) allExtensionVersions.get(Integer.valueOf(intValue))).intValue());
        }
        return sparseIntArray;
    }

    public final Rollback getRollbackForSession(int i) {
        assertInWorkerThread();
        for (int i2 = 0; i2 < this.mRollbacks.size(); i2++) {
            Rollback rollback = this.mRollbacks.get(i2);
            if (rollback.getOriginalSessionId() == i || rollback.containsSessionId(i)) {
                return rollback;
            }
        }
        return null;
    }

    public final void deleteRollback(Rollback rollback, String str) {
        assertInWorkerThread();
        rollback.delete(this.mAppDataRollbackHelper, str);
        this.mRollbackStore.saveRollbackToHistory(rollback);
    }
}
