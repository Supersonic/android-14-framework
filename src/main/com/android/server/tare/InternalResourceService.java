package com.android.server.tare;

import android.app.AlarmManager;
import android.app.tare.EconomyManager;
import android.app.tare.IEconomyManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManagerInternal;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.BatteryManagerInternal;
import android.os.Binder;
import android.os.Handler;
import android.os.IDeviceIdleController;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArrayMap;
import android.util.SparseLongArray;
import android.util.SparseSetArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.IAppOpsCallback;
import com.android.internal.app.IAppOpsService;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.jobs.ArrayUtils;
import com.android.internal.util.jobs.DumpUtils;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.backup.BackupManagerConstants;
import com.android.server.clipboard.ClipboardService;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.tare.Agent;
import com.android.server.tare.EconomyManagerInternal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
/* loaded from: classes2.dex */
public class InternalResourceService extends SystemService {
    public static final boolean DEBUG = Log.isLoggable("TARE", 3);
    public final Agent mAgent;
    public final Analyst mAnalyst;
    public final IAppOpsCallback mApbListener;
    public IAppOpsService mAppOpsService;
    public final BatteryManagerInternal mBatteryManagerInternal;
    public volatile int mBootPhase;
    public final BroadcastReceiver mBroadcastReceiver;
    @GuardedBy({"mLock"})
    public CompleteEconomicPolicy mCompleteEconomicPolicy;
    public final ConfigObserver mConfigObserver;
    @GuardedBy({"mLock"})
    public int mCurrentBatteryLevel;
    public final int mDefaultTargetBackgroundBatteryLifeHours;
    public IDeviceIdleController mDeviceIdleController;
    public final EconomyManagerStub mEconomyManagerStub;
    public volatile int mEnabledMode;
    public volatile boolean mExemptListLoaded;
    @GuardedBy({"mLock"})
    public ArraySet<String> mExemptedApps;
    public final Handler mHandler;
    public volatile boolean mHasBattery;
    @GuardedBy({"mLock"})
    public final SparseArrayMap<String, ArraySet<String>> mInstallers;
    public final Object mLock;
    public final PackageManager mPackageManager;
    public final PackageManagerInternal mPackageManagerInternal;
    @GuardedBy({"mPackageToUidCache"})
    public final SparseArrayMap<String, Integer> mPackageToUidCache;
    @GuardedBy({"mLock"})
    public final SparseArrayMap<String, InstalledPackageInfo> mPkgCache;
    @GuardedBy({"mLock"})
    public final SparseSetArray<String> mRestrictedApps;
    public final Scribe mScribe;
    @GuardedBy({"mStateChangeListeners"})
    public final SparseSetArray<EconomyManagerInternal.TareStateChangeListener> mStateChangeListeners;
    public final UsageStatsManagerInternal.UsageEventListener mSurveillanceAgent;
    @GuardedBy({"mLock"})
    public int mTargetBackgroundBatteryLifeHours;
    @GuardedBy({"mLock"})
    public final SparseArrayMap<String, Long> mTemporaryVips;
    @GuardedBy({"mLock"})
    public final SparseSetArray<String> mUidToPackageCache;
    public final AlarmManager.OnAlarmListener mUnusedWealthReclamationListener;
    @GuardedBy({"mLock"})
    public final SparseArrayMap<String, Boolean> mVipOverrides;

