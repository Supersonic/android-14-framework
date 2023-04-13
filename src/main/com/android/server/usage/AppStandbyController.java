package com.android.server.usage;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.AppStandbyInfo;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.app.usage.UsageStatsManagerInternal;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ApplicationInfo;
import android.content.pm.CrossProfileAppsInternal;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.hardware.display.DisplayManager;
import android.net.NetworkScoreManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IDeviceIdleController;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.p005os.IInstalld;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.util.SparseSetArray;
import android.util.TimeUtils;
import android.widget.Toast;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IAppOpsCallback;
import com.android.internal.app.IAppOpsService;
import com.android.internal.app.IBatteryStats;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.jobs.ArrayUtils;
import com.android.internal.util.jobs.ConcurrentUtils;
import com.android.server.AlarmManagerInternal;
import com.android.server.AppSchedulingModuleThread;
import com.android.server.LocalServices;
import com.android.server.backup.BackupManagerConstants;
import com.android.server.clipboard.ClipboardService;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.usage.AppIdleHistory;
import com.android.server.usage.AppStandbyInternal;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import libcore.util.EmptyArray;
/* loaded from: classes2.dex */
public class AppStandbyController implements AppStandbyInternal, UsageStatsManagerInternal.UsageEventListener {
    @GuardedBy({"mActiveAdminApps"})
    public final SparseArray<Set<String>> mActiveAdminApps;
    public final CountDownLatch mAdminDataAvailableLatch;
    public boolean mAllowRestrictedBucket;
    public volatile boolean mAppIdleEnabled;
    @GuardedBy({"mAppIdleLock"})
    public AppIdleHistory mAppIdleHistory;
    public final Object mAppIdleLock;
    public AppOpsManager mAppOpsManager;
    public long[] mAppStandbyElapsedThresholds;
    public final Map<String, String> mAppStandbyProperties;
    public long[] mAppStandbyScreenThresholds;
    public AppWidgetManager mAppWidgetManager;
    public final SparseSetArray<String> mAppsToRestoreToRare;
    public volatile String mBroadcastResponseExemptedPermissions;
    public volatile List<String> mBroadcastResponseExemptedPermissionsList;
    public volatile String mBroadcastResponseExemptedRoles;
    public volatile List<String> mBroadcastResponseExemptedRolesList;
    public volatile int mBroadcastResponseFgThresholdState;
    public volatile long mBroadcastResponseWindowDurationMillis;
    public volatile long mBroadcastSessionsDurationMs;
    public volatile long mBroadcastSessionsWithResponseDurationMs;
    public String mCachedDeviceProvisioningPackage;
    public volatile String mCachedNetworkScorer;
    public volatile long mCachedNetworkScorerAtMillis;
    @GuardedBy({"mCarrierPrivilegedLock"})
    public List<String> mCarrierPrivilegedApps;
    public final Object mCarrierPrivilegedLock;
    public long mCheckIdleIntervalMillis;
    public final Context mContext;
    public final DisplayManager.DisplayListener mDisplayListener;
    public long mExemptedSyncScheduledDozeTimeoutMillis;
    public long mExemptedSyncScheduledNonDozeTimeoutMillis;
    public long mExemptedSyncStartTimeoutMillis;
    public final AppStandbyHandler mHandler;
    @GuardedBy({"mCarrierPrivilegedLock"})
    public boolean mHaveCarrierPrivilegedApps;
    @GuardedBy({"mHeadlessSystemApps"})
    public final ArraySet<String> mHeadlessSystemApps;
    public long mInitialForegroundServiceStartTimeoutMillis;
    public Injector mInjector;
    public volatile boolean mIsCharging;
    public boolean mLinkCrossProfileApps;
    public volatile boolean mNoteResponseEventForAllBroadcastSessions;
    public int mNotificationSeenPromotedBucket;
    public long mNotificationSeenTimeoutMillis;
    @GuardedBy({"mPackageAccessListeners"})
    public final ArrayList<AppStandbyInternal.AppIdleStateChangeListener> mPackageAccessListeners;
    public PackageManager mPackageManager;
    @GuardedBy({"mPendingIdleStateChecks"})
    public final SparseLongArray mPendingIdleStateChecks;
    public boolean mPendingInitializeDefaults;
    public volatile boolean mPendingOneTimeCheckIdleStates;
    public long mPredictionTimeoutMillis;
    public boolean mRetainNotificationSeenImpactForPreTApps;
    public long mSlicePinnedTimeoutMillis;
    public long mStrongUsageTimeoutMillis;
    public long mSyncAdapterTimeoutMillis;
    @GuardedBy({"mSystemExemptionAppOpMode"})
    public final SparseIntArray mSystemExemptionAppOpMode;
    public long mSystemInteractionTimeoutMillis;
    public final ArrayList<Integer> mSystemPackagesAppIds;
    public boolean mSystemServicesReady;
    public long mSystemUpdateUsageTimeoutMillis;
    public boolean mTriggerQuotaBumpOnNotificationSeen;
    public long mUnexemptedSyncScheduledTimeoutMillis;
    @VisibleForTesting
    static final long[] DEFAULT_SCREEN_TIME_THRESHOLDS = {0, 0, ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS, 7200000, 21600000};
    @VisibleForTesting
    static final long[] MINIMUM_SCREEN_TIME_THRESHOLDS = {0, 0, 0, 1800000, ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS};
    @VisibleForTesting
    static final long[] DEFAULT_ELAPSED_TIME_THRESHOLDS = {0, 43200000, BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS, 172800000, 691200000};
    @VisibleForTesting
    static final long[] MINIMUM_ELAPSED_TIME_THRESHOLDS = {0, ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS, ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS, 7200000, BackupManagerConstants.DEFAULT_KEY_VALUE_BACKUP_INTERVAL_MILLISECONDS};
    public static final int[] THRESHOLD_BUCKETS = {10, 20, 30, 40, 45};

    /* loaded from: classes2.dex */
    public static class Lock {
    }

    public static boolean isUserUsage(int i) {
        if ((65280 & i) == 768) {
            int i2 = i & 255;
            return i2 == 3 || i2 == 4;
        }
        return false;
    }

    public final int usageEventToSubReason(int i) {
        if (i != 1) {
            if (i != 2) {
                if (i != 6) {
                    if (i != 7) {
                        if (i != 10) {
                            if (i != 19) {
                                if (i != 13) {
                                    return i != 14 ? 0 : 9;
                                }
                                return 10;
                            }
                            return 15;
                        }
                        return 2;
                    }
                    return 3;
                }
                return 1;
            }
            return 5;
        }
        return 4;
    }

    /* loaded from: classes2.dex */
    public static class Pool<T> {
        public final T[] mArray;
        public int mSize = 0;

        public Pool(T[] tArr) {
            this.mArray = tArr;
        }

        public synchronized T obtain() {
            T t;
            int i = this.mSize;
            if (i > 0) {
                T[] tArr = this.mArray;
                int i2 = i - 1;
                this.mSize = i2;
                t = tArr[i2];
            } else {
                t = null;
            }
            return t;
        }

