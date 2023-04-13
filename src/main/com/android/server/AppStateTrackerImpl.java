package com.android.server;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.app.IUidObserver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManagerInternal;
import android.os.PowerSaveState;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.util.SparseSetArray;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IAppOpsCallback;
import com.android.internal.app.IAppOpsService;
import com.android.internal.util.jobs.ArrayUtils;
import com.android.internal.util.jobs.StatLogger;
import com.android.server.AppStateTracker;
import com.android.server.usage.AppStandbyInternal;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class AppStateTrackerImpl implements AppStateTracker {
    @VisibleForTesting
    static final int TARGET_OP = 70;
    public ActivityManagerInternal mActivityManagerInternal;
    public AppOpsManager mAppOpsManager;
    public IAppOpsService mAppOpsService;
    public AppStandbyInternal mAppStandbyInternal;
    @GuardedBy({"mLock"})
    public boolean mBatterySaverEnabled;
    public final Context mContext;
    @VisibleForTesting
    FeatureFlagsObserver mFlagsObserver;
    @GuardedBy({"mLock"})
    public boolean mForceAllAppStandbyForSmallBattery;
    @GuardedBy({"mLock"})
    public boolean mForceAllAppsStandby;
    public final MyHandler mHandler;
    public IActivityManager mIActivityManager;
    @GuardedBy({"mLock"})
    public boolean mIsPluggedIn;
    @GuardedBy({"mLock"})
    public int[] mPowerExemptAllAppIds;
    public PowerManagerInternal mPowerManagerInternal;
    public StandbyTracker mStandbyTracker;
    @GuardedBy({"mLock"})
    public boolean mStarted;
    @GuardedBy({"mLock"})
    public int[] mTempExemptAppIds;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final ArraySet<Pair<Integer, String>> mRunAnyRestrictedPackages = new ArraySet<>();
    @GuardedBy({"mLock"})
    public final SparseBooleanArray mActiveUids = new SparseBooleanArray();
    @GuardedBy({"mLock"})
    public int[] mPowerExemptUserAppIds = new int[0];
    @GuardedBy({"mLock"})
    @VisibleForTesting
    final SparseSetArray<String> mExemptedBucketPackages = new SparseSetArray<>();
    @GuardedBy({"mLock"})
    public final ArraySet<Listener> mListeners = new ArraySet<>();
    public volatile Set<Pair<Integer, String>> mBackgroundRestrictedUidPackages = Collections.emptySet();
    public final StatLogger mStatLogger = new StatLogger(new String[]{"UID_FG_STATE_CHANGED", "UID_ACTIVE_STATE_CHANGED", "RUN_ANY_CHANGED", "ALL_UNEXEMPTED", "ALL_EXEMPTION_LIST_CHANGED", "TEMP_EXEMPTION_LIST_CHANGED", "EXEMPTED_BUCKET_CHANGED", "FORCE_ALL_CHANGED", "IS_UID_ACTIVE_CACHED", "IS_UID_ACTIVE_RAW"});
    public final ActivityManagerInternal.AppBackgroundRestrictionListener mAppBackgroundRestrictionListener = new ActivityManagerInternal.AppBackgroundRestrictionListener() { // from class: com.android.server.AppStateTrackerImpl.2
        public void onAutoRestrictedBucketFeatureFlagChanged(boolean z) {
            AppStateTrackerImpl.this.mHandler.notifyAutoRestrictedBucketFeatureFlagChanged(z);
        }
    };
    public final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.server.AppStateTrackerImpl.3
        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            char c;
            int intExtra = intent.getIntExtra("android.intent.extra.user_handle", -1);
            String action = intent.getAction();
            action.hashCode();
            boolean z = true;
            switch (action.hashCode()) {
                case -2061058799:
                    if (action.equals("android.intent.action.USER_REMOVED")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case -1538406691:
                    if (action.equals("android.intent.action.BATTERY_CHANGED")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 525384130:
                    if (action.equals("android.intent.action.PACKAGE_REMOVED")) {
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
                    if (intExtra > 0) {
                        AppStateTrackerImpl.this.mHandler.doUserRemoved(intExtra);
                        return;
                    }
                    return;
                case 1:
                    synchronized (AppStateTrackerImpl.this.mLock) {
                        AppStateTrackerImpl appStateTrackerImpl = AppStateTrackerImpl.this;
                        if (intent.getIntExtra("plugged", 0) == 0) {
                            z = false;
                        }
                        appStateTrackerImpl.mIsPluggedIn = z;
                    }
                    AppStateTrackerImpl.this.updateForceAllAppStandbyState();
                    return;
                case 2:
                    if (intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                        return;
                    }
                    String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
                    int intExtra2 = intent.getIntExtra("android.intent.extra.UID", -1);
                    synchronized (AppStateTrackerImpl.this.mLock) {
                        AppStateTrackerImpl.this.mExemptedBucketPackages.remove(intExtra, schemeSpecificPart);
                        AppStateTrackerImpl.this.mRunAnyRestrictedPackages.remove(Pair.create(Integer.valueOf(intExtra2), schemeSpecificPart));
                        AppStateTrackerImpl.this.updateBackgroundRestrictedUidPackagesLocked();
                        AppStateTrackerImpl.this.mActiveUids.delete(intExtra2);
                    }
                    return;
                default:
                    return;
            }
        }
    };

    public void addBackgroundRestrictedAppListener(final AppStateTracker.BackgroundRestrictedAppListener backgroundRestrictedAppListener) {
        addListener(new Listener() { // from class: com.android.server.AppStateTrackerImpl.1
            @Override // com.android.server.AppStateTrackerImpl.Listener
            public void updateBackgroundRestrictedForUidPackage(int i, String str, boolean z) {
                backgroundRestrictedAppListener.updateBackgroundRestrictedForUidPackage(i, str, z);
            }
        });
    }

    public boolean isAppBackgroundRestricted(int i, String str) {
        return this.mBackgroundRestrictedUidPackages.contains(Pair.create(Integer.valueOf(i), str));
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public class FeatureFlagsObserver extends ContentObserver {
        public FeatureFlagsObserver() {
            super(null);
        }

        public void register() {
            AppStateTrackerImpl.this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("forced_app_standby_for_small_battery_enabled"), false, this);
        }

        public boolean isForcedAppStandbyForSmallBatteryEnabled() {
            return AppStateTrackerImpl.this.injectGetGlobalSettingInt("forced_app_standby_for_small_battery_enabled", 0) == 1;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            if (Settings.Global.getUriFor("forced_app_standby_for_small_battery_enabled").equals(uri)) {
                boolean isForcedAppStandbyForSmallBatteryEnabled = isForcedAppStandbyForSmallBatteryEnabled();
                synchronized (AppStateTrackerImpl.this.mLock) {
                    AppStateTrackerImpl appStateTrackerImpl = AppStateTrackerImpl.this;
                    if (appStateTrackerImpl.mForceAllAppStandbyForSmallBattery == isForcedAppStandbyForSmallBatteryEnabled) {
                        return;
                    }
                    appStateTrackerImpl.mForceAllAppStandbyForSmallBattery = isForcedAppStandbyForSmallBatteryEnabled;
                    appStateTrackerImpl.updateForceAllAppStandbyState();
                    return;
                }
            }
            Slog.w("AppStateTracker", "Unexpected feature flag uri encountered: " + uri);
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Listener {
        public void removeAlarmsForUid(int i) {
        }

        public void removeListenerAlarmsForCachedUid(int i) {
        }

        public void unblockAlarmsForUid(int i) {
        }

        public void unblockAlarmsForUidPackage(int i, String str) {
        }

        public void unblockAllUnrestrictedAlarms() {
        }

        public void updateAlarmsForUid(int i) {
        }

        public void updateAllAlarms() {
        }

        public void updateAllJobs() {
        }

        public void updateBackgroundRestrictedForUidPackage(int i, String str, boolean z) {
        }

        public void updateJobsForUid(int i, boolean z) {
        }

        public void updateJobsForUidPackage(int i, String str, boolean z) {
        }

        public final void onRunAnyAppOpsChanged(AppStateTrackerImpl appStateTrackerImpl, int i, String str) {
            updateJobsForUidPackage(i, str, appStateTrackerImpl.isUidActive(i));
            if (!appStateTrackerImpl.areAlarmsRestricted(i, str)) {
                unblockAlarmsForUidPackage(i, str);
            }
            if (!appStateTrackerImpl.isRunAnyInBackgroundAppOpsAllowed(i, str)) {
                Slog.v("AppStateTracker", "Package " + str + "/" + i + " toggled into fg service restriction");
                updateBackgroundRestrictedForUidPackage(i, str, true);
                return;
            }
            Slog.v("AppStateTracker", "Package " + str + "/" + i + " toggled out of fg service restriction");
            updateBackgroundRestrictedForUidPackage(i, str, false);
        }

        public final void onUidActiveStateChanged(AppStateTrackerImpl appStateTrackerImpl, int i) {
            boolean isUidActive = appStateTrackerImpl.isUidActive(i);
            updateJobsForUid(i, isUidActive);
            updateAlarmsForUid(i);
            if (isUidActive) {
                unblockAlarmsForUid(i);
            }
        }

        public final void onPowerSaveUnexempted(AppStateTrackerImpl appStateTrackerImpl) {
            updateAllJobs();
            updateAllAlarms();
        }

        public final void onPowerSaveExemptionListChanged(AppStateTrackerImpl appStateTrackerImpl) {
            updateAllJobs();
            updateAllAlarms();
            unblockAllUnrestrictedAlarms();
        }

        public final void onTempPowerSaveExemptionListChanged(AppStateTrackerImpl appStateTrackerImpl) {
            updateAllJobs();
        }

        public final void onExemptedBucketChanged(AppStateTrackerImpl appStateTrackerImpl) {
            updateAllJobs();
            updateAllAlarms();
        }

        public final void onForceAllAppsStandbyChanged(AppStateTrackerImpl appStateTrackerImpl) {
            updateAllJobs();
            updateAllAlarms();
        }

        public final void onAutoRestrictedBucketFeatureFlagChanged(AppStateTrackerImpl appStateTrackerImpl, boolean z) {
            updateAllJobs();
            if (z) {
                unblockAllUnrestrictedAlarms();
            }
        }
    }

    public AppStateTrackerImpl(Context context, Looper looper) {
        int[] iArr = new int[0];
        this.mPowerExemptAllAppIds = iArr;
        this.mTempExemptAppIds = iArr;
        this.mContext = context;
        this.mHandler = new MyHandler(looper);
    }

    public void onSystemServicesReady() {
        synchronized (this.mLock) {
            if (this.mStarted) {
                return;
            }
            this.mStarted = true;
            IActivityManager injectIActivityManager = injectIActivityManager();
            Objects.requireNonNull(injectIActivityManager);
            this.mIActivityManager = injectIActivityManager;
            ActivityManagerInternal injectActivityManagerInternal = injectActivityManagerInternal();
            Objects.requireNonNull(injectActivityManagerInternal);
            ActivityManagerInternal activityManagerInternal = injectActivityManagerInternal;
            this.mActivityManagerInternal = injectActivityManagerInternal;
            AppOpsManager injectAppOpsManager = injectAppOpsManager();
            Objects.requireNonNull(injectAppOpsManager);
            AppOpsManager appOpsManager = injectAppOpsManager;
            this.mAppOpsManager = injectAppOpsManager;
            IAppOpsService injectIAppOpsService = injectIAppOpsService();
            Objects.requireNonNull(injectIAppOpsService);
            this.mAppOpsService = injectIAppOpsService;
            PowerManagerInternal injectPowerManagerInternal = injectPowerManagerInternal();
            Objects.requireNonNull(injectPowerManagerInternal);
            PowerManagerInternal powerManagerInternal = injectPowerManagerInternal;
            this.mPowerManagerInternal = injectPowerManagerInternal;
            AppStandbyInternal injectAppStandbyInternal = injectAppStandbyInternal();
            Objects.requireNonNull(injectAppStandbyInternal);
            this.mAppStandbyInternal = injectAppStandbyInternal;
            FeatureFlagsObserver featureFlagsObserver = new FeatureFlagsObserver();
            this.mFlagsObserver = featureFlagsObserver;
            featureFlagsObserver.register();
            this.mForceAllAppStandbyForSmallBattery = this.mFlagsObserver.isForcedAppStandbyForSmallBatteryEnabled();
            StandbyTracker standbyTracker = new StandbyTracker();
            this.mStandbyTracker = standbyTracker;
            this.mAppStandbyInternal.addListener(standbyTracker);
            this.mActivityManagerInternal.addAppBackgroundRestrictionListener(this.mAppBackgroundRestrictionListener);
            try {
                this.mIActivityManager.registerUidObserver(new UidObserver(), 30, -1, (String) null);
                this.mAppOpsService.startWatchingMode(70, (String) null, new AppOpsWatcher());
            } catch (RemoteException unused) {
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.USER_REMOVED");
            intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
            this.mContext.registerReceiver(this.mReceiver, intentFilter);
            IntentFilter intentFilter2 = new IntentFilter("android.intent.action.PACKAGE_REMOVED");
            intentFilter2.addDataScheme("package");
            this.mContext.registerReceiver(this.mReceiver, intentFilter2);
            refreshForcedAppStandbyUidPackagesLocked();
            this.mPowerManagerInternal.registerLowPowerModeObserver(11, new Consumer() { // from class: com.android.server.AppStateTrackerImpl$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AppStateTrackerImpl.this.lambda$onSystemServicesReady$0((PowerSaveState) obj);
                }
            });
            this.mBatterySaverEnabled = this.mPowerManagerInternal.getLowPowerState(11).batterySaverEnabled;
            updateForceAllAppStandbyState();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onSystemServicesReady$0(PowerSaveState powerSaveState) {
        synchronized (this.mLock) {
            this.mBatterySaverEnabled = powerSaveState.batterySaverEnabled;
            updateForceAllAppStandbyState();
        }
    }

    @VisibleForTesting
    public AppOpsManager injectAppOpsManager() {
        return (AppOpsManager) this.mContext.getSystemService(AppOpsManager.class);
    }

    @VisibleForTesting
    public IAppOpsService injectIAppOpsService() {
        return IAppOpsService.Stub.asInterface(ServiceManager.getService("appops"));
    }

    @VisibleForTesting
    public IActivityManager injectIActivityManager() {
        return ActivityManager.getService();
    }

    @VisibleForTesting
    public ActivityManagerInternal injectActivityManagerInternal() {
        return (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
    }

    @VisibleForTesting
    public PowerManagerInternal injectPowerManagerInternal() {
        return (PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class);
    }

    @VisibleForTesting
    public AppStandbyInternal injectAppStandbyInternal() {
        return (AppStandbyInternal) LocalServices.getService(AppStandbyInternal.class);
    }

    @VisibleForTesting
    public boolean isSmallBatteryDevice() {
        return ActivityManager.isSmallBatteryDevice();
    }

    @VisibleForTesting
    public int injectGetGlobalSettingInt(String str, int i) {
        return Settings.Global.getInt(this.mContext.getContentResolver(), str, i);
    }

    @GuardedBy({"mLock"})
    public final void refreshForcedAppStandbyUidPackagesLocked() {
        this.mRunAnyRestrictedPackages.clear();
        List packagesForOps = this.mAppOpsManager.getPackagesForOps(new int[]{70});
        if (packagesForOps == null) {
            return;
        }
        int size = packagesForOps.size();
        for (int i = 0; i < size; i++) {
            AppOpsManager.PackageOps packageOps = (AppOpsManager.PackageOps) packagesForOps.get(i);
            List ops = ((AppOpsManager.PackageOps) packagesForOps.get(i)).getOps();
            for (int i2 = 0; i2 < ops.size(); i2++) {
                AppOpsManager.OpEntry opEntry = (AppOpsManager.OpEntry) ops.get(i2);
                if (opEntry.getOp() == 70 && opEntry.getMode() != 0) {
                    this.mRunAnyRestrictedPackages.add(Pair.create(Integer.valueOf(packageOps.getUid()), packageOps.getPackageName()));
                }
            }
        }
        updateBackgroundRestrictedUidPackagesLocked();
    }

    @GuardedBy({"mLock"})
    public final void updateBackgroundRestrictedUidPackagesLocked() {
        ArraySet arraySet = new ArraySet();
        int size = this.mRunAnyRestrictedPackages.size();
        for (int i = 0; i < size; i++) {
            arraySet.add(this.mRunAnyRestrictedPackages.valueAt(i));
        }
        this.mBackgroundRestrictedUidPackages = Collections.unmodifiableSet(arraySet);
    }

    public final void updateForceAllAppStandbyState() {
        synchronized (this.mLock) {
            if (this.mForceAllAppStandbyForSmallBattery && isSmallBatteryDevice()) {
                toggleForceAllAppsStandbyLocked(!this.mIsPluggedIn);
            } else {
                toggleForceAllAppsStandbyLocked(this.mBatterySaverEnabled);
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void toggleForceAllAppsStandbyLocked(boolean z) {
        if (z == this.mForceAllAppsStandby) {
            return;
        }
        this.mForceAllAppsStandby = z;
        this.mHandler.notifyForceAllAppsStandbyChanged();
    }

    @GuardedBy({"mLock"})
    public final int findForcedAppStandbyUidPackageIndexLocked(int i, String str) {
        int size = this.mRunAnyRestrictedPackages.size();
        if (size > 8) {
            return this.mRunAnyRestrictedPackages.indexOf(Pair.create(Integer.valueOf(i), str));
        }
        for (int i2 = 0; i2 < size; i2++) {
            Pair<Integer, String> valueAt = this.mRunAnyRestrictedPackages.valueAt(i2);
            if (((Integer) valueAt.first).intValue() == i && str.equals(valueAt.second)) {
                return i2;
            }
        }
        return -1;
    }

    @GuardedBy({"mLock"})
    public boolean isRunAnyRestrictedLocked(int i, String str) {
        return findForcedAppStandbyUidPackageIndexLocked(i, str) >= 0;
    }

    @GuardedBy({"mLock"})
    public boolean updateForcedAppStandbyUidPackageLocked(int i, String str, boolean z) {
        int findForcedAppStandbyUidPackageIndexLocked = findForcedAppStandbyUidPackageIndexLocked(i, str);
        if ((findForcedAppStandbyUidPackageIndexLocked >= 0) == z) {
            return false;
        }
        if (z) {
            this.mRunAnyRestrictedPackages.add(Pair.create(Integer.valueOf(i), str));
        } else {
            this.mRunAnyRestrictedPackages.removeAt(findForcedAppStandbyUidPackageIndexLocked);
        }
        updateBackgroundRestrictedUidPackagesLocked();
        return true;
    }

    public static boolean addUidToArray(SparseBooleanArray sparseBooleanArray, int i) {
        if (UserHandle.isCore(i) || sparseBooleanArray.get(i)) {
            return false;
        }
        sparseBooleanArray.put(i, true);
        return true;
    }

    public static boolean removeUidFromArray(SparseBooleanArray sparseBooleanArray, int i, boolean z) {
        if (!UserHandle.isCore(i) && sparseBooleanArray.get(i)) {
            if (z) {
                sparseBooleanArray.delete(i);
                return true;
            }
            sparseBooleanArray.put(i, false);
            return true;
        }
        return false;
    }

    /* loaded from: classes.dex */
    public final class UidObserver extends IUidObserver.Stub {
        public void onUidProcAdjChanged(int i) {
        }

        public void onUidStateChanged(int i, int i2, long j, int i3) {
        }

        public UidObserver() {
        }

        public void onUidActive(int i) {
            AppStateTrackerImpl.this.mHandler.onUidActive(i);
        }

        public void onUidGone(int i, boolean z) {
            AppStateTrackerImpl.this.mHandler.onUidGone(i, z);
        }

        public void onUidIdle(int i, boolean z) {
            AppStateTrackerImpl.this.mHandler.onUidIdle(i, z);
        }

        public void onUidCachedChanged(int i, boolean z) {
            AppStateTrackerImpl.this.mHandler.onUidCachedChanged(i, z);
        }
    }

    /* loaded from: classes.dex */
    public final class AppOpsWatcher extends IAppOpsCallback.Stub {
        public AppOpsWatcher() {
        }

        public void opChanged(int i, int i2, String str) throws RemoteException {
            boolean z = false;
            try {
                if (AppStateTrackerImpl.this.mAppOpsService.checkOperation(70, i2, str) != 0) {
                    z = true;
                }
            } catch (RemoteException unused) {
            }
            synchronized (AppStateTrackerImpl.this.mLock) {
                if (AppStateTrackerImpl.this.updateForcedAppStandbyUidPackageLocked(i2, str, z)) {
                    AppStateTrackerImpl.this.mHandler.notifyRunAnyAppOpsChanged(i2, str);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public final class StandbyTracker extends AppStandbyInternal.AppIdleStateChangeListener {
        public StandbyTracker() {
        }

        public void onAppIdleStateChanged(String str, int i, boolean z, int i2, int i3) {
            boolean remove;
            synchronized (AppStateTrackerImpl.this.mLock) {
                if (i2 == 5) {
                    remove = AppStateTrackerImpl.this.mExemptedBucketPackages.add(i, str);
                } else {
                    remove = AppStateTrackerImpl.this.mExemptedBucketPackages.remove(i, str);
                }
                if (remove) {
                    AppStateTrackerImpl.this.mHandler.notifyExemptedBucketChanged();
                }
            }
        }
    }

    public final Listener[] cloneListeners() {
        Listener[] listenerArr;
        synchronized (this.mLock) {
            ArraySet<Listener> arraySet = this.mListeners;
            listenerArr = (Listener[]) arraySet.toArray(new Listener[arraySet.size()]);
        }
        return listenerArr;
    }

    /* loaded from: classes.dex */
    public class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        public void notifyUidActiveStateChanged(int i) {
            obtainMessage(0, i, 0).sendToTarget();
        }

        public void notifyRunAnyAppOpsChanged(int i, String str) {
            obtainMessage(3, i, 0, str).sendToTarget();
        }

        public void notifyAllUnexempted() {
            removeMessages(4);
            obtainMessage(4).sendToTarget();
        }

        public void notifyAllExemptionListChanged() {
            removeMessages(5);
            obtainMessage(5).sendToTarget();
        }

        public void notifyTempExemptionListChanged() {
            removeMessages(6);
            obtainMessage(6).sendToTarget();
        }

        public void notifyForceAllAppsStandbyChanged() {
            removeMessages(7);
            obtainMessage(7).sendToTarget();
        }

        public void notifyExemptedBucketChanged() {
            removeMessages(10);
            obtainMessage(10).sendToTarget();
        }

        public void notifyAutoRestrictedBucketFeatureFlagChanged(boolean z) {
            removeMessages(11);
            obtainMessage(11, z ? 1 : 0, 0).sendToTarget();
        }

        public void doUserRemoved(int i) {
            obtainMessage(8, i, 0).sendToTarget();
        }

        public void onUidActive(int i) {
            obtainMessage(12, i, 0).sendToTarget();
        }

        public void onUidGone(int i, boolean z) {
            obtainMessage(13, i, z ? 1 : 0).sendToTarget();
        }

        public void onUidIdle(int i, boolean z) {
            obtainMessage(14, i, z ? 1 : 0).sendToTarget();
        }

        public void onUidCachedChanged(int i, boolean z) {
            if (z) {
                obtainMessage(15, i, 0).sendToTarget();
            }
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 8) {
                AppStateTrackerImpl.this.handleUserRemoved(message.arg1);
                return;
            }
            synchronized (AppStateTrackerImpl.this.mLock) {
                AppStateTrackerImpl appStateTrackerImpl = AppStateTrackerImpl.this;
                if (appStateTrackerImpl.mStarted) {
                    long time = appStateTrackerImpl.mStatLogger.getTime();
                    int i = 0;
                    switch (message.what) {
                        case 0:
                            Listener[] cloneListeners = AppStateTrackerImpl.this.cloneListeners();
                            int length = cloneListeners.length;
                            while (i < length) {
                                cloneListeners[i].onUidActiveStateChanged(appStateTrackerImpl, message.arg1);
                                i++;
                            }
                            AppStateTrackerImpl.this.mStatLogger.logDurationStat(1, time);
                            return;
                        case 1:
                        case 2:
                        case 9:
                        default:
                            return;
                        case 3:
                            Listener[] cloneListeners2 = AppStateTrackerImpl.this.cloneListeners();
                            int length2 = cloneListeners2.length;
                            while (i < length2) {
                                cloneListeners2[i].onRunAnyAppOpsChanged(appStateTrackerImpl, message.arg1, (String) message.obj);
                                i++;
                            }
                            AppStateTrackerImpl.this.mStatLogger.logDurationStat(2, time);
                            return;
                        case 4:
                            Listener[] cloneListeners3 = AppStateTrackerImpl.this.cloneListeners();
                            int length3 = cloneListeners3.length;
                            while (i < length3) {
                                cloneListeners3[i].onPowerSaveUnexempted(appStateTrackerImpl);
                                i++;
                            }
                            AppStateTrackerImpl.this.mStatLogger.logDurationStat(3, time);
                            return;
                        case 5:
                            Listener[] cloneListeners4 = AppStateTrackerImpl.this.cloneListeners();
                            int length4 = cloneListeners4.length;
                            while (i < length4) {
                                cloneListeners4[i].onPowerSaveExemptionListChanged(appStateTrackerImpl);
                                i++;
                            }
                            AppStateTrackerImpl.this.mStatLogger.logDurationStat(4, time);
                            return;
                        case 6:
                            Listener[] cloneListeners5 = AppStateTrackerImpl.this.cloneListeners();
                            int length5 = cloneListeners5.length;
                            while (i < length5) {
                                cloneListeners5[i].onTempPowerSaveExemptionListChanged(appStateTrackerImpl);
                                i++;
                            }
                            AppStateTrackerImpl.this.mStatLogger.logDurationStat(5, time);
                            return;
                        case 7:
                            Listener[] cloneListeners6 = AppStateTrackerImpl.this.cloneListeners();
                            int length6 = cloneListeners6.length;
                            while (i < length6) {
                                cloneListeners6[i].onForceAllAppsStandbyChanged(appStateTrackerImpl);
                                i++;
                            }
                            AppStateTrackerImpl.this.mStatLogger.logDurationStat(7, time);
                            return;
                        case 8:
                            AppStateTrackerImpl.this.handleUserRemoved(message.arg1);
                            return;
                        case 10:
                            Listener[] cloneListeners7 = AppStateTrackerImpl.this.cloneListeners();
                            int length7 = cloneListeners7.length;
                            while (i < length7) {
                                cloneListeners7[i].onExemptedBucketChanged(appStateTrackerImpl);
                                i++;
                            }
                            AppStateTrackerImpl.this.mStatLogger.logDurationStat(6, time);
                            return;
                        case 11:
                            boolean z = message.arg1 == 1;
                            Listener[] cloneListeners8 = AppStateTrackerImpl.this.cloneListeners();
                            int length8 = cloneListeners8.length;
                            while (i < length8) {
                                cloneListeners8[i].onAutoRestrictedBucketFeatureFlagChanged(appStateTrackerImpl, z);
                                i++;
                            }
                            return;
                        case 12:
                            handleUidActive(message.arg1);
                            return;
                        case 13:
                            handleUidGone(message.arg1);
                            if (message.arg2 != 0) {
                                handleUidDisabled(message.arg1);
                                return;
                            }
                            return;
                        case 14:
                            handleUidIdle(message.arg1);
                            if (message.arg2 != 0) {
                                handleUidDisabled(message.arg1);
                                return;
                            }
                            return;
                        case 15:
                            handleUidCached(message.arg1);
                            return;
                    }
                }
            }
        }

        public final void handleUidCached(int i) {
            for (Listener listener : AppStateTrackerImpl.this.cloneListeners()) {
                listener.removeListenerAlarmsForCachedUid(i);
            }
        }

        public final void handleUidDisabled(int i) {
            for (Listener listener : AppStateTrackerImpl.this.cloneListeners()) {
                listener.removeAlarmsForUid(i);
            }
        }

        public void handleUidActive(int i) {
            synchronized (AppStateTrackerImpl.this.mLock) {
                if (AppStateTrackerImpl.addUidToArray(AppStateTrackerImpl.this.mActiveUids, i)) {
                    AppStateTrackerImpl.this.mHandler.notifyUidActiveStateChanged(i);
                }
            }
        }

        public void handleUidGone(int i) {
            removeUid(i, true);
        }

        public void handleUidIdle(int i) {
            removeUid(i, false);
        }

        public final void removeUid(int i, boolean z) {
            synchronized (AppStateTrackerImpl.this.mLock) {
                if (AppStateTrackerImpl.removeUidFromArray(AppStateTrackerImpl.this.mActiveUids, i, z)) {
                    AppStateTrackerImpl.this.mHandler.notifyUidActiveStateChanged(i);
                }
            }
        }
    }

    public void handleUserRemoved(int i) {
        synchronized (this.mLock) {
            for (int size = this.mRunAnyRestrictedPackages.size() - 1; size >= 0; size--) {
                if (UserHandle.getUserId(((Integer) this.mRunAnyRestrictedPackages.valueAt(size).first).intValue()) == i) {
                    this.mRunAnyRestrictedPackages.removeAt(size);
                }
            }
            updateBackgroundRestrictedUidPackagesLocked();
            cleanUpArrayForUser(this.mActiveUids, i);
            this.mExemptedBucketPackages.remove(i);
        }
    }

    public final void cleanUpArrayForUser(SparseBooleanArray sparseBooleanArray, int i) {
        for (int size = sparseBooleanArray.size() - 1; size >= 0; size--) {
            if (UserHandle.getUserId(sparseBooleanArray.keyAt(size)) == i) {
                sparseBooleanArray.removeAt(size);
            }
        }
    }

    public void setPowerSaveExemptionListAppIds(int[] iArr, int[] iArr2, int[] iArr3) {
        synchronized (this.mLock) {
            int[] iArr4 = this.mPowerExemptAllAppIds;
            int[] iArr5 = this.mTempExemptAppIds;
            this.mPowerExemptAllAppIds = iArr;
            this.mTempExemptAppIds = iArr3;
            this.mPowerExemptUserAppIds = iArr2;
            if (isAnyAppIdUnexempt(iArr4, iArr)) {
                this.mHandler.notifyAllUnexempted();
            } else if (!Arrays.equals(iArr4, this.mPowerExemptAllAppIds)) {
                this.mHandler.notifyAllExemptionListChanged();
            }
            if (!Arrays.equals(iArr5, this.mTempExemptAppIds)) {
                this.mHandler.notifyTempExemptionListChanged();
            }
        }
    }

    @VisibleForTesting
    public static boolean isAnyAppIdUnexempt(int[] iArr, int[] iArr2) {
        boolean z;
        boolean z2;
        int i = 0;
        int i2 = 0;
        while (true) {
            z = i >= iArr.length;
            z2 = i2 >= iArr2.length;
            if (z || z2) {
                break;
            }
            int i3 = iArr[i];
            int i4 = iArr2[i2];
            if (i3 == i4) {
                i++;
            } else if (i3 < i4) {
                return true;
            }
            i2++;
        }
        if (z) {
            return false;
        }
        return z2;
    }

    public void addListener(Listener listener) {
        synchronized (this.mLock) {
            this.mListeners.add(listener);
        }
    }

    public boolean areAlarmsRestricted(int i, String str) {
        boolean z = false;
        if (isUidActive(i)) {
            return false;
        }
        synchronized (this.mLock) {
            if (ArrayUtils.contains(this.mPowerExemptAllAppIds, UserHandle.getAppId(i))) {
                return false;
            }
            if (!this.mActivityManagerInternal.isBgAutoRestrictedBucketFeatureFlagEnabled() && isRunAnyRestrictedLocked(i, str)) {
                z = true;
            }
            return z;
        }
    }

    public boolean areAlarmsRestrictedByBatterySaver(int i, String str) {
        if (isUidActive(i)) {
            return false;
        }
        synchronized (this.mLock) {
            if (ArrayUtils.contains(this.mPowerExemptAllAppIds, UserHandle.getAppId(i))) {
                return false;
            }
            int userId = UserHandle.getUserId(i);
            if (this.mAppStandbyInternal.isAppIdleEnabled() && !this.mAppStandbyInternal.isInParole() && this.mExemptedBucketPackages.contains(userId, str)) {
                return false;
            }
            return this.mForceAllAppsStandby;
        }
    }

    public boolean areJobsRestricted(int i, String str, boolean z) {
        if (isUidActive(i)) {
            return false;
        }
        synchronized (this.mLock) {
            int appId = UserHandle.getAppId(i);
            if (!ArrayUtils.contains(this.mPowerExemptAllAppIds, appId) && !ArrayUtils.contains(this.mTempExemptAppIds, appId)) {
                if (this.mActivityManagerInternal.isBgAutoRestrictedBucketFeatureFlagEnabled() || !isRunAnyRestrictedLocked(i, str)) {
                    if (z) {
                        return false;
                    }
                    int userId = UserHandle.getUserId(i);
                    if (this.mAppStandbyInternal.isAppIdleEnabled() && !this.mAppStandbyInternal.isInParole() && this.mExemptedBucketPackages.contains(userId, str)) {
                        return false;
                    }
                    return this.mForceAllAppsStandby;
                }
                return true;
            }
            return false;
        }
    }

    public boolean isUidActive(int i) {
        boolean z;
        if (UserHandle.isCore(i)) {
            return true;
        }
        synchronized (this.mLock) {
            z = this.mActiveUids.get(i);
        }
        return z;
    }

    public boolean isUidActiveSynced(int i) {
        if (isUidActive(i)) {
            return true;
        }
        long time = this.mStatLogger.getTime();
        boolean isUidActive = this.mActivityManagerInternal.isUidActive(i);
        this.mStatLogger.logDurationStat(9, time);
        return isUidActive;
    }

    public boolean isForceAllAppsStandbyEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mForceAllAppsStandby;
        }
        return z;
    }

    public boolean isRunAnyInBackgroundAppOpsAllowed(int i, String str) {
        boolean z;
        synchronized (this.mLock) {
            z = !isRunAnyRestrictedLocked(i, str);
        }
        return z;
    }

    public boolean isUidPowerSaveExempt(int i) {
        boolean contains;
        synchronized (this.mLock) {
            contains = ArrayUtils.contains(this.mPowerExemptAllAppIds, UserHandle.getAppId(i));
        }
        return contains;
    }

    public boolean isUidPowerSaveUserExempt(int i) {
        boolean contains;
        synchronized (this.mLock) {
            contains = ArrayUtils.contains(this.mPowerExemptUserAppIds, UserHandle.getAppId(i));
        }
        return contains;
    }

    public boolean isUidTempPowerSaveExempt(int i) {
        boolean contains;
        synchronized (this.mLock) {
            contains = ArrayUtils.contains(this.mTempExemptAppIds, UserHandle.getAppId(i));
        }
        return contains;
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        synchronized (this.mLock) {
            indentingPrintWriter.println("Current AppStateTracker State:");
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.print("Force all apps standby: ");
            indentingPrintWriter.println(isForceAllAppsStandbyEnabled());
            indentingPrintWriter.print("Small Battery Device: ");
            indentingPrintWriter.println(isSmallBatteryDevice());
            indentingPrintWriter.print("Force all apps standby for small battery device: ");
            indentingPrintWriter.println(this.mForceAllAppStandbyForSmallBattery);
            indentingPrintWriter.print("Plugged In: ");
            indentingPrintWriter.println(this.mIsPluggedIn);
            indentingPrintWriter.print("Active uids: ");
            dumpUids(indentingPrintWriter, this.mActiveUids);
            indentingPrintWriter.print("Except-idle + user exemption list appids: ");
            indentingPrintWriter.println(Arrays.toString(this.mPowerExemptAllAppIds));
            indentingPrintWriter.print("User exemption list appids: ");
            indentingPrintWriter.println(Arrays.toString(this.mPowerExemptUserAppIds));
            indentingPrintWriter.print("Temp exemption list appids: ");
            indentingPrintWriter.println(Arrays.toString(this.mTempExemptAppIds));
            indentingPrintWriter.println("Exempted bucket packages:");
            indentingPrintWriter.increaseIndent();
            for (int i = 0; i < this.mExemptedBucketPackages.size(); i++) {
                indentingPrintWriter.print("User ");
                indentingPrintWriter.print(this.mExemptedBucketPackages.keyAt(i));
                indentingPrintWriter.println();
                indentingPrintWriter.increaseIndent();
                for (int i2 = 0; i2 < this.mExemptedBucketPackages.sizeAt(i); i2++) {
                    indentingPrintWriter.print((String) this.mExemptedBucketPackages.valueAt(i, i2));
                    indentingPrintWriter.println();
                }
                indentingPrintWriter.decreaseIndent();
            }
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println();
            indentingPrintWriter.println("Restricted packages:");
            indentingPrintWriter.increaseIndent();
            Iterator<Pair<Integer, String>> it = this.mRunAnyRestrictedPackages.iterator();
            while (it.hasNext()) {
                Pair<Integer, String> next = it.next();
                indentingPrintWriter.print(UserHandle.formatUid(((Integer) next.first).intValue()));
                indentingPrintWriter.print(" ");
                indentingPrintWriter.print((String) next.second);
                indentingPrintWriter.println();
            }
            indentingPrintWriter.decreaseIndent();
            this.mStatLogger.dump(indentingPrintWriter);
            indentingPrintWriter.decreaseIndent();
        }
    }

    public final void dumpUids(PrintWriter printWriter, SparseBooleanArray sparseBooleanArray) {
        printWriter.print("[");
        String str = "";
        for (int i = 0; i < sparseBooleanArray.size(); i++) {
            if (sparseBooleanArray.valueAt(i)) {
                printWriter.print(str);
                printWriter.print(UserHandle.formatUid(sparseBooleanArray.keyAt(i)));
                str = " ";
            }
        }
        printWriter.println("]");
    }

    public void dumpProto(ProtoOutputStream protoOutputStream, long j) {
        synchronized (this.mLock) {
            long start = protoOutputStream.start(j);
            protoOutputStream.write(1133871366145L, isForceAllAppsStandbyEnabled());
            protoOutputStream.write(1133871366150L, isSmallBatteryDevice());
            protoOutputStream.write(1133871366151L, this.mForceAllAppStandbyForSmallBattery);
            protoOutputStream.write(1133871366152L, this.mIsPluggedIn);
            for (int i = 0; i < this.mActiveUids.size(); i++) {
                if (this.mActiveUids.valueAt(i)) {
                    protoOutputStream.write(2220498092034L, this.mActiveUids.keyAt(i));
                }
            }
            for (int i2 : this.mPowerExemptAllAppIds) {
                protoOutputStream.write(2220498092035L, i2);
            }
            for (int i3 : this.mPowerExemptUserAppIds) {
                protoOutputStream.write(2220498092044L, i3);
            }
            for (int i4 : this.mTempExemptAppIds) {
                protoOutputStream.write(2220498092036L, i4);
            }
            for (int i5 = 0; i5 < this.mExemptedBucketPackages.size(); i5++) {
                for (int i6 = 0; i6 < this.mExemptedBucketPackages.sizeAt(i5); i6++) {
                    long start2 = protoOutputStream.start(2246267895818L);
                    protoOutputStream.write(1120986464257L, this.mExemptedBucketPackages.keyAt(i5));
                    protoOutputStream.write(1138166333442L, (String) this.mExemptedBucketPackages.valueAt(i5, i6));
                    protoOutputStream.end(start2);
                }
            }
            Iterator<Pair<Integer, String>> it = this.mRunAnyRestrictedPackages.iterator();
            while (it.hasNext()) {
                Pair<Integer, String> next = it.next();
                long start3 = protoOutputStream.start(2246267895813L);
                protoOutputStream.write(1120986464257L, ((Integer) next.first).intValue());
                protoOutputStream.write(1138166333442L, (String) next.second);
                protoOutputStream.end(start3);
            }
            this.mStatLogger.dumpProto(protoOutputStream, 1146756268041L);
            protoOutputStream.end(start);
        }
    }
}