    public InternalResourceService(Context context) {
        super(context);
        this.mLock = new Object();
        this.mPkgCache = new SparseArrayMap<>();
        this.mUidToPackageCache = new SparseSetArray<>();
        this.mPackageToUidCache = new SparseArrayMap<>();
        this.mStateChangeListeners = new SparseSetArray<>();
        this.mRestrictedApps = new SparseSetArray<>();
        this.mExemptedApps = new ArraySet<>();
        this.mVipOverrides = new SparseArrayMap<>();
        this.mTemporaryVips = new SparseArrayMap<>();
        this.mInstallers = new SparseArrayMap<>();
        this.mHasBattery = true;
        this.mApbListener = new IAppOpsCallback.Stub() { // from class: com.android.server.tare.InternalResourceService.1
            public void opChanged(int i, int i2, String str) {
                boolean z = false;
                try {
                    if (InternalResourceService.this.mAppOpsService.checkOperation(70, i2, str) != 0) {
                        z = true;
                    }
                } catch (RemoteException unused) {
                }
                int userId = UserHandle.getUserId(i2);
                synchronized (InternalResourceService.this.mLock) {
                    if (z) {
                        if (InternalResourceService.this.mRestrictedApps.add(userId, str)) {
                            InternalResourceService.this.mAgent.onAppRestrictedLocked(userId, str);
                        }
                    } else if (InternalResourceService.this.mRestrictedApps.remove(UserHandle.getUserId(i2), str)) {
                        InternalResourceService.this.mAgent.onAppUnrestrictedLocked(userId, str);
                    }
                }
            }
        };
        this.mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.tare.InternalResourceService.2
            public final String getPackageName(Intent intent) {
                Uri data = intent.getData();
                if (data != null) {
                    return data.getSchemeSpecificPart();
                }
                return null;
            }

            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                char c;
                String action = intent.getAction();
                action.hashCode();
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
                    case -757780528:
                        if (action.equals("android.intent.action.PACKAGE_RESTARTED")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case -625323454:
                        if (action.equals("android.intent.action.BATTERY_LEVEL_CHANGED")) {
                            c = 3;
                            break;
                        }
                        c = 65535;
                        break;
                    case -65633567:
                        if (action.equals("android.os.action.POWER_SAVE_WHITELIST_CHANGED")) {
                            c = 4;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1121780209:
                        if (action.equals("android.intent.action.USER_ADDED")) {
                            c = 5;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1544582882:
                        if (action.equals("android.intent.action.PACKAGE_ADDED")) {
                            c = 6;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1580442797:
                        if (action.equals("android.intent.action.PACKAGE_FULLY_REMOVED")) {
                            c = 7;
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
                        InternalResourceService.this.onUserRemoved(intent.getIntExtra("android.intent.extra.user_handle", 0));
                        return;
                    case 1:
                        boolean booleanExtra = intent.getBooleanExtra("present", InternalResourceService.this.mHasBattery);
                        if (InternalResourceService.this.mHasBattery != booleanExtra) {
                            InternalResourceService.this.mHasBattery = booleanExtra;
                            InternalResourceService.this.mConfigObserver.updateEnabledStatus();
                            return;
                        }
                        return;
                    case 2:
                        InternalResourceService.this.onPackageForceStopped(UserHandle.getUserId(intent.getIntExtra("android.intent.extra.UID", -1)), getPackageName(intent));
                        return;
                    case 3:
                        InternalResourceService.this.onBatteryLevelChanged();
                        return;
                    case 4:
                        InternalResourceService.this.onExemptionListChanged();
                        return;
                    case 5:
                        InternalResourceService.this.onUserAdded(intent.getIntExtra("android.intent.extra.user_handle", 0));
                        return;
                    case 6:
                        if (intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                            return;
                        }
                        InternalResourceService.this.onPackageAdded(intent.getIntExtra("android.intent.extra.UID", -1), getPackageName(intent));
                        return;
                    case 7:
                        InternalResourceService.this.onPackageRemoved(intent.getIntExtra("android.intent.extra.UID", -1), getPackageName(intent));
                        return;
                    default:
                        return;
                }
            }
        };
        this.mSurveillanceAgent = new UsageStatsManagerInternal.UsageEventListener() { // from class: com.android.server.tare.InternalResourceService.3
            @Override // android.app.usage.UsageStatsManagerInternal.UsageEventListener
            public void onUsageEvent(int i, UsageEvents.Event event) {
                InternalResourceService.this.mHandler.obtainMessage(2, i, 0, event).sendToTarget();
            }
        };
        this.mUnusedWealthReclamationListener = new AlarmManager.OnAlarmListener() { // from class: com.android.server.tare.InternalResourceService.4
            @Override // android.app.AlarmManager.OnAlarmListener
            public void onAlarm() {
                synchronized (InternalResourceService.this.mLock) {
                    InternalResourceService.this.mAgent.reclaimUnusedAssetsLocked(0.10000000149011612d, 259200000L, false);
                    InternalResourceService.this.mScribe.setLastReclamationTimeLocked(TareUtils.getCurrentTimeMillis());
                    InternalResourceService.this.scheduleUnusedWealthReclamationLocked();
                }
            }
        };
        IrsHandler irsHandler = new IrsHandler(TareHandlerThread.get().getLooper());
        this.mHandler = irsHandler;
        this.mBatteryManagerInternal = (BatteryManagerInternal) LocalServices.getService(BatteryManagerInternal.class);
        PackageManager packageManager = context.getPackageManager();
        this.mPackageManager = packageManager;
        this.mPackageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        this.mEconomyManagerStub = new EconomyManagerStub();
        Analyst analyst = new Analyst();
        this.mAnalyst = analyst;
        Scribe scribe = new Scribe(this, analyst);
        this.mScribe = scribe;
        this.mCompleteEconomicPolicy = new CompleteEconomicPolicy(this);
        this.mAgent = new Agent(this, scribe, analyst);
        this.mConfigObserver = new ConfigObserver(irsHandler, context);
        int i = packageManager.hasSystemFeature("android.hardware.type.watch") ? 100 : 40;
        this.mDefaultTargetBackgroundBatteryLifeHours = i;
        this.mTargetBackgroundBatteryLifeHours = i;
        publishLocalService(EconomyManagerInternal.class, new LocalService());
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("tare", this.mEconomyManagerStub);
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        this.mBootPhase = i;
        if (i == 500) {
            this.mAppOpsService = IAppOpsService.Stub.asInterface(ServiceManager.getService("appops"));
            this.mDeviceIdleController = IDeviceIdleController.Stub.asInterface(ServiceManager.getService("deviceidle"));
            this.mConfigObserver.start();
            onBootPhaseSystemServicesReady();
        } else if (i == 600) {
            onBootPhaseThirdPartyAppsCanStart();
        } else if (i != 1000) {
        } else {
            onBootPhaseBootCompleted();
        }
    }

    public Object getLock() {
        return this.mLock;
    }

    @GuardedBy({"mLock"})
    public CompleteEconomicPolicy getCompleteEconomicPolicyLocked() {
        return this.mCompleteEconomicPolicy;
    }

    public int getAppUpdateResponsibilityCount(int i, String str) {
        int size;
        synchronized (this.mLock) {
            size = ArrayUtils.size((Collection) this.mInstallers.get(i, str));
        }
        return size;
    }

    public SparseArrayMap<String, InstalledPackageInfo> getInstalledPackages() {
        SparseArrayMap<String, InstalledPackageInfo> sparseArrayMap;
        synchronized (this.mLock) {
            sparseArrayMap = this.mPkgCache;
        }
        return sparseArrayMap;
    }

    public List<InstalledPackageInfo> getInstalledPackages(int i) {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mLock) {
            int indexOfKey = this.mPkgCache.indexOfKey(i);
            if (indexOfKey < 0) {
                return arrayList;
            }
            for (int numElementsForKeyAt = this.mPkgCache.numElementsForKeyAt(indexOfKey) - 1; numElementsForKeyAt >= 0; numElementsForKeyAt--) {
                arrayList.add((InstalledPackageInfo) this.mPkgCache.valueAt(indexOfKey, numElementsForKeyAt));
            }
            return arrayList;
        }
    }

    public InstalledPackageInfo getInstalledPackageInfo(int i, String str) {
        InstalledPackageInfo installedPackageInfo;
        synchronized (this.mLock) {
            installedPackageInfo = (InstalledPackageInfo) this.mPkgCache.get(i, str);
        }
        return installedPackageInfo;
    }

    @GuardedBy({"mLock"})
    public long getConsumptionLimitLocked() {
        return (this.mCurrentBatteryLevel * this.mScribe.getSatiatedConsumptionLimitLocked()) / 100;
    }

    @GuardedBy({"mLock"})
    public long getMinBalanceLocked(int i, String str) {
        return (this.mCurrentBatteryLevel * this.mCompleteEconomicPolicy.getMinSatiatedBalance(i, str)) / 100;
    }

    @GuardedBy({"mLock"})
    public long getInitialSatiatedConsumptionLimitLocked() {
        return this.mCompleteEconomicPolicy.getInitialSatiatedConsumptionLimit();
    }

    public long getRealtimeSinceFirstSetupMs() {
        return this.mScribe.getRealtimeSinceFirstSetupMs(SystemClock.elapsedRealtime());
    }

    public int getUid(int i, String str) {
        int intValue;
        synchronized (this.mPackageToUidCache) {
            Integer num = (Integer) this.mPackageToUidCache.get(i, str);
            if (num == null) {
                num = Integer.valueOf(this.mPackageManagerInternal.getPackageUid(str, 0L, i));
                this.mPackageToUidCache.add(i, str, num);
            }
            intValue = num.intValue();
        }
        return intValue;
    }

    public int getEnabledMode() {
        return this.mEnabledMode;
    }

    public int getEnabledMode(int i) {
        synchronized (this.mLock) {
            if (this.mCompleteEconomicPolicy.isPolicyEnabled(i)) {
                return this.mEnabledMode;
            }
            return 0;
        }
    }

    public boolean isPackageExempted(int i, String str) {
        boolean contains;
        synchronized (this.mLock) {
            contains = this.mExemptedApps.contains(str);
        }
        return contains;
    }

    public boolean isPackageRestricted(int i, String str) {
        boolean contains;
        synchronized (this.mLock) {
            contains = this.mRestrictedApps.contains(i, str);
        }
        return contains;
    }

    public boolean isSystem(int i, String str) {
        if (PackageManagerShellCommandDataLoader.PACKAGE.equals(str)) {
            return true;
        }
        return UserHandle.isCore(getUid(i, str));
    }

    public boolean isVip(int i, String str) {
        return isVip(i, str, SystemClock.elapsedRealtime());
    }

    public boolean isVip(int i, String str, long j) {
        synchronized (this.mLock) {
            Boolean bool = (Boolean) this.mVipOverrides.get(i, str);
            if (bool != null) {
                return bool.booleanValue();
            }
            boolean z = true;
            if (isSystem(i, str)) {
                return true;
            }
            synchronized (this.mLock) {
                Long l = (Long) this.mTemporaryVips.get(i, str);
                if (l != null) {
                    if (j > l.longValue()) {
                        z = false;
                    }
                    return z;
                }
                return false;
            }
        }
    }

    public void onBatteryLevelChanged() {
        synchronized (this.mLock) {
            int currentBatteryLevel = getCurrentBatteryLevel();
            this.mAnalyst.noteBatteryLevelChange(currentBatteryLevel);
            int i = this.mCurrentBatteryLevel;
            boolean z = currentBatteryLevel > i;
            if (z) {
                if (currentBatteryLevel >= 80) {
                    maybeAdjustDesiredStockLevelLocked();
                }
                this.mAgent.distributeBasicIncomeLocked(currentBatteryLevel);
            } else if (currentBatteryLevel == i) {
                return;
            }
            this.mCurrentBatteryLevel = currentBatteryLevel;
            adjustCreditSupplyLocked(z);
        }
    }

    public void onDeviceStateChanged() {
        synchronized (this.mLock) {
            this.mAgent.onDeviceStateChangedLocked();
        }
    }

    public void onExemptionListChanged() {
        int[] userIds = ((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).getUserIds();
        synchronized (this.mLock) {
            ArraySet<String> arraySet = this.mExemptedApps;
            ArraySet arraySet2 = new ArraySet();
            try {
                this.mExemptedApps = new ArraySet<>(this.mDeviceIdleController.getFullPowerWhitelist());
                this.mExemptListLoaded = true;
            } catch (RemoteException unused) {
            }
            for (int size = this.mExemptedApps.size() - 1; size >= 0; size--) {
                String valueAt = this.mExemptedApps.valueAt(size);
                if (!arraySet.contains(valueAt)) {
                    arraySet2.add(valueAt);
                }
                arraySet.remove(valueAt);
            }
            for (int size2 = arraySet2.size() - 1; size2 >= 0; size2--) {
                String str = (String) arraySet2.valueAt(size2);
                for (int i : userIds) {
                    if (getUid(i, str) >= 0) {
                        this.mAgent.onAppExemptedLocked(i, str);
                    }
                }
            }
            for (int size3 = arraySet.size() - 1; size3 >= 0; size3--) {
                String valueAt2 = arraySet.valueAt(size3);
                for (int i2 : userIds) {
                    if (getUid(i2, valueAt2) >= 0) {
                        this.mAgent.onAppUnexemptedLocked(i2, valueAt2);
                    }
                }
            }
        }
    }

    public void onPackageAdded(int i, String str) {
        int userId = UserHandle.getUserId(i);
        try {
            PackageInfo packageInfoAsUser = this.mPackageManager.getPackageInfoAsUser(str, 1074532352, userId);
            synchronized (this.mPackageToUidCache) {
                this.mPackageToUidCache.add(userId, str, Integer.valueOf(i));
            }
            synchronized (this.mLock) {
                InstalledPackageInfo installedPackageInfo = new InstalledPackageInfo(getContext(), packageInfoAsUser);
                maybeUpdateInstallerStatusLocked((InstalledPackageInfo) this.mPkgCache.add(userId, str, installedPackageInfo), installedPackageInfo);
                this.mUidToPackageCache.add(i, str);
                this.mAgent.grantBirthrightLocked(userId, str);
                String str2 = installedPackageInfo.installerPackageName;
                if (str2 != null) {
                    this.mAgent.noteInstantaneousEventLocked(userId, str2, -1610612736, null);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Slog.wtf("TARE-IRS", "PM couldn't find newly added package: " + str, e);
        }
    }

    public void onPackageForceStopped(int i, String str) {
        synchronized (this.mLock) {
            this.mAgent.reclaimAllAssetsLocked(i, str, 8);
        }
    }

    public void onPackageRemoved(int i, String str) {
        String str2;
        ArraySet arraySet;
        int userId = UserHandle.getUserId(i);
        synchronized (this.mPackageToUidCache) {
            this.mPackageToUidCache.delete(userId, str);
        }
        synchronized (this.mLock) {
            this.mUidToPackageCache.remove(i, str);
            this.mVipOverrides.delete(userId, str);
            InstalledPackageInfo installedPackageInfo = (InstalledPackageInfo) this.mPkgCache.delete(userId, str);
            this.mInstallers.delete(userId, str);
            if (installedPackageInfo != null && (str2 = installedPackageInfo.installerPackageName) != null && (arraySet = (ArraySet) this.mInstallers.get(userId, str2)) != null) {
                arraySet.remove(str);
            }
            this.mAgent.onPackageRemovedLocked(userId, str);
        }
    }

    public void onUidStateChanged(int i) {
        synchronized (this.mLock) {
            ArraySet<String> packagesForUidLocked = getPackagesForUidLocked(i);
            if (packagesForUidLocked == null) {
                Slog.e("TARE-IRS", "Don't have packages for uid " + i);
            } else {
                this.mAgent.onAppStatesChangedLocked(UserHandle.getUserId(i), packagesForUidLocked);
            }
        }
    }

    public void onUserAdded(int i) {
        synchronized (this.mLock) {
            List installedPackagesAsUser = this.mPackageManager.getInstalledPackagesAsUser(1074532352, i);
            for (int size = installedPackagesAsUser.size() - 1; size >= 0; size--) {
                InstalledPackageInfo installedPackageInfo = new InstalledPackageInfo(getContext(), (PackageInfo) installedPackagesAsUser.get(size));
                maybeUpdateInstallerStatusLocked((InstalledPackageInfo) this.mPkgCache.add(i, installedPackageInfo.packageName, installedPackageInfo), installedPackageInfo);
            }
            this.mAgent.grantBirthrightsLocked(i);
            long elapsedRealtime = SystemClock.elapsedRealtime();
            this.mScribe.setUserAddedTimeLocked(i, elapsedRealtime);
            grantInstallersTemporaryVipStatusLocked(i, elapsedRealtime, 604800000L);
        }
    }

    public void onUserRemoved(int i) {
        synchronized (this.mLock) {
            this.mVipOverrides.delete(i);
            int indexOfKey = this.mPkgCache.indexOfKey(i);
            if (indexOfKey >= 0) {
                for (int numElementsForKeyAt = this.mPkgCache.numElementsForKeyAt(indexOfKey) - 1; numElementsForKeyAt >= 0; numElementsForKeyAt--) {
                    this.mUidToPackageCache.remove(((InstalledPackageInfo) this.mPkgCache.valueAt(indexOfKey, numElementsForKeyAt)).uid);
                }
            }
            this.mInstallers.delete(i);
            this.mPkgCache.delete(i);
            this.mAgent.onUserRemovedLocked(i);
            this.mScribe.onUserRemovedLocked(i);
        }
    }

    @GuardedBy({"mLock"})
    public void maybePerformQuantitativeEasingLocked() {
        if (this.mConfigObserver.ENABLE_TIP3) {
            maybeAdjustDesiredStockLevelLocked();
        } else if (getRealtimeSinceFirstSetupMs() < 432000000) {
        } else {
            long remainingConsumableCakesLocked = this.mScribe.getRemainingConsumableCakesLocked();
            if (this.mCurrentBatteryLevel <= 50 || remainingConsumableCakesLocked > 0) {
                return;
            }
            long satiatedConsumptionLimitLocked = this.mScribe.getSatiatedConsumptionLimitLocked();
            long min = Math.min((((this.mCurrentBatteryLevel - 50) * satiatedConsumptionLimitLocked) / 100) + satiatedConsumptionLimitLocked, this.mCompleteEconomicPolicy.getMaxSatiatedConsumptionLimit());
            if (min != satiatedConsumptionLimitLocked) {
                Slog.i("TARE-IRS", "Increasing consumption limit from " + TareUtils.cakeToString(satiatedConsumptionLimitLocked) + " to " + TareUtils.cakeToString(min));
                this.mScribe.setConsumptionLimitLocked(min);
                adjustCreditSupplyLocked(true);
            }
        }
    }

    @GuardedBy({"mLock"})
    public void maybeAdjustDesiredStockLevelLocked() {
        long max;
        if (this.mConfigObserver.ENABLE_TIP3 && getRealtimeSinceFirstSetupMs() >= 432000000) {
            long currentTimeMillis = TareUtils.getCurrentTimeMillis();
            if (currentTimeMillis - this.mScribe.getLastStockRecalculationTimeLocked() < 57600000 || this.mCurrentBatteryLevel <= 80) {
                return;
            }
            long batteryScreenOffDurationMs = this.mAnalyst.getBatteryScreenOffDurationMs();
            if (batteryScreenOffDurationMs < 28800000) {
                return;
            }
            long batteryScreenOffDischargeMah = this.mAnalyst.getBatteryScreenOffDischargeMah();
            if (batteryScreenOffDischargeMah == 0) {
                Slog.i("TARE-IRS", "Total discharge was 0");
                return;
            }
            long batteryFullCharge = this.mBatteryManagerInternal.getBatteryFullCharge() / 1000;
            long j = ((batteryFullCharge * batteryScreenOffDurationMs) / batteryScreenOffDischargeMah) / ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS;
            long j2 = (j * 100) / this.mTargetBackgroundBatteryLifeHours;
            if (DEBUG) {
                Slog.d("TARE-IRS", "maybeAdjustDesiredStockLevelLocked: screenOffMs=" + batteryScreenOffDurationMs + " dischargeMah=" + batteryScreenOffDischargeMah + " capacityMah=" + batteryFullCharge + " estimatedLifeHours=" + j + " %ofTarget=" + j2);
            }
            long satiatedConsumptionLimitLocked = this.mScribe.getSatiatedConsumptionLimitLocked();
            if (j2 > 105) {
                max = Math.min((long) (satiatedConsumptionLimitLocked * 1.01d), this.mCompleteEconomicPolicy.getMaxSatiatedConsumptionLimit());
            } else if (j2 >= 100) {
                return;
            } else {
                max = Math.max((long) (satiatedConsumptionLimitLocked * 0.98d), this.mCompleteEconomicPolicy.getMinSatiatedConsumptionLimit());
            }
            if (max != satiatedConsumptionLimitLocked) {
                Slog.i("TARE-IRS", "Adjusting consumption limit from " + TareUtils.cakeToString(satiatedConsumptionLimitLocked) + " to " + TareUtils.cakeToString(max) + " because drain was " + j2 + "% of target");
                this.mScribe.setConsumptionLimitLocked(max);
                adjustCreditSupplyLocked(true);
                this.mScribe.setLastStockRecalculationTimeLocked(currentTimeMillis);
            }
        }
    }

    public void postAffordabilityChanged(int i, String str, Agent.ActionAffordabilityNote actionAffordabilityNote) {
        if (DEBUG) {
            Slog.d("TARE-IRS", i + XmlUtils.STRING_ARRAY_SEPARATOR + str + " affordability changed to " + actionAffordabilityNote.isCurrentlyAffordable());
        }
        SomeArgs obtain = SomeArgs.obtain();
        obtain.argi1 = i;
        obtain.arg1 = str;
        obtain.arg2 = actionAffordabilityNote;
        this.mHandler.obtainMessage(0, obtain).sendToTarget();
    }

    @GuardedBy({"mLock"})
    public final void adjustCreditSupplyLocked(boolean z) {
        long consumptionLimitLocked = getConsumptionLimitLocked();
        long remainingConsumableCakesLocked = this.mScribe.getRemainingConsumableCakesLocked();
        int i = (remainingConsumableCakesLocked > consumptionLimitLocked ? 1 : (remainingConsumableCakesLocked == consumptionLimitLocked ? 0 : -1));
        if (i == 0) {
            return;
        }
        if (i > 0) {
            this.mScribe.adjustRemainingConsumableCakesLocked(consumptionLimitLocked - remainingConsumableCakesLocked);
        } else if (z) {
            this.mScribe.adjustRemainingConsumableCakesLocked((long) ((this.mCurrentBatteryLevel / 100.0d) * (consumptionLimitLocked - remainingConsumableCakesLocked)));
        }
        this.mAgent.onCreditSupplyChanged();
    }

    @GuardedBy({"mLock"})
    public final void grantInstallersTemporaryVipStatusLocked(int i, long j, long j2) {
        Long l;
        long j3 = j + j2;
        int indexOfKey = this.mPkgCache.indexOfKey(i);
        if (indexOfKey < 0) {
            return;
        }
        for (int numElementsForKey = this.mPkgCache.numElementsForKey(indexOfKey) - 1; numElementsForKey >= 0; numElementsForKey--) {
            InstalledPackageInfo installedPackageInfo = (InstalledPackageInfo) this.mPkgCache.valueAt(indexOfKey, numElementsForKey);
            if (installedPackageInfo.isSystemInstaller && ((l = (Long) this.mTemporaryVips.get(i, installedPackageInfo.packageName)) == null || l.longValue() < j3)) {
                this.mTemporaryVips.add(i, installedPackageInfo.packageName, Long.valueOf(j3));
            }
        }
        this.mHandler.sendEmptyMessageDelayed(5, j2);
    }

    @GuardedBy({"mLock"})
    public final void processUsageEventLocked(int i, UsageEvents.Event event) {
        if (this.mEnabledMode == 0) {
            return;
        }
        String packageName = event.getPackageName();
        if (DEBUG) {
            Slog.d("TARE-IRS", "Processing event " + event.getEventType() + " (" + event.mInstanceId + ") for " + TareUtils.appToString(i, packageName));
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        int eventType = event.getEventType();
        if (eventType == 1) {
            this.mAgent.noteOngoingEventLocked(i, packageName, -2147483646, String.valueOf(event.mInstanceId), elapsedRealtime);
            return;
        }
        if (eventType != 2) {
            if (eventType != 7) {
                if (eventType != 12) {
                    if (eventType != 9) {
                        if (eventType != 10) {
                            if (eventType != 23 && eventType != 24) {
                                return;
                            }
                        }
                    }
                }
                this.mAgent.noteInstantaneousEventLocked(i, packageName, Integer.MIN_VALUE, null);
                return;
            }
            this.mAgent.noteInstantaneousEventLocked(i, packageName, -2147483644, null);
            return;
        }
        this.mAgent.stopOngoingActionLocked(i, packageName, -2147483646, String.valueOf(event.mInstanceId), elapsedRealtime, TareUtils.getCurrentTimeMillis());
    }

    @GuardedBy({"mLock"})
    public final void scheduleUnusedWealthReclamationLocked() {
        final long currentTimeMillis = TareUtils.getCurrentTimeMillis();
        final long max = Math.max(30000 + currentTimeMillis, this.mScribe.getLastReclamationTimeLocked() + BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS);
        this.mHandler.post(new Runnable() { // from class: com.android.server.tare.InternalResourceService$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                InternalResourceService.this.lambda$scheduleUnusedWealthReclamationLocked$0(max, currentTimeMillis);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleUnusedWealthReclamationLocked$0(long j, long j2) {
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(AlarmManager.class);
        if (alarmManager != null) {
            alarmManager.setWindow(3, SystemClock.elapsedRealtime() + (j - j2), 1800000L, "*tare.reclamation*", this.mUnusedWealthReclamationListener, this.mHandler);
        } else {
            this.mHandler.sendEmptyMessageDelayed(1, 30000L);
        }
    }

    public final int getCurrentBatteryLevel() {
        return this.mBatteryManagerInternal.getBatteryLevel();
    }

    @GuardedBy({"mLock"})
    public final ArraySet<String> getPackagesForUidLocked(int i) {
        String[] packagesForUid;
        ArraySet<String> arraySet = this.mUidToPackageCache.get(i);
        if (arraySet != null || (packagesForUid = this.mPackageManager.getPackagesForUid(i)) == null) {
            return arraySet;
        }
        for (String str : packagesForUid) {
            this.mUidToPackageCache.add(i, str);
        }
        return this.mUidToPackageCache.get(i);
    }

    public final boolean isTareSupported() {
        return this.mHasBattery;
    }

    @GuardedBy({"mLock"})
    public final void loadInstalledPackageListLocked() {
        int[] userIds;
        this.mPkgCache.clear();
        for (int i : ((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).getUserIds()) {
            List installedPackagesAsUser = this.mPackageManager.getInstalledPackagesAsUser(1074532352, i);
            for (int size = installedPackagesAsUser.size() - 1; size >= 0; size--) {
                InstalledPackageInfo installedPackageInfo = new InstalledPackageInfo(getContext(), (PackageInfo) installedPackagesAsUser.get(size));
                maybeUpdateInstallerStatusLocked((InstalledPackageInfo) this.mPkgCache.add(i, installedPackageInfo.packageName, installedPackageInfo), installedPackageInfo);
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void maybeUpdateInstallerStatusLocked(InstalledPackageInfo installedPackageInfo, InstalledPackageInfo installedPackageInfo2) {
        ArraySet arraySet;
        boolean z = true;
        if (installedPackageInfo == null) {
            if (installedPackageInfo2.installerPackageName == null) {
                z = false;
            }
        } else {
            z = true ^ Objects.equals(installedPackageInfo.installerPackageName, installedPackageInfo2.installerPackageName);
        }
        if (z) {
            int userId = UserHandle.getUserId(installedPackageInfo2.uid);
            String str = installedPackageInfo2.packageName;
            if (installedPackageInfo != null && (arraySet = (ArraySet) this.mInstallers.get(userId, installedPackageInfo.installerPackageName)) != null) {
                arraySet.remove(str);
            }
            String str2 = installedPackageInfo2.installerPackageName;
            if (str2 != null) {
                ArraySet arraySet2 = (ArraySet) this.mInstallers.get(userId, str2);
                if (arraySet2 == null) {
                    arraySet2 = new ArraySet();
                    this.mInstallers.add(userId, installedPackageInfo2.installerPackageName, arraySet2);
                }
                arraySet2.add(str);
            }
        }
    }

    public final void registerListeners() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        intentFilter.addAction("android.intent.action.BATTERY_LEVEL_CHANGED");
        intentFilter.addAction("android.os.action.POWER_SAVE_WHITELIST_CHANGED");
        getContext().registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, intentFilter, null, null);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.PACKAGE_FULLY_REMOVED");
        intentFilter2.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter2.addAction("android.intent.action.PACKAGE_RESTARTED");
        intentFilter2.addDataScheme("package");
        getContext().registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, intentFilter2, null, null);
        IntentFilter intentFilter3 = new IntentFilter("android.intent.action.USER_REMOVED");
        intentFilter3.addAction("android.intent.action.USER_ADDED");
        getContext().registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, intentFilter3, null, null);
        ((UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class)).registerListener(this.mSurveillanceAgent);
        try {
            this.mAppOpsService.startWatchingMode(70, (String) null, this.mApbListener);
        } catch (RemoteException unused) {
        }
    }

    public final void setupHeavyWork() {
        SparseLongArray realtimeSinceUsersAddedLocked;
        int[] userIds;
        if (this.mBootPhase < 600 || this.mEnabledMode == 0) {
            return;
        }
        synchronized (this.mLock) {
            this.mCompleteEconomicPolicy.setup(this.mConfigObserver.getAllDeviceConfigProperties());
            loadInstalledPackageListLocked();
            boolean z = !this.mScribe.recordExists();
            long elapsedRealtime = SystemClock.elapsedRealtime();
            if (z) {
                this.mAgent.grantBirthrightsLocked();
                this.mScribe.setConsumptionLimitLocked(this.mCompleteEconomicPolicy.getInitialSatiatedConsumptionLimit());
                this.mScribe.setLastReclamationTimeLocked(TareUtils.getCurrentTimeMillis());
                realtimeSinceUsersAddedLocked = new SparseLongArray();
            } else {
                this.mScribe.loadFromDiskLocked();
                if (this.mScribe.getSatiatedConsumptionLimitLocked() >= this.mCompleteEconomicPolicy.getMinSatiatedConsumptionLimit() && this.mScribe.getSatiatedConsumptionLimitLocked() <= this.mCompleteEconomicPolicy.getMaxSatiatedConsumptionLimit()) {
                    adjustCreditSupplyLocked(true);
                    realtimeSinceUsersAddedLocked = this.mScribe.getRealtimeSinceUsersAddedLocked(elapsedRealtime);
                }
                this.mScribe.setConsumptionLimitLocked(this.mCompleteEconomicPolicy.getInitialSatiatedConsumptionLimit());
                realtimeSinceUsersAddedLocked = this.mScribe.getRealtimeSinceUsersAddedLocked(elapsedRealtime);
            }
            for (int i : ((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).getUserIds()) {
                long j = realtimeSinceUsersAddedLocked.get(i, 0L);
                if (j < 604800000) {
                    grantInstallersTemporaryVipStatusLocked(i, elapsedRealtime, 604800000 - j);
                }
            }
            scheduleUnusedWealthReclamationLocked();
        }
    }

    public final void onBootPhaseSystemServicesReady() {
        boolean booleanExtra;
        if (this.mBootPhase < 500 || this.mEnabledMode == 0) {
            return;
        }
        synchronized (this.mLock) {
            registerListeners();
            this.mCurrentBatteryLevel = getCurrentBatteryLevel();
            Intent registerReceiver = getContext().registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
            if (registerReceiver != null && this.mHasBattery != (booleanExtra = registerReceiver.getBooleanExtra("present", true))) {
                this.mHasBattery = booleanExtra;
                this.mConfigObserver.updateEnabledStatus();
            }
        }
    }

    public final void onBootPhaseThirdPartyAppsCanStart() {
        if (this.mBootPhase < 600 || this.mEnabledMode == 0) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.android.server.tare.InternalResourceService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                InternalResourceService.this.setupHeavyWork();
            }
        });
    }

    public final void onBootPhaseBootCompleted() {
        if (this.mBootPhase < 1000 || this.mEnabledMode == 0) {
            return;
        }
        synchronized (this.mLock) {
            if (!this.mExemptListLoaded) {
                try {
                    this.mExemptedApps = new ArraySet<>(this.mDeviceIdleController.getFullPowerWhitelist());
                    this.mExemptListLoaded = true;
                } catch (RemoteException unused) {
                }
            }
        }
    }

    public final void setupEverything() {
        if (this.mEnabledMode == 0) {
            return;
        }
        if (this.mBootPhase >= 500) {
            onBootPhaseSystemServicesReady();
        }
        if (this.mBootPhase >= 600) {
            onBootPhaseThirdPartyAppsCanStart();
        }
        if (this.mBootPhase >= 1000) {
            onBootPhaseBootCompleted();
        }
    }

    public final void tearDownEverything() {
        if (this.mEnabledMode != 0) {
            return;
        }
        synchronized (this.mLock) {
            this.mAgent.tearDownLocked();
            this.mAnalyst.tearDown();
            this.mCompleteEconomicPolicy.tearDown();
            this.mExemptedApps.clear();
            this.mExemptListLoaded = false;
            this.mHandler.post(new Runnable() { // from class: com.android.server.tare.InternalResourceService$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    InternalResourceService.this.lambda$tearDownEverything$1();
                }
            });
            this.mPkgCache.clear();
            this.mScribe.tearDownLocked();
            this.mUidToPackageCache.clear();
            getContext().unregisterReceiver(this.mBroadcastReceiver);
            ((UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class)).unregisterListener(this.mSurveillanceAgent);
            try {
                this.mAppOpsService.stopWatchingMode(this.mApbListener);
            } catch (RemoteException unused) {
            }
        }
        synchronized (this.mPackageToUidCache) {
            this.mPackageToUidCache.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$tearDownEverything$1() {
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(AlarmManager.class);
        if (alarmManager != null) {
            alarmManager.cancel(this.mUnusedWealthReclamationListener);
        }
    }

    /* loaded from: classes2.dex */
    public final class IrsHandler extends Handler {
        public IrsHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                SomeArgs someArgs = (SomeArgs) message.obj;
                Agent.ActionAffordabilityNote actionAffordabilityNote = (Agent.ActionAffordabilityNote) someArgs.arg2;
                actionAffordabilityNote.getListener().onAffordabilityChanged(someArgs.argi1, (String) someArgs.arg1, actionAffordabilityNote.getActionBill(), actionAffordabilityNote.isCurrentlyAffordable());
                someArgs.recycle();
            } else if (i == 1) {
                removeMessages(1);
                synchronized (InternalResourceService.this.mLock) {
                    InternalResourceService.this.scheduleUnusedWealthReclamationLocked();
                }
            } else if (i == 2) {
                int i2 = message.arg1;
                UsageEvents.Event event = (UsageEvents.Event) message.obj;
                synchronized (InternalResourceService.this.mLock) {
                    InternalResourceService.this.processUsageEventLocked(i2, event);
                }
            } else {
                int i3 = 0;
                if (i == 3) {
                    int i4 = message.arg1;
                    synchronized (InternalResourceService.this.mStateChangeListeners) {
                        int size = InternalResourceService.this.mStateChangeListeners.size();
                        while (i3 < size) {
                            int keyAt = InternalResourceService.this.mStateChangeListeners.keyAt(i3);
                            if ((keyAt & i4) != 0) {
                                ArraySet arraySet = InternalResourceService.this.mStateChangeListeners.get(keyAt);
                                int enabledMode = InternalResourceService.this.getEnabledMode(keyAt);
                                for (int size2 = arraySet.size() - 1; size2 >= 0; size2--) {
                                    ((EconomyManagerInternal.TareStateChangeListener) arraySet.valueAt(size2)).onTareEnabledModeChanged(enabledMode);
                                }
                            }
                            i3++;
                        }
                    }
                } else if (i == 4) {
                    ((EconomyManagerInternal.TareStateChangeListener) message.obj).onTareEnabledModeChanged(InternalResourceService.this.getEnabledMode(message.arg1));
                } else if (i == 5) {
                    removeMessages(5);
                    synchronized (InternalResourceService.this.mLock) {
                        long elapsedRealtime = SystemClock.elapsedRealtime();
                        long j = Long.MAX_VALUE;
                        while (i3 < InternalResourceService.this.mTemporaryVips.numMaps()) {
                            int keyAt2 = InternalResourceService.this.mTemporaryVips.keyAt(i3);
                            for (int numElementsForKeyAt = InternalResourceService.this.mTemporaryVips.numElementsForKeyAt(i3) - 1; numElementsForKeyAt >= 0; numElementsForKeyAt--) {
                                String str = (String) InternalResourceService.this.mTemporaryVips.keyAt(i3, numElementsForKeyAt);
                                Long l = (Long) InternalResourceService.this.mTemporaryVips.valueAt(i3, numElementsForKeyAt);
                                if (l != null && l.longValue() >= elapsedRealtime) {
                                    j = Math.min(j, l.longValue());
                                }
                                InternalResourceService.this.mTemporaryVips.delete(keyAt2, str);
                            }
                            i3++;
                        }
                        if (j < Long.MAX_VALUE) {
                            sendEmptyMessageDelayed(5, j - elapsedRealtime);
                        }
                    }
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public final class EconomyManagerStub extends IEconomyManager.Stub {
        public EconomyManagerStub() {
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            boolean z;
            long clearCallingIdentity;
            if (!DumpUtils.checkDumpAndUsageStatsPermission(InternalResourceService.this.getContext(), "TARE-IRS", printWriter)) {
                return;
            }
            try {
                if (!ArrayUtils.isEmpty(strArr)) {
                    z = false;
                    String str = strArr[0];
                    if ("-h".equals(str) || "--help".equals(str)) {
                        InternalResourceService.dumpHelp(printWriter);
                        return;
                    }
                    if (!"-a".equals(str)) {
                        if (str.length() > 0 && str.charAt(0) == '-') {
                            printWriter.println("Unknown option: " + str);
                            return;
                        }
                    }
                    clearCallingIdentity = Binder.clearCallingIdentity();
                    InternalResourceService.this.dumpInternal(new IndentingPrintWriter(printWriter, "  "), z);
                    return;
                }
                InternalResourceService.this.dumpInternal(new IndentingPrintWriter(printWriter, "  "), z);
                return;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
            z = true;
            clearCallingIdentity = Binder.clearCallingIdentity();
        }

        /* JADX WARN: Multi-variable type inference failed */
        public int handleShellCommand(ParcelFileDescriptor parcelFileDescriptor, ParcelFileDescriptor parcelFileDescriptor2, ParcelFileDescriptor parcelFileDescriptor3, String[] strArr) {
            return new TareShellCommand(InternalResourceService.this).exec(this, parcelFileDescriptor.getFileDescriptor(), parcelFileDescriptor2.getFileDescriptor(), parcelFileDescriptor3.getFileDescriptor(), strArr);
        }
    }

    /* loaded from: classes2.dex */
    public final class LocalService implements EconomyManagerInternal {
        public LocalService() {
        }

        @Override // com.android.server.tare.EconomyManagerInternal
        public void registerAffordabilityChangeListener(int i, String str, EconomyManagerInternal.AffordabilityChangeListener affordabilityChangeListener, EconomyManagerInternal.ActionBill actionBill) {
            if (!InternalResourceService.this.isTareSupported() || InternalResourceService.this.isSystem(i, str)) {
                return;
            }
            synchronized (InternalResourceService.this.mLock) {
                InternalResourceService.this.mAgent.registerAffordabilityChangeListenerLocked(i, str, affordabilityChangeListener, actionBill);
            }
        }

        @Override // com.android.server.tare.EconomyManagerInternal
        public void unregisterAffordabilityChangeListener(int i, String str, EconomyManagerInternal.AffordabilityChangeListener affordabilityChangeListener, EconomyManagerInternal.ActionBill actionBill) {
            if (InternalResourceService.this.isSystem(i, str)) {
                return;
            }
            synchronized (InternalResourceService.this.mLock) {
                InternalResourceService.this.mAgent.unregisterAffordabilityChangeListenerLocked(i, str, affordabilityChangeListener, actionBill);
            }
        }

        @Override // com.android.server.tare.EconomyManagerInternal
        public void registerTareStateChangeListener(EconomyManagerInternal.TareStateChangeListener tareStateChangeListener, int i) {
            if (InternalResourceService.this.isTareSupported()) {
                synchronized (InternalResourceService.this.mStateChangeListeners) {
                    if (InternalResourceService.this.mStateChangeListeners.add(i, tareStateChangeListener)) {
                        InternalResourceService.this.mHandler.obtainMessage(4, i, 0, tareStateChangeListener).sendToTarget();
                    }
                }
            }
        }

        @Override // com.android.server.tare.EconomyManagerInternal
        public boolean canPayFor(int i, String str, EconomyManagerInternal.ActionBill actionBill) {
            Object obj;
            EconomyManagerInternal.AnticipatedAction anticipatedAction;
            long j;
            if (InternalResourceService.this.mEnabledMode == 0 || InternalResourceService.this.isVip(i, str)) {
                return true;
            }
            List<EconomyManagerInternal.AnticipatedAction> anticipatedActions = actionBill.getAnticipatedActions();
            Object obj2 = InternalResourceService.this.mLock;
            synchronized (obj2) {
                long j2 = 0;
                int i2 = 0;
                while (i2 < anticipatedActions.size()) {
                    try {
                        anticipatedAction = anticipatedActions.get(i2);
                        j = InternalResourceService.this.mCompleteEconomicPolicy.getCostOfAction(anticipatedAction.actionId, i, str).price;
                        obj = obj2;
                    } catch (Throwable th) {
                        th = th;
                        obj = obj2;
                    }
                    try {
                        j2 += (anticipatedAction.numInstantaneousCalls * j) + (j * (anticipatedAction.ongoingDurationMs / 1000));
                        i2++;
                        obj2 = obj;
                    } catch (Throwable th2) {
                        th = th2;
                        throw th;
                    }
                }
                Object obj3 = obj2;
                boolean z = InternalResourceService.this.mAgent.getBalanceLocked(i, str) >= j2 && InternalResourceService.this.mScribe.getRemainingConsumableCakesLocked() >= j2;
                return z;
            }
        }

        @Override // com.android.server.tare.EconomyManagerInternal
        public long getMaxDurationMs(int i, String str, EconomyManagerInternal.ActionBill actionBill) {
            if (InternalResourceService.this.mEnabledMode == 0 || InternalResourceService.this.isVip(i, str)) {
                return 851472000000L;
            }
            List<EconomyManagerInternal.AnticipatedAction> anticipatedActions = actionBill.getAnticipatedActions();
            synchronized (InternalResourceService.this.mLock) {
                long j = 0;
                for (int i2 = 0; i2 < anticipatedActions.size(); i2++) {
                    j += InternalResourceService.this.mCompleteEconomicPolicy.getCostOfAction(anticipatedActions.get(i2).actionId, i, str).price;
                }
                if (j == 0) {
                    return 851472000000L;
                }
                return (Math.min(InternalResourceService.this.mAgent.getBalanceLocked(i, str), InternalResourceService.this.mScribe.getRemainingConsumableCakesLocked()) * 1000) / j;
            }
        }

        @Override // com.android.server.tare.EconomyManagerInternal
        public int getEnabledMode(int i) {
            return InternalResourceService.this.getEnabledMode(i);
        }

        @Override // com.android.server.tare.EconomyManagerInternal
        public void noteInstantaneousEvent(int i, String str, int i2, String str2) {
            if (InternalResourceService.this.mEnabledMode == 0) {
                return;
            }
            synchronized (InternalResourceService.this.mLock) {
                InternalResourceService.this.mAgent.noteInstantaneousEventLocked(i, str, i2, str2);
            }
        }

        @Override // com.android.server.tare.EconomyManagerInternal
        public void noteOngoingEventStarted(int i, String str, int i2, String str2) {
            if (InternalResourceService.this.mEnabledMode == 0) {
                return;
            }
            synchronized (InternalResourceService.this.mLock) {
                InternalResourceService.this.mAgent.noteOngoingEventLocked(i, str, i2, str2, SystemClock.elapsedRealtime());
            }
        }

        @Override // com.android.server.tare.EconomyManagerInternal
        public void noteOngoingEventStopped(int i, String str, int i2, String str2) {
            if (InternalResourceService.this.mEnabledMode == 0) {
                return;
            }
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long currentTimeMillis = TareUtils.getCurrentTimeMillis();
            synchronized (InternalResourceService.this.mLock) {
                InternalResourceService.this.mAgent.stopOngoingActionLocked(i, str, i2, str2, elapsedRealtime, currentTimeMillis);
            }
        }
    }

    /* loaded from: classes2.dex */
    public class ConfigObserver extends ContentObserver implements DeviceConfig.OnPropertiesChangedListener {
        public boolean ENABLE_TIP3;
        public final ContentResolver mContentResolver;

        public ConfigObserver(Handler handler, Context context) {
            super(handler);
            this.ENABLE_TIP3 = true;
            this.mContentResolver = context.getContentResolver();
        }

        public void start() {
            DeviceConfig.addOnPropertiesChangedListener("tare", TareHandlerThread.getExecutor(), this);
            this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("enable_tare"), false, this);
            this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("tare_alarm_manager_constants"), false, this);
            this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("tare_job_scheduler_constants"), false, this);
            onPropertiesChanged(getAllDeviceConfigProperties());
            updateEnabledStatus();
        }

        public DeviceConfig.Properties getAllDeviceConfigProperties() {
            return DeviceConfig.getProperties("tare", new String[0]);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            if (uri.equals(Settings.Global.getUriFor("enable_tare"))) {
                updateEnabledStatus();
            } else if (uri.equals(Settings.Global.getUriFor("tare_alarm_manager_constants")) || uri.equals(Settings.Global.getUriFor("tare_job_scheduler_constants"))) {
                updateEconomicPolicy();
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:57:0x00a2 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:67:0x0058 A[SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void onPropertiesChanged(DeviceConfig.Properties properties) {
            char c;
            synchronized (InternalResourceService.this.mLock) {
                boolean z = false;
                for (String str : properties.getKeyset()) {
                    if (str != null) {
                        int hashCode = str.hashCode();
                        if (hashCode == -1428824012) {
                            if (str.equals("enable_tip3")) {
                                c = 1;
                                if (c != 0) {
                                }
                            }
                            c = 65535;
                            if (c != 0) {
                            }
                        } else if (hashCode != -300584602) {
                            if (hashCode == 1536945124 && str.equals("target_bg_battery_life_hrs")) {
                                c = 2;
                                if (c != 0) {
                                    updateEnabledStatus();
                                } else if (c == 1) {
                                    this.ENABLE_TIP3 = properties.getBoolean(str, true);
                                } else if (c == 2) {
                                    synchronized (InternalResourceService.this.mLock) {
                                        InternalResourceService internalResourceService = InternalResourceService.this;
                                        internalResourceService.mTargetBackgroundBatteryLifeHours = properties.getInt(str, internalResourceService.mDefaultTargetBackgroundBatteryLifeHours);
                                        InternalResourceService.this.maybeAdjustDesiredStockLevelLocked();
                                    }
                                } else if (!z && (str.startsWith("am") || str.startsWith("js") || str.startsWith("enable_policy"))) {
                                    updateEconomicPolicy();
                                    z = true;
                                }
                            }
                            c = 65535;
                            if (c != 0) {
                            }
                        } else {
                            if (str.equals("enable_tare_mode")) {
                                c = 0;
                                if (c != 0) {
                                }
                            }
                            c = 65535;
                            if (c != 0) {
                            }
                        }
                    }
                }
            }
        }

        public final void updateEnabledStatus() {
            int i = InternalResourceService.this.isTareSupported() ? Settings.Global.getInt(this.mContentResolver, "enable_tare", DeviceConfig.getInt("tare", "enable_tare_mode", 0)) : 0;
            boolean z = true;
            if (i != 0 && i != 1 && i != 2) {
                i = 0;
            }
            if (InternalResourceService.this.mEnabledMode != i) {
                if (InternalResourceService.this.mEnabledMode != 0 && i != 0) {
                    z = false;
                }
                InternalResourceService.this.mEnabledMode = i;
                if (z) {
                    if (InternalResourceService.this.mEnabledMode != 0) {
                        InternalResourceService.this.setupEverything();
                    } else {
                        InternalResourceService.this.tearDownEverything();
                    }
                }
                InternalResourceService.this.mHandler.obtainMessage(3, 805306368, 0).sendToTarget();
            }
        }

        public final void updateEconomicPolicy() {
            synchronized (InternalResourceService.this.mLock) {
                long minSatiatedConsumptionLimit = InternalResourceService.this.mCompleteEconomicPolicy.getMinSatiatedConsumptionLimit();
                long maxSatiatedConsumptionLimit = InternalResourceService.this.mCompleteEconomicPolicy.getMaxSatiatedConsumptionLimit();
                int enabledPolicyIds = InternalResourceService.this.mCompleteEconomicPolicy.getEnabledPolicyIds();
                InternalResourceService.this.mCompleteEconomicPolicy.tearDown();
                InternalResourceService.this.mCompleteEconomicPolicy = new CompleteEconomicPolicy(InternalResourceService.this);
                if (InternalResourceService.this.mEnabledMode != 0 && InternalResourceService.this.mBootPhase >= 600) {
                    InternalResourceService.this.mCompleteEconomicPolicy.setup(getAllDeviceConfigProperties());
                    if (minSatiatedConsumptionLimit != InternalResourceService.this.mCompleteEconomicPolicy.getMinSatiatedConsumptionLimit() || maxSatiatedConsumptionLimit != InternalResourceService.this.mCompleteEconomicPolicy.getMaxSatiatedConsumptionLimit()) {
                        InternalResourceService.this.mScribe.setConsumptionLimitLocked(InternalResourceService.this.mCompleteEconomicPolicy.getInitialSatiatedConsumptionLimit());
                    }
                    InternalResourceService.this.mAgent.onPricingChangedLocked();
                    int enabledPolicyIds2 = InternalResourceService.this.mCompleteEconomicPolicy.getEnabledPolicyIds();
                    if (enabledPolicyIds != enabledPolicyIds2) {
                        InternalResourceService.this.mHandler.obtainMessage(3, enabledPolicyIds2 ^ enabledPolicyIds, 0).sendToTarget();
                    }
                }
            }
        }
    }

    public int executeClearVip(PrintWriter printWriter) {
        synchronized (this.mLock) {
            SparseSetArray<String> sparseSetArray = new SparseSetArray<>();
            for (int numMaps = this.mVipOverrides.numMaps() - 1; numMaps >= 0; numMaps--) {
                int keyAt = this.mVipOverrides.keyAt(numMaps);
                for (int numElementsForKeyAt = this.mVipOverrides.numElementsForKeyAt(numMaps) - 1; numElementsForKeyAt >= 0; numElementsForKeyAt--) {
                    sparseSetArray.add(keyAt, (String) this.mVipOverrides.keyAt(numMaps, numElementsForKeyAt));
                }
            }
            this.mVipOverrides.clear();
            if (this.mEnabledMode != 0) {
                this.mAgent.onVipStatusChangedLocked(sparseSetArray);
            }
        }
        printWriter.println("Cleared all VIP statuses");
        return 0;
    }

    public int executeSetVip(PrintWriter printWriter, int i, String str, Boolean bool) {
        boolean z;
        synchronized (this.mLock) {
            boolean isVip = isVip(i, str);
            if (bool == null) {
                this.mVipOverrides.delete(i, str);
            } else {
                this.mVipOverrides.add(i, str, bool);
            }
            z = isVip(i, str) != isVip;
            if (this.mEnabledMode != 0 && z) {
                this.mAgent.onVipStatusChangedLocked(i, str);
            }
        }
        printWriter.println(TareUtils.appToString(i, str) + " VIP status set to " + bool + ". Final VIP state changed? " + z);
        return 0;
    }

    public static void dumpHelp(PrintWriter printWriter) {
        printWriter.println("Resource Economy (economy) dump options:");
        printWriter.println("  [-h|--help] [package] ...");
        printWriter.println("    -h | --help: print this help");
        printWriter.println("  [package] is an optional package name to limit the output to.");
    }

    public final void dumpInternal(IndentingPrintWriter indentingPrintWriter, boolean z) {
        if (!isTareSupported()) {
            indentingPrintWriter.print("Unsupported by device");
            return;
        }
        synchronized (this.mLock) {
            indentingPrintWriter.print("Enabled mode: ");
            indentingPrintWriter.println(EconomyManager.enabledModeToString(this.mEnabledMode));
            indentingPrintWriter.print("Current battery level: ");
            indentingPrintWriter.println(this.mCurrentBatteryLevel);
            long consumptionLimitLocked = getConsumptionLimitLocked();
            indentingPrintWriter.print("Consumption limit (current/initial-satiated/current-satiated): ");
            indentingPrintWriter.print(TareUtils.cakeToString(consumptionLimitLocked));
            indentingPrintWriter.print("/");
            indentingPrintWriter.print(TareUtils.cakeToString(this.mCompleteEconomicPolicy.getInitialSatiatedConsumptionLimit()));
            indentingPrintWriter.print("/");
            indentingPrintWriter.println(TareUtils.cakeToString(this.mScribe.getSatiatedConsumptionLimitLocked()));
            indentingPrintWriter.print("Target bg battery life (hours): ");
            indentingPrintWriter.print(this.mTargetBackgroundBatteryLifeHours);
            indentingPrintWriter.print(" (");
            indentingPrintWriter.print(String.format("%.2f", Float.valueOf(100.0f / this.mTargetBackgroundBatteryLifeHours)));
            indentingPrintWriter.println("%/hr)");
            long remainingConsumableCakesLocked = this.mScribe.getRemainingConsumableCakesLocked();
            indentingPrintWriter.print("Goods remaining: ");
            indentingPrintWriter.print(TareUtils.cakeToString(remainingConsumableCakesLocked));
            indentingPrintWriter.print(" (");
            indentingPrintWriter.print(String.format("%.2f", Float.valueOf((((float) remainingConsumableCakesLocked) * 100.0f) / ((float) consumptionLimitLocked))));
            indentingPrintWriter.println("% of current limit)");
            indentingPrintWriter.print("Device wealth: ");
            indentingPrintWriter.println(TareUtils.cakeToString(this.mScribe.getCakesInCirculationForLoggingLocked()));
            indentingPrintWriter.println();
            indentingPrintWriter.print("Exempted apps", this.mExemptedApps);
            indentingPrintWriter.println();
            indentingPrintWriter.println();
            indentingPrintWriter.print("VIPs:");
            indentingPrintWriter.increaseIndent();
            boolean z2 = false;
            for (int i = 0; i < this.mVipOverrides.numMaps(); i++) {
                int keyAt = this.mVipOverrides.keyAt(i);
                int i2 = 0;
                while (i2 < this.mVipOverrides.numElementsForKeyAt(i)) {
                    indentingPrintWriter.println();
                    indentingPrintWriter.print(TareUtils.appToString(keyAt, (String) this.mVipOverrides.keyAt(i, i2)));
                    indentingPrintWriter.print("=");
                    indentingPrintWriter.print(this.mVipOverrides.valueAt(i, i2));
                    i2++;
                    z2 = true;
                }
            }
            if (z2) {
                indentingPrintWriter.println();
            } else {
                indentingPrintWriter.print(" None");
            }
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println();
            indentingPrintWriter.println();
            indentingPrintWriter.print("Temp VIPs:");
            indentingPrintWriter.increaseIndent();
            boolean z3 = false;
            for (int i3 = 0; i3 < this.mTemporaryVips.numMaps(); i3++) {
                int keyAt2 = this.mTemporaryVips.keyAt(i3);
                int i4 = 0;
                while (i4 < this.mTemporaryVips.numElementsForKeyAt(i3)) {
                    indentingPrintWriter.println();
                    indentingPrintWriter.print(TareUtils.appToString(keyAt2, (String) this.mTemporaryVips.keyAt(i3, i4)));
                    indentingPrintWriter.print("=");
                    indentingPrintWriter.print(this.mTemporaryVips.valueAt(i3, i4));
                    i4++;
                    z3 = true;
                }
            }
            if (z3) {
                indentingPrintWriter.println();
            } else {
                indentingPrintWriter.print(" None");
            }
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println();
            indentingPrintWriter.println();
            indentingPrintWriter.println("Installers:");
            indentingPrintWriter.increaseIndent();
            for (int i5 = 0; i5 < this.mInstallers.numMaps(); i5++) {
                int keyAt3 = this.mInstallers.keyAt(i5);
                for (int i6 = 0; i6 < this.mInstallers.numElementsForKeyAt(i5); i6++) {
                    indentingPrintWriter.print(TareUtils.appToString(keyAt3, (String) this.mInstallers.keyAt(i5, i6)));
                    indentingPrintWriter.print(": ");
                    indentingPrintWriter.print(((ArraySet) this.mInstallers.valueAt(i5, i6)).size());
                    indentingPrintWriter.println(" apps");
                }
            }
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println();
            this.mCompleteEconomicPolicy.dump(indentingPrintWriter);
            indentingPrintWriter.println();
            this.mScribe.dumpLocked(indentingPrintWriter, z);
            indentingPrintWriter.println();
            this.mAgent.dumpLocked(indentingPrintWriter);
            indentingPrintWriter.println();
            this.mAnalyst.dump(indentingPrintWriter);
        }
    }
}