        public synchronized void recycle(T t) {
            int i = this.mSize;
            T[] tArr = this.mArray;
            if (i < tArr.length) {
                this.mSize = i + 1;
                tArr[i] = t;
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class StandbyUpdateRecord {
        public static final Pool<StandbyUpdateRecord> sPool = new Pool<>(new StandbyUpdateRecord[10]);
        public int bucket;
        public boolean isUserInteraction;
        public String packageName;
        public int reason;
        public int userId;

        public static StandbyUpdateRecord obtain(String str, int i, int i2, int i3, boolean z) {
            StandbyUpdateRecord obtain = sPool.obtain();
            if (obtain == null) {
                obtain = new StandbyUpdateRecord();
            }
            obtain.packageName = str;
            obtain.userId = i;
            obtain.bucket = i2;
            obtain.reason = i3;
            obtain.isUserInteraction = z;
            return obtain;
        }

        public void recycle() {
            sPool.recycle(this);
        }
    }

    /* loaded from: classes2.dex */
    public static class ContentProviderUsageRecord {
        public static final Pool<ContentProviderUsageRecord> sPool = new Pool<>(new ContentProviderUsageRecord[10]);
        public String name;
        public String packageName;
        public int userId;

        public static ContentProviderUsageRecord obtain(String str, String str2, int i) {
            ContentProviderUsageRecord obtain = sPool.obtain();
            if (obtain == null) {
                obtain = new ContentProviderUsageRecord();
            }
            obtain.name = str;
            obtain.packageName = str2;
            obtain.userId = i;
            return obtain;
        }

        public void recycle() {
            sPool.recycle(this);
        }
    }

    public AppStandbyController(Context context) {
        this(new Injector(context, AppSchedulingModuleThread.get().getLooper()));
    }

    public AppStandbyController(Injector injector) {
        Lock lock = new Lock();
        this.mAppIdleLock = lock;
        this.mPackageAccessListeners = new ArrayList<>();
        this.mCarrierPrivilegedLock = new Lock();
        this.mActiveAdminApps = new SparseArray<>();
        this.mHeadlessSystemApps = new ArraySet<>();
        this.mAdminDataAvailableLatch = new CountDownLatch(1);
        this.mPendingIdleStateChecks = new SparseLongArray();
        this.mSystemExemptionAppOpMode = new SparseIntArray();
        this.mCachedNetworkScorer = null;
        this.mCachedNetworkScorerAtMillis = 0L;
        this.mCachedDeviceProvisioningPackage = null;
        long[] jArr = DEFAULT_ELAPSED_TIME_THRESHOLDS;
        this.mCheckIdleIntervalMillis = Math.min(jArr[1] / 4, (long) BackupManagerConstants.DEFAULT_KEY_VALUE_BACKUP_INTERVAL_MILLISECONDS);
        this.mAppStandbyScreenThresholds = DEFAULT_SCREEN_TIME_THRESHOLDS;
        this.mAppStandbyElapsedThresholds = jArr;
        this.mStrongUsageTimeoutMillis = ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS;
        this.mNotificationSeenTimeoutMillis = 43200000L;
        this.mSlicePinnedTimeoutMillis = 43200000L;
        this.mNotificationSeenPromotedBucket = 20;
        this.mTriggerQuotaBumpOnNotificationSeen = false;
        this.mRetainNotificationSeenImpactForPreTApps = false;
        this.mSystemUpdateUsageTimeoutMillis = 7200000L;
        this.mPredictionTimeoutMillis = 43200000L;
        this.mSyncAdapterTimeoutMillis = 600000L;
        this.mExemptedSyncScheduledNonDozeTimeoutMillis = 600000L;
        this.mExemptedSyncScheduledDozeTimeoutMillis = BackupManagerConstants.DEFAULT_KEY_VALUE_BACKUP_INTERVAL_MILLISECONDS;
        this.mExemptedSyncStartTimeoutMillis = 600000L;
        this.mUnexemptedSyncScheduledTimeoutMillis = 600000L;
        this.mSystemInteractionTimeoutMillis = 600000L;
        this.mInitialForegroundServiceStartTimeoutMillis = 1800000L;
        this.mLinkCrossProfileApps = true;
        this.mBroadcastResponseWindowDurationMillis = 120000L;
        this.mBroadcastResponseFgThresholdState = 2;
        this.mBroadcastSessionsDurationMs = 120000L;
        this.mBroadcastSessionsWithResponseDurationMs = 120000L;
        this.mNoteResponseEventForAllBroadcastSessions = true;
        this.mBroadcastResponseExemptedRoles = "";
        List<String> list = Collections.EMPTY_LIST;
        this.mBroadcastResponseExemptedRolesList = list;
        this.mBroadcastResponseExemptedPermissions = "";
        this.mBroadcastResponseExemptedPermissionsList = list;
        this.mAppStandbyProperties = new ArrayMap();
        this.mAppsToRestoreToRare = new SparseSetArray<>();
        this.mSystemPackagesAppIds = new ArrayList<>();
        this.mSystemServicesReady = false;
        this.mDisplayListener = new DisplayManager.DisplayListener() { // from class: com.android.server.usage.AppStandbyController.2
            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayAdded(int i) {
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayRemoved(int i) {
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayChanged(int i) {
                if (i == 0) {
                    boolean isDisplayOn = AppStandbyController.this.isDisplayOn();
                    synchronized (AppStandbyController.this.mAppIdleLock) {
                        AppStandbyController.this.mAppIdleHistory.updateDisplay(isDisplayOn, AppStandbyController.this.mInjector.elapsedRealtime());
                    }
                }
            }
        };
        this.mInjector = injector;
        Context context = injector.getContext();
        this.mContext = context;
        AppStandbyHandler appStandbyHandler = new AppStandbyHandler(this.mInjector.getLooper());
        this.mHandler = appStandbyHandler;
        this.mPackageManager = context.getPackageManager();
        DeviceStateReceiver deviceStateReceiver = new DeviceStateReceiver();
        IntentFilter intentFilter = new IntentFilter("android.os.action.CHARGING");
        intentFilter.addAction("android.os.action.DISCHARGING");
        intentFilter.addAction("android.os.action.POWER_SAVE_WHITELIST_CHANGED");
        context.registerReceiver(deviceStateReceiver, intentFilter);
        synchronized (lock) {
            this.mAppIdleHistory = new AppIdleHistory(this.mInjector.getDataSystemDirectory(), this.mInjector.elapsedRealtime());
        }
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter2.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter2.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter2.addDataScheme("package");
        context.registerReceiverAsUser(new PackageReceiver(), UserHandle.ALL, intentFilter2, null, appStandbyHandler);
    }

    @VisibleForTesting
    public void setAppIdleEnabled(boolean z) {
        UsageStatsManagerInternal usageStatsManagerInternal = (UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class);
        if (z) {
            usageStatsManagerInternal.registerListener(this);
        } else {
            usageStatsManagerInternal.unregisterListener(this);
        }
        synchronized (this.mAppIdleLock) {
            if (this.mAppIdleEnabled != z) {
                boolean isInParole = isInParole();
                this.mAppIdleEnabled = z;
                if (isInParole() != isInParole) {
                    postParoleStateChanged();
                }
            }
        }
    }

    public boolean isAppIdleEnabled() {
        return this.mAppIdleEnabled;
    }

    public void onBootPhase(int i) {
        int i2;
        boolean userFileExists;
        this.mInjector.onBootPhase(i);
        if (i != 500) {
            if (i == 1000) {
                setChargingState(this.mInjector.isCharging());
                this.mHandler.post(new Runnable() { // from class: com.android.server.usage.AppStandbyController$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        AppStandbyController.this.updatePowerWhitelistCache();
                    }
                });
                this.mHandler.post(new Runnable() { // from class: com.android.server.usage.AppStandbyController$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        AppStandbyController.this.loadHeadlessSystemAppCache();
                    }
                });
                return;
            }
            return;
        }
        Slog.d("AppStandbyController", "Setting app idle enabled state");
        if (this.mAppIdleEnabled) {
            ((UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class)).registerListener(this);
        }
        new ConstantsObserver(this.mHandler).start();
        this.mAppWidgetManager = (AppWidgetManager) this.mContext.getSystemService(AppWidgetManager.class);
        this.mAppOpsManager = (AppOpsManager) this.mContext.getSystemService(AppOpsManager.class);
        try {
            this.mInjector.getAppOpsService().startWatchingMode(128, (String) null, new IAppOpsCallback.Stub() { // from class: com.android.server.usage.AppStandbyController.1
                public void opChanged(int i3, int i4, String str) {
                    int userId = UserHandle.getUserId(i4);
                    synchronized (AppStandbyController.this.mSystemExemptionAppOpMode) {
                        AppStandbyController.this.mSystemExemptionAppOpMode.delete(i4);
                    }
                    AppStandbyController.this.mHandler.obtainMessage(11, userId, i4, str).sendToTarget();
                }
            });
        } catch (RemoteException e) {
            Slog.wtf("AppStandbyController", "Failed start watching for app op", e);
        }
        this.mInjector.registerDisplayListener(this.mDisplayListener, this.mHandler);
        synchronized (this.mAppIdleLock) {
            this.mAppIdleHistory.updateDisplay(isDisplayOn(), this.mInjector.elapsedRealtime());
        }
        this.mSystemServicesReady = true;
        synchronized (this.mAppIdleLock) {
            userFileExists = this.mAppIdleHistory.userFileExists(0);
        }
        if (this.mPendingInitializeDefaults || !userFileExists) {
            initializeDefaultsForSystemApps(0);
        }
        if (this.mPendingOneTimeCheckIdleStates) {
            postOneTimeCheckIdleStates();
        }
        List<ApplicationInfo> installedApplications = this.mPackageManager.getInstalledApplications(542908416);
        int size = installedApplications.size();
        for (i2 = 0; i2 < size; i2++) {
            this.mSystemPackagesAppIds.add(Integer.valueOf(UserHandle.getAppId(installedApplications.get(i2).uid)));
        }
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:30:? -> B:20:0x0065). Please submit an issue!!! */
    public final void reportContentProviderUsage(String str, String str2, int i) {
        int i2;
        int i3;
        Object obj;
        if (this.mAppIdleEnabled) {
            String[] syncAdapterPackagesForAuthorityAsUser = ContentResolver.getSyncAdapterPackagesForAuthorityAsUser(str, i);
            PackageManagerInternal packageManagerInternal = this.mInjector.getPackageManagerInternal();
            long elapsedRealtime = this.mInjector.elapsedRealtime();
            int length = syncAdapterPackagesForAuthorityAsUser.length;
            int i4 = 0;
            while (i4 < length) {
                String str3 = syncAdapterPackagesForAuthorityAsUser[i4];
                if (!str3.equals(str2)) {
                    if (this.mSystemPackagesAppIds.contains(Integer.valueOf(UserHandle.getAppId(packageManagerInternal.getPackageUid(str3, 0L, i))))) {
                        List<UserHandle> crossProfileTargets = getCrossProfileTargets(str3, i);
                        Object obj2 = this.mAppIdleLock;
                        synchronized (obj2) {
                            try {
                                obj = obj2;
                                i2 = i4;
                                i3 = length;
                                try {
                                    reportNoninteractiveUsageCrossUserLocked(str3, i, 10, 8, elapsedRealtime, this.mSyncAdapterTimeoutMillis, crossProfileTargets);
                                    i4 = i2 + 1;
                                    length = i3;
                                } catch (Throwable th) {
                                    th = th;
                                    throw th;
                                }
                            } catch (Throwable th2) {
                                th = th2;
                                obj = obj2;
                                throw th;
                            }
                        }
                    }
                }
                i2 = i4;
                i3 = length;
                i4 = i2 + 1;
                length = i3;
            }
        }
    }

    public final void reportExemptedSyncScheduled(String str, int i) {
        long j;
        int i2;
        int i3;
        if (this.mAppIdleEnabled) {
            if (!this.mInjector.isDeviceIdleMode()) {
                j = this.mExemptedSyncScheduledNonDozeTimeoutMillis;
                i2 = 10;
                i3 = 11;
            } else {
                j = this.mExemptedSyncScheduledDozeTimeoutMillis;
                i2 = 20;
                i3 = 12;
            }
            long j2 = j;
            int i4 = i3;
            int i5 = i2;
            long elapsedRealtime = this.mInjector.elapsedRealtime();
            List<UserHandle> crossProfileTargets = getCrossProfileTargets(str, i);
            synchronized (this.mAppIdleLock) {
                reportNoninteractiveUsageCrossUserLocked(str, i, i5, i4, elapsedRealtime, j2, crossProfileTargets);
            }
        }
    }

    public final void reportUnexemptedSyncScheduled(String str, int i) {
        if (this.mAppIdleEnabled) {
            long elapsedRealtime = this.mInjector.elapsedRealtime();
            synchronized (this.mAppIdleLock) {
                if (this.mAppIdleHistory.getAppStandbyBucket(str, i, elapsedRealtime) == 50) {
                    reportNoninteractiveUsageCrossUserLocked(str, i, 20, 14, elapsedRealtime, this.mUnexemptedSyncScheduledTimeoutMillis, getCrossProfileTargets(str, i));
                }
            }
        }
    }

    public final void reportExemptedSyncStart(String str, int i) {
        if (this.mAppIdleEnabled) {
            long elapsedRealtime = this.mInjector.elapsedRealtime();
            List<UserHandle> crossProfileTargets = getCrossProfileTargets(str, i);
            synchronized (this.mAppIdleLock) {
                reportNoninteractiveUsageCrossUserLocked(str, i, 10, 13, elapsedRealtime, this.mExemptedSyncStartTimeoutMillis, crossProfileTargets);
            }
        }
    }

    public final void reportNoninteractiveUsageCrossUserLocked(String str, int i, int i2, int i3, long j, long j2, List<UserHandle> list) {
        reportNoninteractiveUsageLocked(str, i, i2, i3, j, j2);
        int size = list.size();
        for (int i4 = 0; i4 < size; i4++) {
            reportNoninteractiveUsageLocked(str, list.get(i4).getIdentifier(), i2, i3, j, j2);
        }
    }

    public final void reportNoninteractiveUsageLocked(String str, int i, int i2, int i3, long j, long j2) {
        AppIdleHistory.AppUsageHistory reportUsage = this.mAppIdleHistory.reportUsage(str, i, i2, i3, 0L, j + j2);
        AppStandbyHandler appStandbyHandler = this.mHandler;
        appStandbyHandler.sendMessageDelayed(appStandbyHandler.obtainMessage(11, i, -1, str), j2);
        maybeInformListeners(str, i, j, reportUsage.currentBucket, reportUsage.bucketingReason, false);
    }

    public final void triggerListenerQuotaBump(String str, int i) {
        if (this.mAppIdleEnabled) {
            synchronized (this.mPackageAccessListeners) {
                Iterator<AppStandbyInternal.AppIdleStateChangeListener> it = this.mPackageAccessListeners.iterator();
                while (it.hasNext()) {
                    it.next().triggerTemporaryQuotaBump(str, i);
                }
            }
        }
    }

    @VisibleForTesting
    public void setChargingState(boolean z) {
        if (this.mIsCharging != z) {
            this.mIsCharging = z;
            postParoleStateChanged();
        }
    }

    public boolean isInParole() {
        return !this.mAppIdleEnabled || this.mIsCharging;
    }

    public final void postParoleStateChanged() {
        this.mHandler.removeMessages(9);
        this.mHandler.sendEmptyMessage(9);
    }

    public void postCheckIdleStates(int i) {
        if (i == -1) {
            postOneTimeCheckIdleStates();
            return;
        }
        synchronized (this.mPendingIdleStateChecks) {
            this.mPendingIdleStateChecks.put(i, this.mInjector.elapsedRealtime());
        }
        this.mHandler.obtainMessage(5).sendToTarget();
    }

    public void postOneTimeCheckIdleStates() {
        if (this.mInjector.getBootPhase() < 500) {
            this.mPendingOneTimeCheckIdleStates = true;
            return;
        }
        this.mHandler.sendEmptyMessage(10);
        this.mPendingOneTimeCheckIdleStates = false;
    }

    @VisibleForTesting
    public boolean checkIdleStates(int i) {
        if (this.mAppIdleEnabled) {
            try {
                int[] runningUserIds = this.mInjector.getRunningUserIds();
                if (i != -1) {
                    if (!ArrayUtils.contains(runningUserIds, i)) {
                        return false;
                    }
                }
                long elapsedRealtime = this.mInjector.elapsedRealtime();
                for (int i2 : runningUserIds) {
                    if (i == -1 || i == i2) {
                        List installedPackagesAsUser = this.mPackageManager.getInstalledPackagesAsUser(512, i2);
                        int i3 = 0;
                        for (int size = installedPackagesAsUser.size(); i3 < size; size = size) {
                            PackageInfo packageInfo = (PackageInfo) installedPackagesAsUser.get(i3);
                            checkAndUpdateStandbyState(packageInfo.packageName, i2, packageInfo.applicationInfo.uid, elapsedRealtime);
                            i3++;
                        }
                    }
                }
                return true;
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:37:0x0095, code lost:
        r1 = r2.lastPredictedBucket;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x0099, code lost:
        if (r1 < 10) goto L83;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x009d, code lost:
        if (r1 > 40) goto L78;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x009f, code lost:
        r3 = 1281;
        r6 = r1;
     */
    /* JADX WARN: Removed duplicated region for block: B:50:0x00c5  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x00d4  */
    /* JADX WARN: Removed duplicated region for block: B:72:0x010a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void checkAndUpdateStandbyState(String str, int i, int i2, long j) {
        int packageUidAsUser;
        int minBucketWithValidExpiryTime;
        int i3;
        long j2;
        int i4;
        int i5;
        int i6;
        String str2;
        int i7;
        boolean z;
        boolean z2;
        boolean isIdle;
        if (i2 <= 0) {
            try {
                packageUidAsUser = this.mPackageManager.getPackageUidAsUser(str, i);
            } catch (PackageManager.NameNotFoundException unused) {
                return;
            }
        } else {
            packageUidAsUser = i2;
        }
        int appMinBucket = getAppMinBucket(str, UserHandle.getAppId(packageUidAsUser), i);
        if (appMinBucket <= 10) {
            synchronized (this.mAppIdleLock) {
                isIdle = this.mAppIdleHistory.isIdle(str, i, j);
                this.mAppIdleHistory.setAppStandbyBucket(str, i, j, appMinBucket, 256);
                z = this.mAppIdleHistory.isIdle(str, i, j);
            }
            maybeInformListeners(str, i, j, appMinBucket, 256, false);
            z2 = isIdle;
            i7 = i;
            str2 = str;
        } else {
            synchronized (this.mAppIdleLock) {
                boolean isIdle2 = this.mAppIdleHistory.isIdle(str, i, j);
                AppIdleHistory.AppUsageHistory appUsageHistory = this.mAppIdleHistory.getAppUsageHistory(str, i, j);
                int i8 = appUsageHistory.bucketingReason;
                int i9 = 65280 & i8;
                if (i9 == 1024) {
                    return;
                }
                int i10 = appUsageHistory.currentBucket;
                if (i10 == 50) {
                    return;
                }
                int max = Math.max(i10, 10);
                boolean predictionTimedOut = predictionTimedOut(appUsageHistory, j);
                if (i9 != 256 && i9 != 768 && i9 != 512 && !predictionTimedOut) {
                    int i11 = i8;
                    long elapsedTime = this.mAppIdleHistory.getElapsedTime(j);
                    minBucketWithValidExpiryTime = getMinBucketWithValidExpiryTime(appUsageHistory, max, elapsedTime);
                    if (minBucketWithValidExpiryTime == -1) {
                        if (minBucketWithValidExpiryTime != 10 && appUsageHistory.currentBucket != minBucketWithValidExpiryTime) {
                            i3 = 775;
                        }
                        i3 = appUsageHistory.bucketingReason;
                    } else {
                        minBucketWithValidExpiryTime = max;
                        i3 = i11;
                    }
                    j2 = appUsageHistory.lastUsedByUserElapsedTime;
                    int i12 = minBucketWithValidExpiryTime;
                    if (j2 >= 0 || appUsageHistory.lastRestrictAttemptElapsedTime <= j2 || elapsedTime - j2 < this.mInjector.getAutoRestrictedBucketDelayMs()) {
                        i4 = i3;
                        i5 = i12;
                    } else {
                        i4 = appUsageHistory.lastRestrictReason;
                        i5 = 45;
                    }
                    i6 = (i5 == 45 || this.mAllowRestrictedBucket) ? i5 : 40;
                    if (i6 > appMinBucket) {
                        i6 = appMinBucket;
                    }
                    if (i10 == i6 && !predictionTimedOut) {
                        str2 = str;
                        i7 = i;
                        z = isIdle2;
                        z2 = isIdle2;
                    }
                    this.mAppIdleHistory.setAppStandbyBucket(str, i, j, i6, i4);
                    str2 = str;
                    i7 = i;
                    boolean isIdle3 = this.mAppIdleHistory.isIdle(str2, i7, j);
                    maybeInformListeners(str, i, j, i6, i4, false);
                    z = isIdle3;
                    z2 = isIdle2;
                }
                if (i9 != 256 || (appUsageHistory.bucketingReason & 255) != 2) {
                    max = getBucketForLocked(str, i, j);
                    i8 = 512;
                }
                int i112 = i8;
                long elapsedTime2 = this.mAppIdleHistory.getElapsedTime(j);
                minBucketWithValidExpiryTime = getMinBucketWithValidExpiryTime(appUsageHistory, max, elapsedTime2);
                if (minBucketWithValidExpiryTime == -1) {
                }
                j2 = appUsageHistory.lastUsedByUserElapsedTime;
                int i122 = minBucketWithValidExpiryTime;
                if (j2 >= 0) {
                }
                i4 = i3;
                i5 = i122;
                if (i5 == 45) {
                }
                if (i6 > appMinBucket) {
                }
                if (i10 == i6) {
                    str2 = str;
                    i7 = i;
                    z = isIdle2;
                    z2 = isIdle2;
                }
                this.mAppIdleHistory.setAppStandbyBucket(str, i, j, i6, i4);
                str2 = str;
                i7 = i;
                boolean isIdle32 = this.mAppIdleHistory.isIdle(str2, i7, j);
                maybeInformListeners(str, i, j, i6, i4, false);
                z = isIdle32;
                z2 = isIdle2;
            }
        }
        if (z2 != z) {
            notifyBatteryStats(str2, i7, z);
        }
    }

    public final boolean predictionTimedOut(AppIdleHistory.AppUsageHistory appUsageHistory, long j) {
        return appUsageHistory.lastPredictedTime > 0 && this.mAppIdleHistory.getElapsedTime(j) - appUsageHistory.lastPredictedTime > this.mPredictionTimeoutMillis;
    }

    public final void maybeInformListeners(String str, int i, long j, int i2, int i3, boolean z) {
        synchronized (this.mAppIdleLock) {
            if (this.mAppIdleHistory.shouldInformListeners(str, i, j, i2)) {
                StandbyUpdateRecord obtain = StandbyUpdateRecord.obtain(str, i, i2, i3, z);
                AppStandbyHandler appStandbyHandler = this.mHandler;
                appStandbyHandler.sendMessage(appStandbyHandler.obtainMessage(3, obtain));
            }
        }
    }

    @GuardedBy({"mAppIdleLock"})
    public final int getBucketForLocked(String str, int i, long j) {
        int thresholdIndex = this.mAppIdleHistory.getThresholdIndex(str, i, j, this.mAppStandbyScreenThresholds, this.mAppStandbyElapsedThresholds);
        if (thresholdIndex >= 0) {
            return THRESHOLD_BUCKETS[thresholdIndex];
        }
        return 50;
    }

    public final void notifyBatteryStats(String str, int i, boolean z) {
        try {
            int packageUidAsUser = this.mPackageManager.getPackageUidAsUser(str, IInstalld.FLAG_FORCE, i);
            if (z) {
                this.mInjector.noteEvent(15, str, packageUidAsUser);
            } else {
                this.mInjector.noteEvent(16, str, packageUidAsUser);
            }
        } catch (PackageManager.NameNotFoundException | RemoteException unused) {
        }
    }

    @Override // android.app.usage.UsageStatsManagerInternal.UsageEventListener
    public void onUsageEvent(int i, UsageEvents.Event event) {
        if (this.mAppIdleEnabled) {
            int eventType = event.getEventType();
            if (eventType == 1 || eventType == 2 || eventType == 6 || eventType == 7 || eventType == 10 || eventType == 14 || eventType == 13 || eventType == 19) {
                String packageName = event.getPackageName();
                List<UserHandle> crossProfileTargets = getCrossProfileTargets(packageName, i);
                synchronized (this.mAppIdleLock) {
                    long elapsedRealtime = this.mInjector.elapsedRealtime();
                    reportEventLocked(packageName, eventType, elapsedRealtime, i);
                    int size = crossProfileTargets.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        reportEventLocked(packageName, eventType, elapsedRealtime, crossProfileTargets.get(i2).getIdentifier());
                    }
                }
            }
        }
    }

    @GuardedBy({"mAppIdleLock"})
    public final void reportEventLocked(String str, int i, long j, int i2) {
        int i3;
        int i4;
        long j2;
        AppIdleHistory.AppUsageHistory appUsageHistory;
        boolean z;
        int i5;
        String str2;
        int i6;
        long j3;
        boolean isIdle = this.mAppIdleHistory.isIdle(str, i2, j);
        AppIdleHistory.AppUsageHistory appUsageHistory2 = this.mAppIdleHistory.getAppUsageHistory(str, i2, j);
        int i7 = appUsageHistory2.currentBucket;
        int i8 = appUsageHistory2.bucketingReason;
        int usageEventToSubReason = usageEventToSubReason(i);
        int i9 = usageEventToSubReason | FrameworkStatsLog.APP_STANDBY_BUCKET_CHANGED__MAIN_REASON__MAIN_USAGE;
        if (i == 10) {
            if (!this.mRetainNotificationSeenImpactForPreTApps || getTargetSdkVersion(str) >= 33) {
                if (this.mTriggerQuotaBumpOnNotificationSeen) {
                    this.mHandler.obtainMessage(7, i2, -1, str).sendToTarget();
                }
                i6 = this.mNotificationSeenPromotedBucket;
                j3 = this.mNotificationSeenTimeoutMillis;
            } else {
                i6 = 20;
                j3 = 43200000;
            }
            long j4 = j3;
            i3 = i9;
            i4 = i8;
            this.mAppIdleHistory.reportUsage(appUsageHistory2, str, i2, i6, usageEventToSubReason, 0L, j + j4);
            j2 = j4;
        } else {
            i3 = i9;
            i4 = i8;
            if (i == 14) {
                this.mAppIdleHistory.reportUsage(appUsageHistory2, str, i2, 20, usageEventToSubReason, 0L, j + this.mSlicePinnedTimeoutMillis);
                j2 = this.mSlicePinnedTimeoutMillis;
            } else if (i == 6) {
                this.mAppIdleHistory.reportUsage(appUsageHistory2, str, i2, 10, usageEventToSubReason, 0L, j + this.mSystemInteractionTimeoutMillis);
                j2 = this.mSystemInteractionTimeoutMillis;
            } else if (i != 19) {
                this.mAppIdleHistory.reportUsage(appUsageHistory2, str, i2, 10, usageEventToSubReason, j, j + this.mStrongUsageTimeoutMillis);
                j2 = this.mStrongUsageTimeoutMillis;
            } else if (i7 != 50) {
                return;
            } else {
                this.mAppIdleHistory.reportUsage(appUsageHistory2, str, i2, 10, usageEventToSubReason, 0L, j + this.mInitialForegroundServiceStartTimeoutMillis);
                j2 = this.mInitialForegroundServiceStartTimeoutMillis;
            }
        }
        if (appUsageHistory2.currentBucket != i7) {
            AppStandbyHandler appStandbyHandler = this.mHandler;
            appStandbyHandler.sendMessageDelayed(appStandbyHandler.obtainMessage(11, i2, -1, str), j2);
            int i10 = appUsageHistory2.currentBucket;
            boolean z2 = i10 == 10 && (i4 & 65280) != 768;
            appUsageHistory = appUsageHistory2;
            z = isIdle;
            i5 = i2;
            str2 = str;
            maybeInformListeners(str, i2, j, i10, i3, z2);
        } else {
            appUsageHistory = appUsageHistory2;
            z = isIdle;
            i5 = i2;
            str2 = str;
        }
        boolean z3 = appUsageHistory.currentBucket >= 40;
        if (z != z3) {
            notifyBatteryStats(str2, i5, z3);
        }
    }

    public final int getTargetSdkVersion(String str) {
        return this.mInjector.getPackageManagerInternal().getPackageTargetSdkVersion(str);
    }

    public final int getMinBucketWithValidExpiryTime(AppIdleHistory.AppUsageHistory appUsageHistory, int i, long j) {
        SparseLongArray sparseLongArray = appUsageHistory.bucketExpiryTimesMs;
        if (sparseLongArray == null) {
            return -1;
        }
        int size = sparseLongArray.size();
        for (int i2 = 0; i2 < size; i2++) {
            int keyAt = appUsageHistory.bucketExpiryTimesMs.keyAt(i2);
            if (i <= keyAt) {
                break;
            } else if (appUsageHistory.bucketExpiryTimesMs.valueAt(i2) > j) {
                return keyAt;
            }
        }
        return -1;
    }

    public final List<UserHandle> getCrossProfileTargets(String str, int i) {
        synchronized (this.mAppIdleLock) {
            if (this.mLinkCrossProfileApps) {
                return this.mInjector.getValidCrossProfileTargets(str, i);
            }
            return Collections.emptyList();
        }
    }

    @VisibleForTesting
    public void forceIdleState(String str, int i, boolean z) {
        int appId;
        int idle;
        if (this.mAppIdleEnabled && (appId = getAppId(str)) >= 0) {
            int appMinBucket = getAppMinBucket(str, appId, i);
            if (z && appMinBucket < 40) {
                Slog.e("AppStandbyController", "Tried to force an app to be idle when its min bucket is " + UsageStatsManager.standbyBucketToString(appMinBucket));
                return;
            }
            long elapsedRealtime = this.mInjector.elapsedRealtime();
            boolean isAppIdleFiltered = isAppIdleFiltered(str, appId, i, elapsedRealtime);
            synchronized (this.mAppIdleLock) {
                idle = this.mAppIdleHistory.setIdle(str, i, z, elapsedRealtime);
            }
            boolean isAppIdleFiltered2 = isAppIdleFiltered(str, appId, i, elapsedRealtime);
            maybeInformListeners(str, i, elapsedRealtime, idle, 1024, false);
            if (isAppIdleFiltered != isAppIdleFiltered2) {
                notifyBatteryStats(str, i, isAppIdleFiltered2);
            }
        }
    }

    public void setLastJobRunTime(String str, int i, long j) {
        synchronized (this.mAppIdleLock) {
            this.mAppIdleHistory.setLastJobRunTime(str, i, j);
        }
    }

    public long getTimeSinceLastJobRun(String str, int i) {
        long timeSinceLastJobRun;
        long elapsedRealtime = this.mInjector.elapsedRealtime();
        synchronized (this.mAppIdleLock) {
            timeSinceLastJobRun = this.mAppIdleHistory.getTimeSinceLastJobRun(str, i, elapsedRealtime);
        }
        return timeSinceLastJobRun;
    }

    public void setEstimatedLaunchTime(String str, int i, long j) {
        long elapsedRealtime = this.mInjector.elapsedRealtime();
        synchronized (this.mAppIdleLock) {
            this.mAppIdleHistory.setEstimatedLaunchTime(str, i, elapsedRealtime, j);
        }
    }

    public long getEstimatedLaunchTime(String str, int i) {
        long estimatedLaunchTime;
        long elapsedRealtime = this.mInjector.elapsedRealtime();
        synchronized (this.mAppIdleLock) {
            estimatedLaunchTime = this.mAppIdleHistory.getEstimatedLaunchTime(str, i, elapsedRealtime);
        }
        return estimatedLaunchTime;
    }

    public long getTimeSinceLastUsedByUser(String str, int i) {
        long timeSinceLastUsedByUser;
        long elapsedRealtime = this.mInjector.elapsedRealtime();
        synchronized (this.mAppIdleLock) {
            timeSinceLastUsedByUser = this.mAppIdleHistory.getTimeSinceLastUsedByUser(str, i, elapsedRealtime);
        }
        return timeSinceLastUsedByUser;
    }

    public void onUserRemoved(int i) {
        synchronized (this.mAppIdleLock) {
            this.mAppIdleHistory.onUserRemoved(i);
            synchronized (this.mActiveAdminApps) {
                this.mActiveAdminApps.remove(i);
            }
        }
    }

    public final boolean isAppIdleUnfiltered(String str, int i, long j) {
        boolean isIdle;
        synchronized (this.mAppIdleLock) {
            isIdle = this.mAppIdleHistory.isIdle(str, i, j);
        }
        return isIdle;
    }

    public void addListener(AppStandbyInternal.AppIdleStateChangeListener appIdleStateChangeListener) {
        synchronized (this.mPackageAccessListeners) {
            if (!this.mPackageAccessListeners.contains(appIdleStateChangeListener)) {
                this.mPackageAccessListeners.add(appIdleStateChangeListener);
            }
        }
    }

    public void removeListener(AppStandbyInternal.AppIdleStateChangeListener appIdleStateChangeListener) {
        synchronized (this.mPackageAccessListeners) {
            this.mPackageAccessListeners.remove(appIdleStateChangeListener);
        }
    }

    public int getAppId(String str) {
        try {
            return this.mPackageManager.getApplicationInfo(str, 4194816).uid;
        } catch (PackageManager.NameNotFoundException unused) {
            return -1;
        }
    }

    public boolean isAppIdleFiltered(String str, int i, long j, boolean z) {
        if (z && this.mInjector.isPackageEphemeral(i, str)) {
            return false;
        }
        return isAppIdleFiltered(str, getAppId(str), i, j);
    }

    public final int getAppMinBucket(String str, int i) {
        try {
            return getAppMinBucket(str, UserHandle.getAppId(this.mPackageManager.getPackageUidAsUser(str, i)), i);
        } catch (PackageManager.NameNotFoundException unused) {
            return 50;
        }
    }

    public final int getAppMinBucket(String str, int i, int i2) {
        if (str == null) {
            return 50;
        }
        if (this.mAppIdleEnabled && i >= 10000 && !str.equals(PackageManagerShellCommandDataLoader.PACKAGE)) {
            if (this.mSystemServicesReady) {
                if (this.mInjector.isNonIdleWhitelisted(str) || isActiveDeviceAdmin(str, i2) || isActiveNetworkScorer(str)) {
                    return 5;
                }
                int uid = UserHandle.getUid(i2, i);
                synchronized (this.mSystemExemptionAppOpMode) {
                    if (this.mSystemExemptionAppOpMode.indexOfKey(uid) >= 0) {
                        if (this.mSystemExemptionAppOpMode.get(uid) == 0) {
                            return 5;
                        }
                    } else {
                        int checkOpNoThrow = this.mAppOpsManager.checkOpNoThrow(128, uid, str);
                        this.mSystemExemptionAppOpMode.put(uid, checkOpNoThrow);
                        if (checkOpNoThrow == 0) {
                            return 5;
                        }
                    }
                    AppWidgetManager appWidgetManager = this.mAppWidgetManager;
                    if (appWidgetManager != null && this.mInjector.isBoundWidgetPackage(appWidgetManager, str, i2)) {
                        return 10;
                    }
                    if (isDeviceProvisioningPackage(str)) {
                        return 5;
                    }
                    if (this.mInjector.isWellbeingPackage(str) || this.mInjector.shouldGetExactAlarmBucketElevation(str, UserHandle.getUid(i2, i))) {
                        return 20;
                    }
                }
            }
            if (isCarrierApp(str)) {
                return 5;
            }
            if (isHeadlessSystemApp(str)) {
                return 10;
            }
            return this.mPackageManager.checkPermission("android.permission.ACCESS_BACKGROUND_LOCATION", str) == 0 ? 30 : 50;
        }
        return 5;
    }

    public final boolean isHeadlessSystemApp(String str) {
        boolean contains;
        synchronized (this.mHeadlessSystemApps) {
            contains = this.mHeadlessSystemApps.contains(str);
        }
        return contains;
    }

    public boolean isAppIdleFiltered(String str, int i, int i2, long j) {
        return this.mAppIdleEnabled && !this.mIsCharging && isAppIdleUnfiltered(str, i2, j) && getAppMinBucket(str, i, i2) >= 40;
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x007d  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0083  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int[] getIdleUidsForUser(int i) {
        int i2;
        int i3;
        ApplicationInfo applicationInfo;
        boolean z;
        if (!this.mAppIdleEnabled) {
            return EmptyArray.INT;
        }
        Trace.traceBegin(64L, "getIdleUidsForUser");
        long elapsedRealtime = this.mInjector.elapsedRealtime();
        List<ApplicationInfo> installedApplications = this.mInjector.getPackageManagerInternal().getInstalledApplications(0L, i, Process.myUid());
        if (installedApplications == null) {
            return EmptyArray.INT;
        }
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
        int size = installedApplications.size() - 1;
        int i4 = 0;
        while (size >= 0) {
            ApplicationInfo applicationInfo2 = installedApplications.get(size);
            int indexOfKey = sparseBooleanArray.indexOfKey(applicationInfo2.uid);
            boolean valueAt = indexOfKey < 0 ? true : sparseBooleanArray.valueAt(indexOfKey);
            if (valueAt) {
                i2 = indexOfKey;
                i3 = size;
                applicationInfo = applicationInfo2;
                if (isAppIdleFiltered(applicationInfo2.packageName, UserHandle.getAppId(applicationInfo2.uid), i, elapsedRealtime)) {
                    z = true;
                    if (valueAt && !z) {
                        i4++;
                    }
                    if (i2 >= 0) {
                        sparseBooleanArray.put(applicationInfo.uid, z);
                    } else {
                        sparseBooleanArray.setValueAt(i2, z);
                    }
                    size = i3 - 1;
                }
            } else {
                i2 = indexOfKey;
                i3 = size;
                applicationInfo = applicationInfo2;
            }
            z = false;
            if (valueAt) {
                i4++;
            }
            if (i2 >= 0) {
            }
            size = i3 - 1;
        }
        int size2 = sparseBooleanArray.size() - i4;
        int[] iArr = new int[size2];
        for (int size3 = sparseBooleanArray.size() - 1; size3 >= 0; size3--) {
            if (sparseBooleanArray.valueAt(size3)) {
                size2--;
                iArr[size2] = sparseBooleanArray.keyAt(size3);
            }
        }
        Trace.traceEnd(64L);
        return iArr;
    }

    public void setAppIdleAsync(String str, boolean z, int i) {
        if (str == null || !this.mAppIdleEnabled) {
            return;
        }
        this.mHandler.obtainMessage(4, i, z ? 1 : 0, str).sendToTarget();
    }

    public int getAppStandbyBucket(String str, int i, long j, boolean z) {
        int appStandbyBucket;
        if (this.mAppIdleEnabled) {
            if (z && this.mInjector.isPackageEphemeral(i, str)) {
                return 10;
            }
            synchronized (this.mAppIdleLock) {
                appStandbyBucket = this.mAppIdleHistory.getAppStandbyBucket(str, i, j);
            }
            return appStandbyBucket;
        }
        return 5;
    }

    public int getAppStandbyBucketReason(String str, int i, long j) {
        int appStandbyReason;
        synchronized (this.mAppIdleLock) {
            appStandbyReason = this.mAppIdleHistory.getAppStandbyReason(str, i, j);
        }
        return appStandbyReason;
    }

    public List<AppStandbyInfo> getAppStandbyBuckets(int i) {
        ArrayList<AppStandbyInfo> appStandbyBuckets;
        synchronized (this.mAppIdleLock) {
            appStandbyBuckets = this.mAppIdleHistory.getAppStandbyBuckets(i, this.mAppIdleEnabled);
        }
        return appStandbyBuckets;
    }

    public int getAppMinStandbyBucket(String str, int i, int i2, boolean z) {
        int appMinBucket;
        if (z && this.mInjector.isPackageEphemeral(i2, str)) {
            return 50;
        }
        synchronized (this.mAppIdleLock) {
            appMinBucket = getAppMinBucket(str, i, i2);
        }
        return appMinBucket;
    }

    public void restrictApp(String str, int i, int i2) {
        restrictApp(str, i, FrameworkStatsLog.APP_STANDBY_BUCKET_CHANGED__MAIN_REASON__MAIN_FORCED_BY_SYSTEM, i2);
    }

    public void restrictApp(String str, int i, int i2, int i3) {
        if (i2 != 1536 && i2 != 1024) {
            Slog.e("AppStandbyController", "Tried to restrict app " + str + " for an unsupported reason");
        } else if (!this.mInjector.isPackageInstalled(str, 0, i)) {
            Slog.e("AppStandbyController", "Tried to restrict uninstalled app: " + str);
        } else {
            setAppStandbyBucket(str, i, this.mAllowRestrictedBucket ? 45 : 40, (i2 & 65280) | (i3 & 255), this.mInjector.elapsedRealtime(), false);
        }
    }

    public void restoreAppsToRare(Set<String> set, final int i) {
        long elapsedRealtime = this.mInjector.elapsedRealtime();
        for (String str : set) {
            if (!this.mInjector.isPackageInstalled(str, 0, i)) {
                Slog.i("AppStandbyController", "Tried to restore bucket for uninstalled app: " + str);
                this.mAppsToRestoreToRare.add(i, str);
            } else {
                restoreAppToRare(str, i, elapsedRealtime, 258);
            }
        }
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.usage.AppStandbyController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                AppStandbyController.this.lambda$restoreAppsToRare$0(i);
            }
        }, 28800000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$restoreAppsToRare$0(int i) {
        this.mAppsToRestoreToRare.remove(i);
    }

    public final void restoreAppToRare(String str, int i, long j, int i2) {
        if (getAppStandbyBucket(str, i, j, false) == 50) {
            setAppStandbyBucket(str, i, 40, i2, j, false);
        }
    }

    public void setAppStandbyBucket(String str, int i, int i2, int i3, int i4) {
        setAppStandbyBuckets(Collections.singletonList(new AppStandbyInfo(str, i)), i2, i3, i4);
    }

    public void setAppStandbyBuckets(List<AppStandbyInfo> list, int i, int i2, int i3) {
        int i4;
        int handleIncomingUser = ActivityManager.handleIncomingUser(i3, i2, i, false, true, "setAppStandbyBucket", null);
        boolean z = i2 == 0 || i2 == 2000;
        if ((!UserHandle.isSameApp(i2, 1000) || i3 == Process.myPid()) && !z) {
            i4 = UserHandle.isCore(i2) ? FrameworkStatsLog.APP_STANDBY_BUCKET_CHANGED__MAIN_REASON__MAIN_FORCED_BY_SYSTEM : 1280;
        } else {
            i4 = 1024;
        }
        int i5 = i4;
        int size = list.size();
        long elapsedRealtime = this.mInjector.elapsedRealtime();
        for (int i6 = 0; i6 < size; i6++) {
            AppStandbyInfo appStandbyInfo = list.get(i6);
            String str = appStandbyInfo.mPackageName;
            int i7 = appStandbyInfo.mStandbyBucket;
            if (i7 < 10 || i7 > 50) {
                throw new IllegalArgumentException("Cannot set the standby bucket to " + i7);
            }
            int packageUid = this.mInjector.getPackageManagerInternal().getPackageUid(str, 4980736L, handleIncomingUser);
            if (packageUid == i2) {
                throw new IllegalArgumentException("Cannot set your own standby bucket");
            }
            if (packageUid < 0) {
                throw new IllegalArgumentException("Cannot set standby bucket for non existent package (" + str + ")");
            }
            setAppStandbyBucket(str, handleIncomingUser, i7, i5, elapsedRealtime, z);
        }
    }

    @VisibleForTesting
    public void setAppStandbyBucket(String str, int i, int i2, int i3) {
        setAppStandbyBucket(str, i, i2, i3, this.mInjector.elapsedRealtime(), false);
    }

    /* JADX WARN: Removed duplicated region for block: B:123:0x01e5  */
    /* JADX WARN: Removed duplicated region for block: B:124:0x01e8  */
    /* JADX WARN: Removed duplicated region for block: B:127:0x01ff  */
    /* JADX WARN: Removed duplicated region for block: B:128:0x0202  */
    /* JADX WARN: Removed duplicated region for block: B:130:0x0206 A[Catch: all -> 0x021a, TryCatch #0 {, blocks: (B:7:0x0012, B:9:0x001b, B:10:0x0031, B:14:0x0039, B:18:0x0041, B:22:0x0057, B:24:0x005d, B:32:0x0069, B:38:0x0079, B:43:0x0083, B:53:0x0095, B:55:0x00a7, B:59:0x00bc, B:64:0x00d4, B:65:0x00d7, B:73:0x00e7, B:78:0x00f0, B:80:0x00f2, B:83:0x00fa, B:87:0x0101, B:89:0x0115, B:91:0x0119, B:93:0x011e, B:102:0x019c, B:106:0x01b3, B:121:0x01d7, B:125:0x01e9, B:130:0x0206, B:131:0x0209, B:110:0x01bb, B:114:0x01c4, B:116:0x01c8, B:94:0x0140, B:95:0x0158, B:97:0x0169, B:98:0x0192, B:30:0x0067), top: B:137:0x0012 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void setAppStandbyBucket(String str, int i, int i2, int i3, long j, boolean z) {
        int i4;
        int i5;
        boolean z2;
        int i6;
        int i7;
        int i8;
        boolean z3;
        boolean z4;
        int i9;
        AppIdleHistory.AppUsageHistory appUsageHistory;
        int i10;
        if (this.mAppIdleEnabled) {
            synchronized (this.mAppIdleLock) {
                if (!this.mInjector.isPackageInstalled(str, 0, i)) {
                    Slog.e("AppStandbyController", "Tried to set bucket of uninstalled app: " + str);
                    return;
                }
                int i11 = (i2 != 45 || this.mAllowRestrictedBucket) ? i2 : 40;
                AppIdleHistory.AppUsageHistory appUsageHistory2 = this.mAppIdleHistory.getAppUsageHistory(str, i, j);
                int i12 = i3 & 65280;
                boolean z5 = i12 == 1280;
                int i13 = appUsageHistory2.currentBucket;
                if (i13 < 10) {
                    return;
                }
                if ((i13 == 50 || i11 == 50) && z5) {
                    return;
                }
                int i14 = appUsageHistory2.bucketingReason;
                int i15 = i14 & 65280;
                int i16 = FrameworkStatsLog.APP_STANDBY_BUCKET_CHANGED__MAIN_REASON__MAIN_FORCED_BY_SYSTEM;
                boolean z6 = i15 == 1536;
                if (z5) {
                    if ((i14 & 65280) != 1024 && !z6) {
                        i16 = FrameworkStatsLog.APP_STANDBY_BUCKET_CHANGED__MAIN_REASON__MAIN_FORCED_BY_SYSTEM;
                    }
                    return;
                }
                boolean z7 = i12 == i16;
                if (i13 == i11 && z6 && z7) {
                    if (i11 == 45) {
                        appUsageHistory = appUsageHistory2;
                        i10 = i11;
                        this.mAppIdleHistory.noteRestrictionAttempt(str, i, j, i3);
                    } else {
                        appUsageHistory = appUsageHistory2;
                        i10 = i11;
                    }
                    int i17 = (appUsageHistory.bucketingReason & 255) | FrameworkStatsLog.APP_STANDBY_BUCKET_CHANGED__MAIN_REASON__MAIN_FORCED_BY_SYSTEM | (i3 & 255);
                    boolean z8 = appUsageHistory.currentBucket >= 40;
                    this.mAppIdleHistory.setAppStandbyBucket(str, i, j, i10, i17, z);
                    boolean z9 = i10 >= 40;
                    if (z8 != z9) {
                        notifyBatteryStats(str, i, z9);
                    }
                    return;
                }
                int i18 = i11;
                boolean z10 = i12 == 1024;
                if (i13 == 45) {
                    if ((65280 & i14) == 512) {
                        if (z5 && i18 >= 40) {
                            return;
                        }
                    } else if (!isUserUsage(i3) && !z10) {
                        return;
                    }
                }
                if (i18 == 45) {
                    i5 = i18;
                    i6 = -1;
                    i4 = 512;
                    this.mAppIdleHistory.noteRestrictionAttempt(str, i, j, i3);
                    if (z10) {
                        if (Build.IS_DEBUGGABLE && (i3 & 255) != 2) {
                            z2 = false;
                            Toast.makeText(this.mContext, this.mHandler.getLooper(), this.mContext.getResources().getString(17039705, str), 0).show();
                        } else {
                            z2 = false;
                            Slog.i("AppStandbyController", str + " restricted by user");
                        }
                    } else {
                        z2 = false;
                        long autoRestrictedBucketDelayMs = (appUsageHistory2.lastUsedByUserElapsedTime + this.mInjector.getAutoRestrictedBucketDelayMs()) - j;
                        if (autoRestrictedBucketDelayMs > 0) {
                            Slog.w("AppStandbyController", "Tried to restrict recently used app: " + str + " due to " + i3);
                            AppStandbyHandler appStandbyHandler = this.mHandler;
                            appStandbyHandler.sendMessageDelayed(appStandbyHandler.obtainMessage(11, i, -1, str), autoRestrictedBucketDelayMs);
                            return;
                        }
                    }
                } else {
                    i4 = 512;
                    i5 = i18;
                    z2 = false;
                    i6 = -1;
                }
                if (z5) {
                    long elapsedTime = this.mAppIdleHistory.getElapsedTime(j);
                    i7 = i5;
                    this.mAppIdleHistory.updateLastPrediction(appUsageHistory2, elapsedTime, i7);
                    i8 = getMinBucketWithValidExpiryTime(appUsageHistory2, i7, elapsedTime);
                    if (i8 != i6) {
                        if (i8 != 10 && appUsageHistory2.currentBucket != i8) {
                            i9 = 775;
                            i4 = i9;
                        }
                        i9 = appUsageHistory2.bucketingReason;
                        i4 = i9;
                    } else if (i7 == 40 && this.mAllowRestrictedBucket && getBucketForLocked(str, i, j) == 45) {
                        i8 = 45;
                    }
                    int min = Math.min(i8, getAppMinBucket(str, i));
                    z3 = appUsageHistory2.currentBucket < 40 ? true : z2;
                    boolean z11 = z2;
                    this.mAppIdleHistory.setAppStandbyBucket(str, i, j, min, i4, z);
                    z4 = min < 40 ? true : z11;
                    if (z3 != z4) {
                        notifyBatteryStats(str, i, z4);
                    }
                    maybeInformListeners(str, i, j, min, i4, false);
                }
                i7 = i5;
                i8 = i7;
                i4 = i3;
                int min2 = Math.min(i8, getAppMinBucket(str, i));
                if (appUsageHistory2.currentBucket < 40) {
                }
                boolean z112 = z2;
                this.mAppIdleHistory.setAppStandbyBucket(str, i, j, min2, i4, z);
                if (min2 < 40) {
                }
                if (z3 != z4) {
                }
                maybeInformListeners(str, i, j, min2, i4, false);
            }
        }
    }

    @VisibleForTesting
    public boolean isActiveDeviceAdmin(String str, int i) {
        boolean z;
        synchronized (this.mActiveAdminApps) {
            Set<String> set = this.mActiveAdminApps.get(i);
            z = set != null && set.contains(str);
        }
        return z;
    }

    public void addActiveDeviceAdmin(String str, int i) {
        synchronized (this.mActiveAdminApps) {
            Set<String> set = this.mActiveAdminApps.get(i);
            if (set == null) {
                set = new ArraySet<>();
                this.mActiveAdminApps.put(i, set);
            }
            set.add(str);
        }
    }

    public void setActiveAdminApps(Set<String> set, int i) {
        synchronized (this.mActiveAdminApps) {
            if (set == null) {
                this.mActiveAdminApps.remove(i);
            } else {
                this.mActiveAdminApps.put(i, set);
            }
        }
    }

    public void onAdminDataAvailable() {
        this.mAdminDataAvailableLatch.countDown();
    }

    public final void waitForAdminData() {
        if (this.mContext.getPackageManager().hasSystemFeature("android.software.device_admin")) {
            ConcurrentUtils.waitForCountDownNoInterrupt(this.mAdminDataAvailableLatch, 10000L, "Wait for admin data");
        }
    }

    @VisibleForTesting
    public Set<String> getActiveAdminAppsForTest(int i) {
        Set<String> set;
        synchronized (this.mActiveAdminApps) {
            set = this.mActiveAdminApps.get(i);
        }
        return set;
    }

    public final boolean isDeviceProvisioningPackage(String str) {
        if (this.mCachedDeviceProvisioningPackage == null) {
            this.mCachedDeviceProvisioningPackage = this.mContext.getResources().getString(17039914);
        }
        return this.mCachedDeviceProvisioningPackage.equals(str);
    }

    public final boolean isCarrierApp(String str) {
        synchronized (this.mCarrierPrivilegedLock) {
            if (!this.mHaveCarrierPrivilegedApps) {
                fetchCarrierPrivilegedAppsCPL();
            }
            List<String> list = this.mCarrierPrivilegedApps;
            if (list != null) {
                return list.contains(str);
            }
            return false;
        }
    }

    public void clearCarrierPrivilegedApps() {
        synchronized (this.mCarrierPrivilegedLock) {
            this.mHaveCarrierPrivilegedApps = false;
            this.mCarrierPrivilegedApps = null;
        }
    }

    @GuardedBy({"mCarrierPrivilegedLock"})
    public final void fetchCarrierPrivilegedAppsCPL() {
        this.mCarrierPrivilegedApps = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).getCarrierPrivilegedPackagesForAllActiveSubscriptions();
        this.mHaveCarrierPrivilegedApps = true;
    }

    public final boolean isActiveNetworkScorer(String str) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        if (this.mCachedNetworkScorer == null || this.mCachedNetworkScorerAtMillis < elapsedRealtime - 5000) {
            this.mCachedNetworkScorer = this.mInjector.getActiveNetworkScorer();
            this.mCachedNetworkScorerAtMillis = elapsedRealtime;
        }
        return str.equals(this.mCachedNetworkScorer);
    }

    public final void informListeners(String str, int i, int i2, int i3, boolean z) {
        boolean z2 = i2 >= 40;
        synchronized (this.mPackageAccessListeners) {
            Iterator<AppStandbyInternal.AppIdleStateChangeListener> it = this.mPackageAccessListeners.iterator();
            while (it.hasNext()) {
                AppStandbyInternal.AppIdleStateChangeListener next = it.next();
                next.onAppIdleStateChanged(str, i, z2, i2, i3);
                if (z) {
                    next.onUserInteractionStarted(str, i);
                }
            }
        }
    }

    public final void informParoleStateChanged() {
        boolean isInParole = isInParole();
        synchronized (this.mPackageAccessListeners) {
            Iterator<AppStandbyInternal.AppIdleStateChangeListener> it = this.mPackageAccessListeners.iterator();
            while (it.hasNext()) {
                it.next().onParoleStateChanged(isInParole);
            }
        }
    }

    public long getBroadcastResponseWindowDurationMs() {
        return this.mBroadcastResponseWindowDurationMillis;
    }

    public int getBroadcastResponseFgThresholdState() {
        return this.mBroadcastResponseFgThresholdState;
    }

    public long getBroadcastSessionsDurationMs() {
        return this.mBroadcastSessionsDurationMs;
    }

    public long getBroadcastSessionsWithResponseDurationMs() {
        return this.mBroadcastSessionsWithResponseDurationMs;
    }

    public boolean shouldNoteResponseEventForAllBroadcastSessions() {
        return this.mNoteResponseEventForAllBroadcastSessions;
    }

    public List<String> getBroadcastResponseExemptedRoles() {
        return this.mBroadcastResponseExemptedRolesList;
    }

    public List<String> getBroadcastResponseExemptedPermissions() {
        return this.mBroadcastResponseExemptedPermissionsList;
    }

    public String getAppStandbyConstant(String str) {
        return this.mAppStandbyProperties.get(str);
    }

    public void clearLastUsedTimestampsForTest(String str, int i) {
        synchronized (this.mAppIdleLock) {
            this.mAppIdleHistory.clearLastUsedTimestamps(str, i);
        }
    }

    public void flushToDisk() {
        synchronized (this.mAppIdleLock) {
            this.mAppIdleHistory.writeAppIdleTimes(this.mInjector.elapsedRealtime());
            this.mAppIdleHistory.writeAppIdleDurations();
        }
    }

    public final boolean isDisplayOn() {
        return this.mInjector.isDefaultDisplayOn();
    }

    @VisibleForTesting
    public void clearAppIdleForPackage(String str, int i) {
        synchronized (this.mAppIdleLock) {
            this.mAppIdleHistory.clearUsage(str, i);
        }
    }

    @VisibleForTesting
    public void maybeUnrestrictBuggyApp(String str, int i) {
        maybeUnrestrictApp(str, i, FrameworkStatsLog.APP_STANDBY_BUCKET_CHANGED__MAIN_REASON__MAIN_FORCED_BY_SYSTEM, 4, 256, 1);
    }

    public void maybeUnrestrictApp(String str, int i, int i2, int i3, int i4, int i5) {
        int i6;
        synchronized (this.mAppIdleLock) {
            long elapsedRealtime = this.mInjector.elapsedRealtime();
            AppIdleHistory.AppUsageHistory appUsageHistory = this.mAppIdleHistory.getAppUsageHistory(str, i, elapsedRealtime);
            int i7 = 45;
            if (appUsageHistory.currentBucket == 45) {
                int i8 = appUsageHistory.bucketingReason;
                if ((65280 & i8) == i2) {
                    if ((i8 & 255) == i3) {
                        i6 = i4 | i5;
                        i7 = 40;
                    } else {
                        i6 = (~i3) & i8;
                    }
                    this.mAppIdleHistory.setAppStandbyBucket(str, i, elapsedRealtime, i7, i6);
                    maybeInformListeners(str, i, elapsedRealtime, i7, i6, false);
                }
            }
        }
    }

    public final void updatePowerWhitelistCache() {
        if (this.mInjector.getBootPhase() < 500) {
            return;
        }
        this.mInjector.updatePowerWhitelistCache();
        postCheckIdleStates(-1);
    }

    /* loaded from: classes2.dex */
    public class PackageReceiver extends BroadcastReceiver {
        public PackageReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
            int sendingUserId = getSendingUserId();
            if ("android.intent.action.PACKAGE_ADDED".equals(action) || "android.intent.action.PACKAGE_CHANGED".equals(action)) {
                String[] stringArrayExtra = intent.getStringArrayExtra("android.intent.extra.changed_component_name_list");
                if (stringArrayExtra == null || (stringArrayExtra.length == 1 && schemeSpecificPart.equals(stringArrayExtra[0]))) {
                    AppStandbyController.this.clearCarrierPrivilegedApps();
                    AppStandbyController.this.evaluateSystemAppException(schemeSpecificPart, sendingUserId);
                }
                if ("android.intent.action.PACKAGE_CHANGED".equals(action)) {
                    AppStandbyController.this.mHandler.obtainMessage(11, sendingUserId, -1, schemeSpecificPart).sendToTarget();
                }
            }
            if ("android.intent.action.PACKAGE_REMOVED".equals(action) || "android.intent.action.PACKAGE_ADDED".equals(action)) {
                if (intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                    AppStandbyController.this.maybeUnrestrictBuggyApp(schemeSpecificPart, sendingUserId);
                } else if (!"android.intent.action.PACKAGE_ADDED".equals(action)) {
                    AppStandbyController.this.clearAppIdleForPackage(schemeSpecificPart, sendingUserId);
                } else if (AppStandbyController.this.mAppsToRestoreToRare.contains(sendingUserId, schemeSpecificPart)) {
                    AppStandbyController appStandbyController = AppStandbyController.this;
                    appStandbyController.restoreAppToRare(schemeSpecificPart, sendingUserId, appStandbyController.mInjector.elapsedRealtime(), 258);
                    AppStandbyController.this.mAppsToRestoreToRare.remove(sendingUserId, schemeSpecificPart);
                }
            }
            synchronized (AppStandbyController.this.mSystemExemptionAppOpMode) {
                if ("android.intent.action.PACKAGE_REMOVED".equals(action)) {
                    AppStandbyController.this.mSystemExemptionAppOpMode.delete(UserHandle.getUid(sendingUserId, AppStandbyController.this.getAppId(schemeSpecificPart)));
                }
            }
        }
    }

    public final void evaluateSystemAppException(String str, int i) {
        if (this.mSystemServicesReady) {
            try {
                maybeUpdateHeadlessSystemAppCache(this.mPackageManager.getPackageInfoAsUser(str, 1835520, i));
            } catch (PackageManager.NameNotFoundException unused) {
                synchronized (this.mHeadlessSystemApps) {
                    this.mHeadlessSystemApps.remove(str);
                }
            }
        }
    }

    public final boolean maybeUpdateHeadlessSystemAppCache(PackageInfo packageInfo) {
        ApplicationInfo applicationInfo;
        if (packageInfo == null || (applicationInfo = packageInfo.applicationInfo) == null || !(applicationInfo.isSystemApp() || packageInfo.applicationInfo.isUpdatedSystemApp())) {
            return false;
        }
        return updateHeadlessSystemAppCache(packageInfo.packageName, ArrayUtils.isEmpty(this.mPackageManager.queryIntentActivitiesAsUser(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER").setPackage(packageInfo.packageName), 1835520, 0)));
    }

    public final boolean updateHeadlessSystemAppCache(String str, boolean z) {
        synchronized (this.mHeadlessSystemApps) {
            if (z) {
                return this.mHeadlessSystemApps.add(str);
            }
            return this.mHeadlessSystemApps.remove(str);
        }
    }

    public void initializeDefaultsForSystemApps(int i) {
        int i2;
        if (!this.mSystemServicesReady) {
            this.mPendingInitializeDefaults = true;
            return;
        }
        Slog.d("AppStandbyController", "Initializing defaults for system apps on user " + i + ", appIdleEnabled=" + this.mAppIdleEnabled);
        long elapsedRealtime = this.mInjector.elapsedRealtime();
        List installedPackagesAsUser = this.mPackageManager.getInstalledPackagesAsUser(512, i);
        int size = installedPackagesAsUser.size();
        synchronized (this.mAppIdleLock) {
            int i3 = 0;
            while (i3 < size) {
                PackageInfo packageInfo = (PackageInfo) installedPackagesAsUser.get(i3);
                String str = packageInfo.packageName;
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                if (applicationInfo == null || !applicationInfo.isSystemApp()) {
                    i2 = i3;
                } else {
                    i2 = i3;
                    this.mAppIdleHistory.reportUsage(str, i, 10, 6, 0L, elapsedRealtime + this.mSystemUpdateUsageTimeoutMillis);
                }
                i3 = i2 + 1;
            }
            this.mAppIdleHistory.writeAppIdleTimes(i, elapsedRealtime);
        }
    }

    public final Set<String> getSystemPackagesWithLauncherActivities() {
        List<ResolveInfo> queryIntentActivitiesAsUser = this.mPackageManager.queryIntentActivitiesAsUser(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER"), 1835520, 0);
        ArraySet arraySet = new ArraySet();
        for (ResolveInfo resolveInfo : queryIntentActivitiesAsUser) {
            arraySet.add(resolveInfo.activityInfo.packageName);
        }
        return arraySet;
    }

    public final void loadHeadlessSystemAppCache() {
        long uptimeMillis = SystemClock.uptimeMillis();
        List installedPackagesAsUser = this.mPackageManager.getInstalledPackagesAsUser(1835520, 0);
        Set<String> systemPackagesWithLauncherActivities = getSystemPackagesWithLauncherActivities();
        int size = installedPackagesAsUser.size();
        for (int i = 0; i < size; i++) {
            PackageInfo packageInfo = (PackageInfo) installedPackagesAsUser.get(i);
            if (packageInfo != null) {
                String str = packageInfo.packageName;
                if (updateHeadlessSystemAppCache(str, !systemPackagesWithLauncherActivities.contains(str))) {
                    this.mHandler.obtainMessage(11, 0, -1, str).sendToTarget();
                }
            }
        }
        long uptimeMillis2 = SystemClock.uptimeMillis();
        Slog.d("AppStandbyController", "Loaded headless system app cache in " + (uptimeMillis2 - uptimeMillis) + " ms: appIdleEnabled=" + this.mAppIdleEnabled);
    }

    public void postReportContentProviderUsage(String str, String str2, int i) {
        this.mHandler.obtainMessage(8, ContentProviderUsageRecord.obtain(str, str2, i)).sendToTarget();
    }

    public void postReportSyncScheduled(String str, int i, boolean z) {
        this.mHandler.obtainMessage(12, i, z ? 1 : 0, str).sendToTarget();
    }

    public void postReportExemptedSyncStart(String str, int i) {
        this.mHandler.obtainMessage(13, i, 0, str).sendToTarget();
    }

    @VisibleForTesting
    public AppIdleHistory getAppIdleHistoryForTest() {
        AppIdleHistory appIdleHistory;
        synchronized (this.mAppIdleLock) {
            appIdleHistory = this.mAppIdleHistory;
        }
        return appIdleHistory;
    }

    public void dumpUsers(IndentingPrintWriter indentingPrintWriter, int[] iArr, List<String> list) {
        synchronized (this.mAppIdleLock) {
            this.mAppIdleHistory.dumpUsers(indentingPrintWriter, iArr, list);
        }
    }

    public void dumpState(String[] strArr, PrintWriter printWriter) {
        synchronized (this.mCarrierPrivilegedLock) {
            printWriter.println("Carrier privileged apps (have=" + this.mHaveCarrierPrivilegedApps + "): " + this.mCarrierPrivilegedApps);
        }
        printWriter.println();
        printWriter.println("Settings:");
        printWriter.print("  mCheckIdleIntervalMillis=");
        TimeUtils.formatDuration(this.mCheckIdleIntervalMillis, printWriter);
        printWriter.println();
        printWriter.print("  mStrongUsageTimeoutMillis=");
        TimeUtils.formatDuration(this.mStrongUsageTimeoutMillis, printWriter);
        printWriter.println();
        printWriter.print("  mNotificationSeenTimeoutMillis=");
        TimeUtils.formatDuration(this.mNotificationSeenTimeoutMillis, printWriter);
        printWriter.println();
        printWriter.print("  mNotificationSeenPromotedBucket=");
        printWriter.print(UsageStatsManager.standbyBucketToString(this.mNotificationSeenPromotedBucket));
        printWriter.println();
        printWriter.print("  mTriggerQuotaBumpOnNotificationSeen=");
        printWriter.print(this.mTriggerQuotaBumpOnNotificationSeen);
        printWriter.println();
        printWriter.print("  mRetainNotificationSeenImpactForPreTApps=");
        printWriter.print(this.mRetainNotificationSeenImpactForPreTApps);
        printWriter.println();
        printWriter.print("  mSlicePinnedTimeoutMillis=");
        TimeUtils.formatDuration(this.mSlicePinnedTimeoutMillis, printWriter);
        printWriter.println();
        printWriter.print("  mSyncAdapterTimeoutMillis=");
        TimeUtils.formatDuration(this.mSyncAdapterTimeoutMillis, printWriter);
        printWriter.println();
        printWriter.print("  mSystemInteractionTimeoutMillis=");
        TimeUtils.formatDuration(this.mSystemInteractionTimeoutMillis, printWriter);
        printWriter.println();
        printWriter.print("  mInitialForegroundServiceStartTimeoutMillis=");
        TimeUtils.formatDuration(this.mInitialForegroundServiceStartTimeoutMillis, printWriter);
        printWriter.println();
        printWriter.print("  mPredictionTimeoutMillis=");
        TimeUtils.formatDuration(this.mPredictionTimeoutMillis, printWriter);
        printWriter.println();
        printWriter.print("  mExemptedSyncScheduledNonDozeTimeoutMillis=");
        TimeUtils.formatDuration(this.mExemptedSyncScheduledNonDozeTimeoutMillis, printWriter);
        printWriter.println();
        printWriter.print("  mExemptedSyncScheduledDozeTimeoutMillis=");
        TimeUtils.formatDuration(this.mExemptedSyncScheduledDozeTimeoutMillis, printWriter);
        printWriter.println();
        printWriter.print("  mExemptedSyncStartTimeoutMillis=");
        TimeUtils.formatDuration(this.mExemptedSyncStartTimeoutMillis, printWriter);
        printWriter.println();
        printWriter.print("  mUnexemptedSyncScheduledTimeoutMillis=");
        TimeUtils.formatDuration(this.mUnexemptedSyncScheduledTimeoutMillis, printWriter);
        printWriter.println();
        printWriter.print("  mSystemUpdateUsageTimeoutMillis=");
        TimeUtils.formatDuration(this.mSystemUpdateUsageTimeoutMillis, printWriter);
        printWriter.println();
        printWriter.print("  mBroadcastResponseWindowDurationMillis=");
        TimeUtils.formatDuration(this.mBroadcastResponseWindowDurationMillis, printWriter);
        printWriter.println();
        printWriter.print("  mBroadcastResponseFgThresholdState=");
        printWriter.print(ActivityManager.procStateToString(this.mBroadcastResponseFgThresholdState));
        printWriter.println();
        printWriter.print("  mBroadcastSessionsDurationMs=");
        TimeUtils.formatDuration(this.mBroadcastSessionsDurationMs, printWriter);
        printWriter.println();
        printWriter.print("  mBroadcastSessionsWithResponseDurationMs=");
        TimeUtils.formatDuration(this.mBroadcastSessionsWithResponseDurationMs, printWriter);
        printWriter.println();
        printWriter.print("  mNoteResponseEventForAllBroadcastSessions=");
        printWriter.print(this.mNoteResponseEventForAllBroadcastSessions);
        printWriter.println();
        printWriter.print("  mBroadcastResponseExemptedRoles=");
        printWriter.print(this.mBroadcastResponseExemptedRoles);
        printWriter.println();
        printWriter.print("  mBroadcastResponseExemptedPermissions=");
        printWriter.print(this.mBroadcastResponseExemptedPermissions);
        printWriter.println();
        printWriter.println();
        printWriter.print("mAppIdleEnabled=");
        printWriter.print(this.mAppIdleEnabled);
        printWriter.print(" mAllowRestrictedBucket=");
        printWriter.print(this.mAllowRestrictedBucket);
        printWriter.print(" mIsCharging=");
        printWriter.print(this.mIsCharging);
        printWriter.println();
        printWriter.print("mScreenThresholds=");
        printWriter.println(Arrays.toString(this.mAppStandbyScreenThresholds));
        printWriter.print("mElapsedThresholds=");
        printWriter.println(Arrays.toString(this.mAppStandbyElapsedThresholds));
        printWriter.println();
        printWriter.println("mHeadlessSystemApps=[");
        synchronized (this.mHeadlessSystemApps) {
            for (int size = this.mHeadlessSystemApps.size() - 1; size >= 0; size--) {
                printWriter.print("  ");
                printWriter.print(this.mHeadlessSystemApps.valueAt(size));
                if (size != 0) {
                    printWriter.println(",");
                }
            }
        }
        printWriter.println("]");
        printWriter.println();
        printWriter.println("mSystemPackagesAppIds=[");
        synchronized (this.mSystemPackagesAppIds) {
            for (int size2 = this.mSystemPackagesAppIds.size() - 1; size2 >= 0; size2--) {
                printWriter.print("  ");
                printWriter.print(this.mSystemPackagesAppIds.get(size2));
                if (size2 != 0) {
                    printWriter.println(",");
                }
            }
        }
        printWriter.println("]");
        printWriter.println();
        this.mInjector.dump(printWriter);
    }

    /* loaded from: classes2.dex */
    public static class Injector {
        public AlarmManagerInternal mAlarmManagerInternal;
        public BatteryManager mBatteryManager;
        public IBatteryStats mBatteryStats;
        public int mBootPhase;
        public final Context mContext;
        public CrossProfileAppsInternal mCrossProfileAppsInternal;
        public IDeviceIdleController mDeviceIdleController;
        public DisplayManager mDisplayManager;
        public final Looper mLooper;
        public PackageManagerInternal mPackageManagerInternal;
        public PowerManager mPowerManager;
        public long mAutoRestrictedBucketDelayMs = BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS;
        @GuardedBy({"mPowerWhitelistedApps"})
        public final ArraySet<String> mPowerWhitelistedApps = new ArraySet<>();
        public String mWellbeingApp = null;

        public Injector(Context context, Looper looper) {
            this.mContext = context;
            this.mLooper = looper;
        }

        public Context getContext() {
            return this.mContext;
        }

        public Looper getLooper() {
            return this.mLooper;
        }

        public void onBootPhase(int i) {
            if (i == 500) {
                this.mDeviceIdleController = IDeviceIdleController.Stub.asInterface(ServiceManager.getService("deviceidle"));
                this.mBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
                this.mPackageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
                this.mDisplayManager = (DisplayManager) this.mContext.getSystemService("display");
                this.mPowerManager = (PowerManager) this.mContext.getSystemService(PowerManager.class);
                this.mBatteryManager = (BatteryManager) this.mContext.getSystemService(BatteryManager.class);
                this.mCrossProfileAppsInternal = (CrossProfileAppsInternal) LocalServices.getService(CrossProfileAppsInternal.class);
                this.mAlarmManagerInternal = (AlarmManagerInternal) LocalServices.getService(AlarmManagerInternal.class);
                if (((ActivityManager) this.mContext.getSystemService("activity")).isLowRamDevice() || ActivityManager.isSmallBatteryDevice()) {
                    this.mAutoRestrictedBucketDelayMs = 43200000L;
                }
            } else if (i == 1000) {
                this.mWellbeingApp = this.mContext.getPackageManager().getWellbeingPackageName();
            }
            this.mBootPhase = i;
        }

        public int getBootPhase() {
            return this.mBootPhase;
        }

        public long elapsedRealtime() {
            return SystemClock.elapsedRealtime();
        }

        public boolean isAppIdleEnabled() {
            return this.mContext.getResources().getBoolean(17891644) && (Settings.Global.getInt(this.mContext.getContentResolver(), "app_standby_enabled", 1) == 1 && Settings.Global.getInt(this.mContext.getContentResolver(), "adaptive_battery_management_enabled", 1) == 1);
        }

        public boolean isCharging() {
            return this.mBatteryManager.isCharging();
        }

        public boolean isNonIdleWhitelisted(String str) {
            boolean contains;
            if (this.mBootPhase < 500) {
                return false;
            }
            synchronized (this.mPowerWhitelistedApps) {
                contains = this.mPowerWhitelistedApps.contains(str);
            }
            return contains;
        }

        public IAppOpsService getAppOpsService() {
            return IAppOpsService.Stub.asInterface(ServiceManager.getService("appops"));
        }

        public boolean isWellbeingPackage(String str) {
            return str.equals(this.mWellbeingApp);
        }

        public boolean shouldGetExactAlarmBucketElevation(String str, int i) {
            return this.mAlarmManagerInternal.shouldGetBucketElevation(str, i);
        }

        public void updatePowerWhitelistCache() {
            try {
                String[] fullPowerWhitelistExceptIdle = this.mDeviceIdleController.getFullPowerWhitelistExceptIdle();
                synchronized (this.mPowerWhitelistedApps) {
                    this.mPowerWhitelistedApps.clear();
                    for (String str : fullPowerWhitelistExceptIdle) {
                        this.mPowerWhitelistedApps.add(str);
                    }
                }
            } catch (RemoteException e) {
                Slog.wtf("AppStandbyController", "Failed to get power whitelist", e);
            }
        }

        public boolean isRestrictedBucketEnabled() {
            return Settings.Global.getInt(this.mContext.getContentResolver(), "enable_restricted_bucket", 1) == 1;
        }

        public File getDataSystemDirectory() {
            return Environment.getDataSystemDirectory();
        }

        public long getAutoRestrictedBucketDelayMs() {
            return this.mAutoRestrictedBucketDelayMs;
        }

        public void noteEvent(int i, String str, int i2) throws RemoteException {
            IBatteryStats iBatteryStats = this.mBatteryStats;
            if (iBatteryStats != null) {
                iBatteryStats.noteEvent(i, str, i2);
            }
        }

        public PackageManagerInternal getPackageManagerInternal() {
            return this.mPackageManagerInternal;
        }

        public boolean isPackageEphemeral(int i, String str) {
            return this.mPackageManagerInternal.isPackageEphemeral(i, str);
        }

        public boolean isPackageInstalled(String str, int i, int i2) {
            return this.mPackageManagerInternal.getPackageUid(str, (long) i, i2) >= 0;
        }

        public int[] getRunningUserIds() throws RemoteException {
            return ActivityManager.getService().getRunningUserIds();
        }

        public boolean isDefaultDisplayOn() {
            return this.mDisplayManager.getDisplay(0).getState() == 2;
        }

        public void registerDisplayListener(DisplayManager.DisplayListener displayListener, Handler handler) {
            this.mDisplayManager.registerDisplayListener(displayListener, handler);
        }

        public String getActiveNetworkScorer() {
            return ((NetworkScoreManager) this.mContext.getSystemService("network_score")).getActiveScorerPackage();
        }

        public boolean isBoundWidgetPackage(AppWidgetManager appWidgetManager, String str, int i) {
            return appWidgetManager.isBoundWidgetPackage(str, i);
        }

        public DeviceConfig.Properties getDeviceConfigProperties(String... strArr) {
            return DeviceConfig.getProperties("app_standby", strArr);
        }

        public boolean isDeviceIdleMode() {
            return this.mPowerManager.isDeviceIdleMode();
        }

        public List<UserHandle> getValidCrossProfileTargets(String str, int i) {
            int packageUid = this.mPackageManagerInternal.getPackageUid(str, 0L, i);
            AndroidPackage androidPackage = this.mPackageManagerInternal.getPackage(packageUid);
            if (packageUid < 0 || androidPackage == null || !androidPackage.isCrossProfile() || !this.mCrossProfileAppsInternal.verifyUidHasInteractAcrossProfilePermission(str, packageUid)) {
                if (packageUid >= 0 && androidPackage == null) {
                    Slog.wtf("AppStandbyController", "Null package retrieved for UID " + packageUid);
                }
                return Collections.emptyList();
            }
            return this.mCrossProfileAppsInternal.getTargetUserProfiles(str, i);
        }

        public void registerDeviceConfigPropertiesChangedListener(DeviceConfig.OnPropertiesChangedListener onPropertiesChangedListener) {
            DeviceConfig.addOnPropertiesChangedListener("app_standby", AppSchedulingModuleThread.getExecutor(), onPropertiesChangedListener);
        }

        public void dump(PrintWriter printWriter) {
            printWriter.println("mPowerWhitelistedApps=[");
            synchronized (this.mPowerWhitelistedApps) {
                for (int size = this.mPowerWhitelistedApps.size() - 1; size >= 0; size--) {
                    printWriter.print("  ");
                    printWriter.print(this.mPowerWhitelistedApps.valueAt(size));
                    printWriter.println(",");
                }
            }
            printWriter.println("]");
            printWriter.println();
        }
    }

    /* loaded from: classes2.dex */
    public class AppStandbyHandler extends Handler {
        public AppStandbyHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            long j;
            switch (message.what) {
                case 3:
                    StandbyUpdateRecord standbyUpdateRecord = (StandbyUpdateRecord) message.obj;
                    AppStandbyController.this.informListeners(standbyUpdateRecord.packageName, standbyUpdateRecord.userId, standbyUpdateRecord.bucket, standbyUpdateRecord.reason, standbyUpdateRecord.isUserInteraction);
                    standbyUpdateRecord.recycle();
                    return;
                case 4:
                    AppStandbyController.this.forceIdleState((String) message.obj, message.arg1, message.arg2 == 1);
                    return;
                case 5:
                    removeMessages(5);
                    long elapsedRealtime = AppStandbyController.this.mInjector.elapsedRealtime();
                    synchronized (AppStandbyController.this.mPendingIdleStateChecks) {
                        j = Long.MAX_VALUE;
                        for (int size = AppStandbyController.this.mPendingIdleStateChecks.size() - 1; size >= 0; size--) {
                            long valueAt = AppStandbyController.this.mPendingIdleStateChecks.valueAt(size);
                            if (valueAt <= elapsedRealtime) {
                                int keyAt = AppStandbyController.this.mPendingIdleStateChecks.keyAt(size);
                                if (AppStandbyController.this.checkIdleStates(keyAt) && AppStandbyController.this.mAppIdleEnabled) {
                                    AppStandbyController appStandbyController = AppStandbyController.this;
                                    long j2 = appStandbyController.mCheckIdleIntervalMillis + elapsedRealtime;
                                    appStandbyController.mPendingIdleStateChecks.put(keyAt, j2);
                                    valueAt = j2;
                                } else {
                                    AppStandbyController.this.mPendingIdleStateChecks.removeAt(size);
                                }
                            }
                            j = Math.min(j, valueAt);
                        }
                    }
                    if (j != Long.MAX_VALUE) {
                        AppStandbyController.this.mHandler.sendMessageDelayed(AppStandbyController.this.mHandler.obtainMessage(5), j - elapsedRealtime);
                        return;
                    }
                    return;
                case 6:
                default:
                    super.handleMessage(message);
                    return;
                case 7:
                    AppStandbyController.this.triggerListenerQuotaBump((String) message.obj, message.arg1);
                    return;
                case 8:
                    ContentProviderUsageRecord contentProviderUsageRecord = (ContentProviderUsageRecord) message.obj;
                    AppStandbyController.this.reportContentProviderUsage(contentProviderUsageRecord.name, contentProviderUsageRecord.packageName, contentProviderUsageRecord.userId);
                    contentProviderUsageRecord.recycle();
                    return;
                case 9:
                    AppStandbyController.this.informParoleStateChanged();
                    return;
                case 10:
                    AppStandbyController.this.mHandler.removeMessages(10);
                    AppStandbyController.this.waitForAdminData();
                    AppStandbyController.this.checkIdleStates(-1);
                    return;
                case 11:
                    AppStandbyController appStandbyController2 = AppStandbyController.this;
                    appStandbyController2.checkAndUpdateStandbyState((String) message.obj, message.arg1, message.arg2, appStandbyController2.mInjector.elapsedRealtime());
                    return;
                case 12:
                    if (message.arg2 > 0) {
                        AppStandbyController.this.reportExemptedSyncScheduled((String) message.obj, message.arg1);
                        return;
                    } else {
                        AppStandbyController.this.reportUnexemptedSyncScheduled((String) message.obj, message.arg1);
                        return;
                    }
                case 13:
                    AppStandbyController.this.reportExemptedSyncStart((String) message.obj, message.arg1);
                    return;
            }
        }
    }

    /* loaded from: classes2.dex */
    public class DeviceStateReceiver extends BroadcastReceiver {
        public DeviceStateReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            action.hashCode();
            char c = 65535;
            switch (action.hashCode()) {
                case -65633567:
                    if (action.equals("android.os.action.POWER_SAVE_WHITELIST_CHANGED")) {
                        c = 0;
                        break;
                    }
                    break;
                case -54942926:
                    if (action.equals("android.os.action.DISCHARGING")) {
                        c = 1;
                        break;
                    }
                    break;
                case 948344062:
                    if (action.equals("android.os.action.CHARGING")) {
                        c = 2;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    if (AppStandbyController.this.mSystemServicesReady) {
                        AppStandbyHandler appStandbyHandler = AppStandbyController.this.mHandler;
                        final AppStandbyController appStandbyController = AppStandbyController.this;
                        appStandbyHandler.post(new Runnable() { // from class: com.android.server.usage.AppStandbyController$DeviceStateReceiver$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                AppStandbyController.this.updatePowerWhitelistCache();
                            }
                        });
                        return;
                    }
                    return;
                case 1:
                    AppStandbyController.this.setChargingState(false);
                    return;
                case 2:
                    AppStandbyController.this.setChargingState(true);
                    return;
                default:
                    return;
            }
        }
    }

    /* loaded from: classes2.dex */
    public class ConstantsObserver extends ContentObserver implements DeviceConfig.OnPropertiesChangedListener {
        public final String[] KEYS_ELAPSED_TIME_THRESHOLDS;
        public final String[] KEYS_SCREEN_TIME_THRESHOLDS;
        public final TextUtils.SimpleStringSplitter mStringPipeSplitter;

        public ConstantsObserver(Handler handler) {
            super(handler);
            this.KEYS_SCREEN_TIME_THRESHOLDS = new String[]{"screen_threshold_active", "screen_threshold_working_set", "screen_threshold_frequent", "screen_threshold_rare", "screen_threshold_restricted"};
            this.KEYS_ELAPSED_TIME_THRESHOLDS = new String[]{"elapsed_threshold_active", "elapsed_threshold_working_set", "elapsed_threshold_frequent", "elapsed_threshold_rare", "elapsed_threshold_restricted"};
            this.mStringPipeSplitter = new TextUtils.SimpleStringSplitter('|');
        }

        public void start() {
            ContentResolver contentResolver = AppStandbyController.this.mContext.getContentResolver();
            contentResolver.registerContentObserver(Settings.Global.getUriFor("app_standby_enabled"), false, this);
            contentResolver.registerContentObserver(Settings.Global.getUriFor("enable_restricted_bucket"), false, this);
            contentResolver.registerContentObserver(Settings.Global.getUriFor("adaptive_battery_management_enabled"), false, this);
            AppStandbyController.this.mInjector.registerDeviceConfigPropertiesChangedListener(this);
            processProperties(AppStandbyController.this.mInjector.getDeviceConfigProperties(new String[0]));
            updateSettings();
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            updateSettings();
            AppStandbyController.this.postOneTimeCheckIdleStates();
        }

        public void onPropertiesChanged(DeviceConfig.Properties properties) {
            processProperties(properties);
            AppStandbyController.this.postOneTimeCheckIdleStates();
        }

        public final void processProperties(DeviceConfig.Properties properties) {
            char c;
            synchronized (AppStandbyController.this.mAppIdleLock) {
                boolean z = false;
                for (String str : properties.getKeyset()) {
                    if (str != null) {
                        switch (str.hashCode()) {
                            case -1991469656:
                                if (str.equals("sync_adapter_duration")) {
                                    c = '\f';
                                    break;
                                }
                                c = 65535;
                                break;
                            case -1963219299:
                                if (str.equals("brodacast_response_exempted_permissions")) {
                                    c = 23;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -1794959158:
                                if (str.equals("trigger_quota_bump_on_notification_seen")) {
                                    c = 6;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -1610671326:
                                if (str.equals("unexempted_sync_scheduled_duration")) {
                                    c = 16;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -1525033432:
                                if (str.equals("broadcast_sessions_with_response_duration_ms")) {
                                    c = 20;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -1063555730:
                                if (str.equals("slice_pinned_duration")) {
                                    c = 7;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -973233853:
                                if (str.equals("auto_restricted_bucket_delay_ms")) {
                                    c = 0;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -695619964:
                                if (str.equals("notification_seen_duration")) {
                                    c = 3;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -654339791:
                                if (str.equals("system_interaction_duration")) {
                                    c = '\n';
                                    break;
                                }
                                c = 65535;
                                break;
                            case -641750299:
                                if (str.equals("note_response_event_for_all_broadcast_sessions")) {
                                    c = 21;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -557676904:
                                if (str.equals("system_update_usage_duration")) {
                                    c = 11;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -294320234:
                                if (str.equals("brodacast_response_exempted_roles")) {
                                    c = 22;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -129077581:
                                if (str.equals("broadcast_response_window_timeout_ms")) {
                                    c = 17;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -57661244:
                                if (str.equals("exempted_sync_scheduled_d_duration")) {
                                    c = '\r';
                                    break;
                                }
                                c = 65535;
                                break;
                            case 276460958:
                                if (str.equals("retain_notification_seen_impact_for_pre_t_apps")) {
                                    c = 5;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 456604392:
                                if (str.equals("exempted_sync_scheduled_nd_duration")) {
                                    c = 14;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 742365823:
                                if (str.equals("broadcast_response_fg_threshold_state")) {
                                    c = 18;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 938381045:
                                if (str.equals("notification_seen_promoted_bucket")) {
                                    c = 4;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 992238669:
                                if (str.equals("broadcast_sessions_duration_ms")) {
                                    c = 19;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1105744372:
                                if (str.equals("exempted_sync_start_duration")) {
                                    c = 15;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1288386175:
                                if (str.equals("cross_profile_apps_share_standby_buckets")) {
                                    c = 1;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1378352561:
                                if (str.equals("prediction_timeout")) {
                                    c = '\t';
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1400233242:
                                if (str.equals("strong_usage_duration")) {
                                    c = '\b';
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1915246556:
                                if (str.equals("initial_foreground_service_start_duration")) {
                                    c = 2;
                                    break;
                                }
                                c = 65535;
                                break;
                            default:
                                c = 65535;
                                break;
                        }
                        boolean z2 = z;
                        switch (c) {
                            case 0:
                                AppStandbyController.this.mInjector.mAutoRestrictedBucketDelayMs = Math.max((long) BackupManagerConstants.DEFAULT_KEY_VALUE_BACKUP_INTERVAL_MILLISECONDS, properties.getLong("auto_restricted_bucket_delay_ms", (long) BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS));
                                z = z2;
                                break;
                            case 1:
                                AppStandbyController.this.mLinkCrossProfileApps = properties.getBoolean("cross_profile_apps_share_standby_buckets", true);
                                z = z2;
                                break;
                            case 2:
                                AppStandbyController.this.mInitialForegroundServiceStartTimeoutMillis = properties.getLong("initial_foreground_service_start_duration", 1800000L);
                                z = z2;
                                break;
                            case 3:
                                AppStandbyController.this.mNotificationSeenTimeoutMillis = properties.getLong("notification_seen_duration", 43200000L);
                                z = z2;
                                break;
                            case 4:
                                AppStandbyController.this.mNotificationSeenPromotedBucket = properties.getInt("notification_seen_promoted_bucket", 20);
                                z = z2;
                                break;
                            case 5:
                                AppStandbyController.this.mRetainNotificationSeenImpactForPreTApps = properties.getBoolean("retain_notification_seen_impact_for_pre_t_apps", false);
                                z = z2;
                                break;
                            case 6:
                                AppStandbyController.this.mTriggerQuotaBumpOnNotificationSeen = properties.getBoolean("trigger_quota_bump_on_notification_seen", false);
                                z = z2;
                                break;
                            case 7:
                                AppStandbyController.this.mSlicePinnedTimeoutMillis = properties.getLong("slice_pinned_duration", 43200000L);
                                z = z2;
                                break;
                            case '\b':
                                AppStandbyController.this.mStrongUsageTimeoutMillis = properties.getLong("strong_usage_duration", (long) ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS);
                                z = z2;
                                break;
                            case '\t':
                                AppStandbyController.this.mPredictionTimeoutMillis = properties.getLong("prediction_timeout", 43200000L);
                                z = z2;
                                break;
                            case '\n':
                                AppStandbyController.this.mSystemInteractionTimeoutMillis = properties.getLong("system_interaction_duration", 600000L);
                                z = z2;
                                break;
                            case 11:
                                AppStandbyController.this.mSystemUpdateUsageTimeoutMillis = properties.getLong("system_update_usage_duration", 7200000L);
                                z = z2;
                                break;
                            case '\f':
                                AppStandbyController.this.mSyncAdapterTimeoutMillis = properties.getLong("sync_adapter_duration", 600000L);
                                z = z2;
                                break;
                            case '\r':
                                AppStandbyController.this.mExemptedSyncScheduledDozeTimeoutMillis = properties.getLong("exempted_sync_scheduled_d_duration", (long) BackupManagerConstants.DEFAULT_KEY_VALUE_BACKUP_INTERVAL_MILLISECONDS);
                                z = z2;
                                break;
                            case 14:
                                AppStandbyController.this.mExemptedSyncScheduledNonDozeTimeoutMillis = properties.getLong("exempted_sync_scheduled_nd_duration", 600000L);
                                z = z2;
                                break;
                            case 15:
                                AppStandbyController.this.mExemptedSyncStartTimeoutMillis = properties.getLong("exempted_sync_start_duration", 600000L);
                                z = z2;
                                break;
                            case 16:
                                AppStandbyController.this.mUnexemptedSyncScheduledTimeoutMillis = properties.getLong("unexempted_sync_scheduled_duration", 600000L);
                                z = z2;
                                break;
                            case 17:
                                AppStandbyController.this.mBroadcastResponseWindowDurationMillis = properties.getLong("broadcast_response_window_timeout_ms", 120000L);
                                z = z2;
                                break;
                            case 18:
                                AppStandbyController.this.mBroadcastResponseFgThresholdState = properties.getInt("broadcast_response_fg_threshold_state", 2);
                                z = z2;
                                break;
                            case 19:
                                AppStandbyController.this.mBroadcastSessionsDurationMs = properties.getLong("broadcast_sessions_duration_ms", 120000L);
                                z = z2;
                                break;
                            case 20:
                                AppStandbyController.this.mBroadcastSessionsWithResponseDurationMs = properties.getLong("broadcast_sessions_with_response_duration_ms", 120000L);
                                z = z2;
                                break;
                            case 21:
                                AppStandbyController.this.mNoteResponseEventForAllBroadcastSessions = properties.getBoolean("note_response_event_for_all_broadcast_sessions", true);
                                z = z2;
                                break;
                            case 22:
                                AppStandbyController.this.mBroadcastResponseExemptedRoles = properties.getString("brodacast_response_exempted_roles", "");
                                AppStandbyController appStandbyController = AppStandbyController.this;
                                appStandbyController.mBroadcastResponseExemptedRolesList = splitPipeSeparatedString(appStandbyController.mBroadcastResponseExemptedRoles);
                                z = z2;
                                break;
                            case 23:
                                AppStandbyController.this.mBroadcastResponseExemptedPermissions = properties.getString("brodacast_response_exempted_permissions", "");
                                AppStandbyController appStandbyController2 = AppStandbyController.this;
                                appStandbyController2.mBroadcastResponseExemptedPermissionsList = splitPipeSeparatedString(appStandbyController2.mBroadcastResponseExemptedPermissions);
                                z = z2;
                                break;
                            default:
                                if (!z2 && (str.startsWith("screen_threshold_") || str.startsWith("elapsed_threshold_"))) {
                                    updateTimeThresholds();
                                    z = true;
                                    break;
                                }
                                z = z2;
                                break;
                        }
                        AppStandbyController.this.mAppStandbyProperties.put(str, properties.getString(str, (String) null));
                    }
                }
            }
        }

        public final List<String> splitPipeSeparatedString(String str) {
            ArrayList arrayList = new ArrayList();
            this.mStringPipeSplitter.setString(str);
            while (this.mStringPipeSplitter.hasNext()) {
                arrayList.add(this.mStringPipeSplitter.next());
            }
            return arrayList;
        }

        public final void updateTimeThresholds() {
            DeviceConfig.Properties deviceConfigProperties = AppStandbyController.this.mInjector.getDeviceConfigProperties(this.KEYS_SCREEN_TIME_THRESHOLDS);
            DeviceConfig.Properties deviceConfigProperties2 = AppStandbyController.this.mInjector.getDeviceConfigProperties(this.KEYS_ELAPSED_TIME_THRESHOLDS);
            AppStandbyController.this.mAppStandbyScreenThresholds = generateThresholdArray(deviceConfigProperties, this.KEYS_SCREEN_TIME_THRESHOLDS, AppStandbyController.DEFAULT_SCREEN_TIME_THRESHOLDS, AppStandbyController.MINIMUM_SCREEN_TIME_THRESHOLDS);
            AppStandbyController.this.mAppStandbyElapsedThresholds = generateThresholdArray(deviceConfigProperties2, this.KEYS_ELAPSED_TIME_THRESHOLDS, AppStandbyController.DEFAULT_ELAPSED_TIME_THRESHOLDS, AppStandbyController.MINIMUM_ELAPSED_TIME_THRESHOLDS);
            AppStandbyController appStandbyController = AppStandbyController.this;
            appStandbyController.mCheckIdleIntervalMillis = Math.min(appStandbyController.mAppStandbyElapsedThresholds[1] / 4, (long) BackupManagerConstants.DEFAULT_KEY_VALUE_BACKUP_INTERVAL_MILLISECONDS);
        }

        public void updateSettings() {
            synchronized (AppStandbyController.this.mAppIdleLock) {
                AppStandbyController appStandbyController = AppStandbyController.this;
                appStandbyController.mAllowRestrictedBucket = appStandbyController.mInjector.isRestrictedBucketEnabled();
            }
            AppStandbyController appStandbyController2 = AppStandbyController.this;
            appStandbyController2.setAppIdleEnabled(appStandbyController2.mInjector.isAppIdleEnabled());
        }

        public long[] generateThresholdArray(DeviceConfig.Properties properties, String[] strArr, long[] jArr, long[] jArr2) {
            if (properties.getKeyset().isEmpty()) {
                return jArr;
            }
            if (strArr.length != AppStandbyController.THRESHOLD_BUCKETS.length) {
                throw new IllegalStateException("# keys (" + strArr.length + ") != # buckets (" + AppStandbyController.THRESHOLD_BUCKETS.length + ")");
            } else if (jArr.length != AppStandbyController.THRESHOLD_BUCKETS.length) {
                throw new IllegalStateException("# defaults (" + jArr.length + ") != # buckets (" + AppStandbyController.THRESHOLD_BUCKETS.length + ")");
            } else {
                if (jArr2.length != AppStandbyController.THRESHOLD_BUCKETS.length) {
                    Slog.wtf("AppStandbyController", "minValues array is the wrong size");
                    jArr2 = new long[AppStandbyController.THRESHOLD_BUCKETS.length];
                }
                long[] jArr3 = new long[AppStandbyController.THRESHOLD_BUCKETS.length];
                for (int i = 0; i < AppStandbyController.THRESHOLD_BUCKETS.length; i++) {
                    jArr3[i] = Math.max(jArr2[i], properties.getLong(strArr[i], jArr[i]));
                }
                return jArr3;
            }
        }
    }
}
