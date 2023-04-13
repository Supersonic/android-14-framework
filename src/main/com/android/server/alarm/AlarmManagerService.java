package com.android.server.alarm;

import android.app.ActivityManagerInternal;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.BroadcastOptions;
import android.app.IAlarmCompleteListener;
import android.app.IAlarmListener;
import android.app.IAlarmManager;
import android.app.PendingIntent;
import android.app.compat.CompatChanges;
import android.app.role.RoleManager;
import android.app.tare.EconomyManager;
import android.app.usage.UsageStatsManagerInternal;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.PermissionChecker;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.UserPackage;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCallback;
import android.os.ShellCommand;
import android.os.SystemClock;
import android.os.ThreadLocalWorkSource;
import android.os.Trace;
import android.os.UserHandle;
import android.os.WorkSource;
import android.p005os.BatteryStatsInternal;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.system.Os;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.EventLog;
import android.util.IndentingPrintWriter;
import android.util.IntArray;
import android.util.LongArrayQueue;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseArrayMap;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.Keep;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IAppOpsCallback;
import com.android.internal.app.IAppOpsService;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.LocalLog;
import com.android.internal.util.RingBuffer;
import com.android.internal.util.jobs.DumpUtils;
import com.android.internal.util.jobs.StatLogger;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.AlarmManagerInternal;
import com.android.server.AppSchedulingModuleThread;
import com.android.server.AppStateTracker;
import com.android.server.AppStateTrackerImpl;
import com.android.server.DeviceIdleInternal;
import com.android.server.EventLogTags;
import com.android.server.LocalServices;
import com.android.server.SystemClockTime;
import com.android.server.SystemService;
import com.android.server.SystemServiceManager;
import com.android.server.SystemTimeZone;
import com.android.server.alarm.AlarmManagerService;
import com.android.server.alarm.AlarmStore;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.backup.BackupManagerConstants;
import com.android.server.clipboard.ClipboardService;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p011pm.permission.PermissionManagerService;
import com.android.server.p011pm.permission.PermissionManagerServiceInternal;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.tare.EconomyManagerInternal;
import com.android.server.usage.AppStandbyInternal;
import dalvik.annotation.optimization.NeverCompile;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.function.Supplier;
import libcore.util.EmptyArray;
/* loaded from: classes.dex */
public class AlarmManagerService extends SystemService {
    public static final Intent NEXT_ALARM_CLOCK_CHANGED_INTENT = new Intent("android.app.action.NEXT_ALARM_CLOCK_CHANGED").addFlags(553648128);
    public ActivityManagerInternal mActivityManagerInternal;
    public ActivityOptions mActivityOptsRestrictBal;
    @GuardedBy({"mLock"})
    public final SparseArrayMap<String, ArrayMap<EconomyManagerInternal.ActionBill, Boolean>> mAffordabilityCache;
    public final EconomyManagerInternal.AffordabilityChangeListener mAffordabilityChangeListener;
    @GuardedBy({"mLock"})
    public final Runnable mAlarmClockUpdater;
    public final Comparator<Alarm> mAlarmDispatchComparator;
    @GuardedBy({"mLock"})
    public AlarmStore mAlarmStore;
    public SparseIntArray mAlarmsPerUid;
    public AppWakeupHistory mAllowWhileIdleCompatHistory;
    public final ArrayList<Object> mAllowWhileIdleDispatches;
    public AppWakeupHistory mAllowWhileIdleHistory;
    public AppOpsManager mAppOps;
    @VisibleForTesting
    boolean mAppStandbyParole;
    public AppStateTrackerImpl mAppStateTracker;
    public AppWakeupHistory mAppWakeupHistory;
    public final Intent mBackgroundIntent;
    public BatteryStatsInternal mBatteryStatsInternal;
    public BroadcastOptions mBroadcastOptsRestrictBal;
    public int mBroadcastRefCount;
    public final SparseArray<ArrayMap<String, BroadcastStats>> mBroadcastStats;
    public ClockReceiver mClockReceiver;
    public Constants mConstants;
    public int mCurrentSeq;
    public PendingIntent mDateChangeSender;
    public final DeliveryTracker mDeliveryTracker;
    public final EconomyManagerInternal mEconomyManagerInternal;
    @VisibleForTesting
    volatile Set<Integer> mExactAlarmCandidates;
    public final AppStateTrackerImpl.Listener mForceAppStandbyListener;
    public AlarmHandler mHandler;
    public final SparseArray<AlarmManager.AlarmClockInfo> mHandlerSparseAlarmClockArray;
    public ArrayList<InFlight> mInFlight;
    public final ArrayList<AlarmManagerInternal.InFlightListener> mInFlightListeners;
    public final Injector mInjector;
    public boolean mInteractive;
    public long mLastAlarmDeliveryTime;
    @GuardedBy({"mLock"})
    @VisibleForTesting
    SparseIntArray mLastOpScheduleExactAlarm;
    public final SparseLongArray mLastPriorityAlarmDispatch;
    public long mLastTickReceived;
    public long mLastTickSet;
    public long mLastTimeChangeClockTime;
    public long mLastTimeChangeRealtime;
    public long mLastTrigger;
    public long mLastWakeup;
    @GuardedBy({"mLock"})
    public int mListenerCount;
    public IBinder.DeathRecipient mListenerDeathRecipient;
    @GuardedBy({"mLock"})
    public int mListenerFinishCount;
    public DeviceIdleInternal mLocalDeviceIdleController;
    public volatile PermissionManagerServiceInternal mLocalPermissionManager;
    public final Object mLock;
    public final LocalLog mLog;
    public long mMaxDelayTime;
    public MetricsHelper mMetricsHelper;
    public final SparseArray<AlarmManager.AlarmClockInfo> mNextAlarmClockForUser;
    public boolean mNextAlarmClockMayChange;
    public long mNextNonWakeUpSetAt;
    public long mNextNonWakeup;
    public long mNextNonWakeupDeliveryTime;
    public int mNextTickHistory;
    public Alarm mNextWakeFromIdle;
    public long mNextWakeUpSetAt;
    public long mNextWakeup;
    public long mNonInteractiveStartTime;
    public long mNonInteractiveTime;
    public int mNumDelayedAlarms;
    public int mNumTimeChanged;
    public BroadcastOptions mOptsTimeBroadcast;
    public BroadcastOptions mOptsWithFgs;
    public BroadcastOptions mOptsWithFgsForAlarmClock;
    public BroadcastOptions mOptsWithoutFgs;
    public PackageManagerInternal mPackageManagerInternal;
    public SparseArray<ArrayList<Alarm>> mPendingBackgroundAlarms;
    public Alarm mPendingIdleUntil;
    public ArrayList<Alarm> mPendingNonWakeupAlarms;
    public final SparseBooleanArray mPendingSendNextAlarmClockChangedForUser;
    public final HashMap<String, PriorityClass> mPriorities;
    public final SparseArray<RingBuffer<RemovedAlarm>> mRemovalHistory;
    public RoleManager mRoleManager;
    @GuardedBy({"mLock"})
    public int mSendCount;
    @GuardedBy({"mLock"})
    public int mSendFinishCount;
    public final IBinder mService;
    public long mStartCurrentDelayTime;
    public final StatLogger mStatLogger;
    public int mSystemUiUid;
    public TemporaryQuotaReserve mTemporaryQuotaReserve;
    public final long[] mTickHistory;
    public Intent mTimeTickIntent;
    public Bundle mTimeTickOptions;
    public IAlarmListener mTimeTickTrigger;
    public final SparseArray<AlarmManager.AlarmClockInfo> mTmpSparseAlarmClockArray;
    public long mTotalDelayTime;
    public UsageStatsManagerInternal mUsageStatsManagerInternal;
    public PowerManager.WakeLock mWakeLock;

    public static long clampPositive(long j) {
        if (j >= 0) {
            return j;
        }
        return Long.MAX_VALUE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static native void close(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long getNextAlarm(long j, int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long init();

    public static boolean isRtc(int i) {
        return i == 1 || i == 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static native int set(long j, int i, long j2, long j3);

    @Keep
    private static native int setKernelTimezone(long j, int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native int waitForAlarm(long j);

    public static boolean isTimeTickAlarm(Alarm alarm) {
        return alarm.uid == 1000 && "TIME_TICK".equals(alarm.listenerTag);
    }

    public static BroadcastOptions makeBasicAlarmBroadcastOptions() {
        BroadcastOptions makeBasic = BroadcastOptions.makeBasic();
        makeBasic.setAlarmBroadcast(true);
        return makeBasic;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        this.mNextAlarmClockMayChange = true;
    }

    /* loaded from: classes.dex */
    public static class TemporaryQuotaReserve {
        public long mMaxDuration;
        public final ArrayMap<UserPackage, QuotaInfo> mQuotaBuffer = new ArrayMap<>();

        /* loaded from: classes.dex */
        public static class QuotaInfo {
            public long expirationTime;
            public long lastUsage;
            public int remainingQuota;

            public QuotaInfo() {
            }
        }

        public TemporaryQuotaReserve(long j) {
            this.mMaxDuration = j;
        }

        public void replenishQuota(String str, int i, int i2, long j) {
            if (i2 <= 0) {
                return;
            }
            UserPackage of = UserPackage.of(i, str);
            QuotaInfo quotaInfo = this.mQuotaBuffer.get(of);
            if (quotaInfo == null) {
                quotaInfo = new QuotaInfo();
                this.mQuotaBuffer.put(of, quotaInfo);
            }
            quotaInfo.remainingQuota = i2;
            quotaInfo.expirationTime = j + this.mMaxDuration;
        }

        public boolean hasQuota(String str, int i, long j) {
            QuotaInfo quotaInfo = this.mQuotaBuffer.get(UserPackage.of(i, str));
            return quotaInfo != null && quotaInfo.remainingQuota > 0 && j <= quotaInfo.expirationTime;
        }

        public void recordUsage(String str, int i, long j) {
            QuotaInfo quotaInfo = this.mQuotaBuffer.get(UserPackage.of(i, str));
            if (quotaInfo == null) {
                Slog.wtf("AlarmManager", "Temporary quota being consumed at " + j + " but not found for package: " + str + ", user: " + i);
            } else if (j > quotaInfo.lastUsage) {
                int i2 = quotaInfo.remainingQuota;
                if (i2 <= 0) {
                    Slog.wtf("AlarmManager", "Temporary quota being consumed at " + j + " but remaining only " + quotaInfo.remainingQuota + " for package: " + str + ", user: " + i);
                } else if (quotaInfo.expirationTime < j) {
                    Slog.wtf("AlarmManager", "Temporary quota being consumed at " + j + " but expired at " + quotaInfo.expirationTime + " for package: " + str + ", user: " + i);
                } else {
                    quotaInfo.remainingQuota = i2 - 1;
                }
                quotaInfo.lastUsage = j;
            }
        }

        public void cleanUpExpiredQuotas(long j) {
            for (int size = this.mQuotaBuffer.size() - 1; size >= 0; size--) {
                if (this.mQuotaBuffer.valueAt(size).expirationTime < j) {
                    this.mQuotaBuffer.removeAt(size);
                }
            }
        }

        public void removeForUser(int i) {
            for (int size = this.mQuotaBuffer.size() - 1; size >= 0; size--) {
                if (this.mQuotaBuffer.keyAt(size).userId == i) {
                    this.mQuotaBuffer.removeAt(size);
                }
            }
        }

        public void removeForPackage(String str, int i) {
            this.mQuotaBuffer.remove(UserPackage.of(i, str));
        }

        public void dump(IndentingPrintWriter indentingPrintWriter, long j) {
            indentingPrintWriter.increaseIndent();
            for (int i = 0; i < this.mQuotaBuffer.size(); i++) {
                UserPackage keyAt = this.mQuotaBuffer.keyAt(i);
                QuotaInfo valueAt = this.mQuotaBuffer.valueAt(i);
                indentingPrintWriter.print(keyAt.packageName);
                indentingPrintWriter.print(", u");
                indentingPrintWriter.print(keyAt.userId);
                indentingPrintWriter.print(": ");
                if (valueAt == null) {
                    indentingPrintWriter.print("--");
                } else {
                    indentingPrintWriter.print("quota: ");
                    indentingPrintWriter.print(valueAt.remainingQuota);
                    indentingPrintWriter.print(", expiration: ");
                    TimeUtils.formatDuration(valueAt.expirationTime, j, indentingPrintWriter);
                    indentingPrintWriter.print(" last used: ");
                    TimeUtils.formatDuration(valueAt.lastUsage, j, indentingPrintWriter);
                }
                indentingPrintWriter.println();
            }
            indentingPrintWriter.decreaseIndent();
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class AppWakeupHistory {
        public final ArrayMap<UserPackage, LongArrayQueue> mPackageHistory = new ArrayMap<>();
        public long mWindowSize;

        public AppWakeupHistory(long j) {
            this.mWindowSize = j;
        }

        public void recordAlarmForPackage(String str, int i, long j) {
            UserPackage of = UserPackage.of(i, str);
            LongArrayQueue longArrayQueue = this.mPackageHistory.get(of);
            if (longArrayQueue == null) {
                longArrayQueue = new LongArrayQueue();
                this.mPackageHistory.put(of, longArrayQueue);
            }
            if (longArrayQueue.size() == 0 || longArrayQueue.peekLast() < j) {
                longArrayQueue.addLast(j);
            }
            snapToWindow(longArrayQueue);
        }

        public void removeForUser(int i) {
            for (int size = this.mPackageHistory.size() - 1; size >= 0; size--) {
                if (this.mPackageHistory.keyAt(size).userId == i) {
                    this.mPackageHistory.removeAt(size);
                }
            }
        }

        public void removeForPackage(String str, int i) {
            this.mPackageHistory.remove(UserPackage.of(i, str));
        }

        public final void snapToWindow(LongArrayQueue longArrayQueue) {
            while (longArrayQueue.peekFirst() + this.mWindowSize < longArrayQueue.peekLast()) {
                longArrayQueue.removeFirst();
            }
        }

        public int getTotalWakeupsInWindow(String str, int i) {
            LongArrayQueue longArrayQueue = this.mPackageHistory.get(UserPackage.of(i, str));
            if (longArrayQueue == null) {
                return 0;
            }
            return longArrayQueue.size();
        }

        public long getNthLastWakeupForPackage(String str, int i, int i2) {
            int size;
            LongArrayQueue longArrayQueue = this.mPackageHistory.get(UserPackage.of(i, str));
            if (longArrayQueue != null && (size = longArrayQueue.size() - i2) >= 0) {
                return longArrayQueue.get(size);
            }
            return 0L;
        }

        public void dump(IndentingPrintWriter indentingPrintWriter, long j) {
            indentingPrintWriter.increaseIndent();
            for (int i = 0; i < this.mPackageHistory.size(); i++) {
                UserPackage keyAt = this.mPackageHistory.keyAt(i);
                LongArrayQueue valueAt = this.mPackageHistory.valueAt(i);
                indentingPrintWriter.print(keyAt.packageName);
                indentingPrintWriter.print(", u");
                indentingPrintWriter.print(keyAt.userId);
                indentingPrintWriter.print(": ");
                int max = Math.max(0, valueAt.size() - 100);
                for (int size = valueAt.size() - 1; size >= max; size--) {
                    TimeUtils.formatDuration(valueAt.get(size), j, indentingPrintWriter);
                    indentingPrintWriter.print(", ");
                }
                indentingPrintWriter.println();
            }
            indentingPrintWriter.decreaseIndent();
        }
    }

    /* loaded from: classes.dex */
    public static class RemovedAlarm {
        public final int mRemoveReason;
        public final String mTag;
        public final long mWhenRemovedElapsed;
        public final long mWhenRemovedRtc;

        public static final boolean isLoggable(int i) {
            return i != 0;
        }

        public RemovedAlarm(Alarm alarm, int i, long j, long j2) {
            this.mTag = alarm.statsTag;
            this.mRemoveReason = i;
            this.mWhenRemovedRtc = j;
            this.mWhenRemovedElapsed = j2;
        }

        public static final String removeReasonToString(int i) {
            switch (i) {
                case 1:
                    return "alarm_cancelled";
                case 2:
                    return "exact_alarm_permission_revoked";
                case 3:
                    return "data_cleared";
                case 4:
                    return "pi_cancelled";
                case 5:
                    return "listener_binder_died";
                case 6:
                    return "listener_cached";
                default:
                    return "unknown:" + i;
            }
        }

        public void dump(IndentingPrintWriter indentingPrintWriter, long j, SimpleDateFormat simpleDateFormat) {
            indentingPrintWriter.print("[tag", this.mTag);
            indentingPrintWriter.print("reason", removeReasonToString(this.mRemoveReason));
            indentingPrintWriter.print("elapsed=");
            TimeUtils.formatDuration(this.mWhenRemovedElapsed, j, indentingPrintWriter);
            indentingPrintWriter.print(" rtc=");
            indentingPrintWriter.print(simpleDateFormat.format(new Date(this.mWhenRemovedRtc)));
            indentingPrintWriter.println("]");
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public final class Constants implements DeviceConfig.OnPropertiesChangedListener, EconomyManagerInternal.TareStateChangeListener {
        @VisibleForTesting
        static final String KEY_ALLOW_WHILE_IDLE_COMPAT_QUOTA = "allow_while_idle_compat_quota";
        @VisibleForTesting
        static final String KEY_ALLOW_WHILE_IDLE_COMPAT_WINDOW = "allow_while_idle_compat_window";
        @VisibleForTesting
        static final String KEY_ALLOW_WHILE_IDLE_QUOTA = "allow_while_idle_quota";
        @VisibleForTesting
        static final String KEY_ALLOW_WHILE_IDLE_WHITELIST_DURATION = "allow_while_idle_whitelist_duration";
        @VisibleForTesting
        static final String KEY_ALLOW_WHILE_IDLE_WINDOW = "allow_while_idle_window";
        @VisibleForTesting
        static final String KEY_EXACT_ALARM_DENY_LIST = "exact_alarm_deny_list";
        @VisibleForTesting
        static final String KEY_KILL_ON_SCHEDULE_EXACT_ALARM_REVOKED = "kill_on_schedule_exact_alarm_revoked";
        @VisibleForTesting
        static final String KEY_LISTENER_TIMEOUT = "listener_timeout";
        @VisibleForTesting
        static final String KEY_MAX_ALARMS_PER_UID = "max_alarms_per_uid";
        @VisibleForTesting
        static final String KEY_MAX_DEVICE_IDLE_FUZZ = "max_device_idle_fuzz";
        @VisibleForTesting
        static final String KEY_MAX_INTERVAL = "max_interval";
        @VisibleForTesting
        static final String KEY_MIN_DEVICE_IDLE_FUZZ = "min_device_idle_fuzz";
        @VisibleForTesting
        static final String KEY_MIN_FUTURITY = "min_futurity";
        @VisibleForTesting
        static final String KEY_MIN_INTERVAL = "min_interval";
        @VisibleForTesting
        static final String KEY_MIN_WINDOW = "min_window";
        @VisibleForTesting
        static final String KEY_PRIORITY_ALARM_DELAY = "priority_alarm_delay";
        @VisibleForTesting
        static final String KEY_TEMPORARY_QUOTA_BUMP = "temporary_quota_bump";
        @VisibleForTesting
        static final int MAX_EXACT_ALARM_DENY_LIST_SIZE = 250;
        public int[] APP_STANDBY_QUOTAS;
        public final int[] DEFAULT_APP_STANDBY_QUOTAS;
        @VisibleForTesting
        final String[] KEYS_APP_STANDBY_QUOTAS = {"standby_quota_active", "standby_quota_working", "standby_quota_frequent", "standby_quota_rare", "standby_quota_never"};
        public long MIN_FUTURITY = 5000;
        public long MIN_INTERVAL = 60000;
        public long MAX_INTERVAL = 31536000000L;
        public long MIN_WINDOW = 600000;
        public long ALLOW_WHILE_IDLE_WHITELIST_DURATION = 10000;
        public long LISTENER_TIMEOUT = 5000;
        public int MAX_ALARMS_PER_UID = 500;
        public long APP_STANDBY_WINDOW = ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS;
        public int APP_STANDBY_RESTRICTED_QUOTA = 1;
        public long APP_STANDBY_RESTRICTED_WINDOW = BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS;
        public boolean TIME_TICK_ALLOWED_WHILE_IDLE = true;
        public int ALLOW_WHILE_IDLE_QUOTA = 72;
        public int ALLOW_WHILE_IDLE_COMPAT_QUOTA = 7;
        public long ALLOW_WHILE_IDLE_COMPAT_WINDOW = ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS;
        public long ALLOW_WHILE_IDLE_WINDOW = ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS;
        public long PRIORITY_ALARM_DELAY = 540000;
        public volatile Set<String> EXACT_ALARM_DENY_LIST = Collections.emptySet();
        public long MIN_DEVICE_IDLE_FUZZ = 120000;
        public long MAX_DEVICE_IDLE_FUZZ = 900000;
        public boolean KILL_ON_SCHEDULE_EXACT_ALARM_REVOKED = true;
        public int USE_TARE_POLICY = 0;
        public int TEMPORARY_QUOTA_BUMP = 0;
        public boolean DELAY_NONWAKEUP_ALARMS_WHILE_SCREEN_OFF = true;
        public long mLastAllowWhileIdleWhitelistDuration = -1;
        public int mVersion = 0;

        public Constants(Handler handler) {
            int i = 0;
            int[] iArr = {720, 10, 2, 1, 0};
            this.DEFAULT_APP_STANDBY_QUOTAS = iArr;
            this.APP_STANDBY_QUOTAS = new int[iArr.length];
            updateAllowWhileIdleWhitelistDurationLocked();
            while (true) {
                int[] iArr2 = this.APP_STANDBY_QUOTAS;
                if (i >= iArr2.length) {
                    return;
                }
                iArr2[i] = this.DEFAULT_APP_STANDBY_QUOTAS[i];
                i++;
            }
        }

        public int getVersion() {
            int i;
            synchronized (AlarmManagerService.this.mLock) {
                i = this.mVersion;
            }
            return i;
        }

        public void start() {
            AlarmManagerService.this.mInjector.registerDeviceConfigListener(this);
            EconomyManagerInternal economyManagerInternal = (EconomyManagerInternal) LocalServices.getService(EconomyManagerInternal.class);
            economyManagerInternal.registerTareStateChangeListener(this, 268435456);
            onPropertiesChanged(DeviceConfig.getProperties("alarm_manager", new String[0]));
            updateTareSettings(economyManagerInternal.getEnabledMode(268435456));
        }

        public void updateAllowWhileIdleWhitelistDurationLocked() {
            long j = this.mLastAllowWhileIdleWhitelistDuration;
            long j2 = this.ALLOW_WHILE_IDLE_WHITELIST_DURATION;
            if (j != j2) {
                this.mLastAllowWhileIdleWhitelistDuration = j2;
                AlarmManagerService.this.mOptsWithFgs.setTemporaryAppAllowlist(j2, 0, (int) FrameworkStatsLog.f61x1c64b730, "");
                AlarmManagerService.this.mOptsWithFgsForAlarmClock.setTemporaryAppAllowlist(this.ALLOW_WHILE_IDLE_WHITELIST_DURATION, 0, (int) FrameworkStatsLog.f60xe9b52732, "");
                AlarmManagerService.this.mOptsWithoutFgs.setTemporaryAppAllowlist(this.ALLOW_WHILE_IDLE_WHITELIST_DURATION, 1, -1, "");
            }
        }

        public void onPropertiesChanged(DeviceConfig.Properties properties) {
            char c;
            String[] split;
            synchronized (AlarmManagerService.this.mLock) {
                this.mVersion++;
                boolean z = false;
                boolean z2 = false;
                for (String str : properties.getKeyset()) {
                    if (str != null) {
                        switch (str.hashCode()) {
                            case -2073052857:
                                if (str.equals(KEY_KILL_ON_SCHEDULE_EXACT_ALARM_REVOKED)) {
                                    c = 18;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -1577286106:
                                if (str.equals(KEY_ALLOW_WHILE_IDLE_COMPAT_WINDOW)) {
                                    c = 7;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -1490947714:
                                if (str.equals(KEY_MIN_DEVICE_IDLE_FUZZ)) {
                                    c = 16;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -1293038119:
                                if (str.equals(KEY_MIN_FUTURITY)) {
                                    c = 0;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -975718548:
                                if (str.equals(KEY_MAX_ALARMS_PER_UID)) {
                                    c = '\n';
                                    break;
                                }
                                c = 65535;
                                break;
                            case -880907612:
                                if (str.equals("app_standby_restricted_window")) {
                                    c = '\f';
                                    break;
                                }
                                c = 65535;
                                break;
                            case -618440710:
                                if (str.equals(KEY_PRIORITY_ALARM_DELAY)) {
                                    c = 14;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -591494837:
                                if (str.equals(KEY_TEMPORARY_QUOTA_BUMP)) {
                                    c = 19;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -577593775:
                                if (str.equals(KEY_ALLOW_WHILE_IDLE_QUOTA)) {
                                    c = 3;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -564889801:
                                if (str.equals(KEY_ALLOW_WHILE_IDLE_WINDOW)) {
                                    c = 6;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -410928980:
                                if (str.equals(KEY_MAX_DEVICE_IDLE_FUZZ)) {
                                    c = 17;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -392965507:
                                if (str.equals(KEY_MIN_WINDOW)) {
                                    c = 4;
                                    break;
                                }
                                c = 65535;
                                break;
                            case -147388512:
                                if (str.equals("app_standby_window")) {
                                    c = 11;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 932562134:
                                if (str.equals(KEY_LISTENER_TIMEOUT)) {
                                    c = '\t';
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1139967827:
                                if (str.equals(KEY_ALLOW_WHILE_IDLE_WHITELIST_DURATION)) {
                                    c = '\b';
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1213697417:
                                if (str.equals("time_tick_allowed_while_idle")) {
                                    c = '\r';
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1528643904:
                                if (str.equals(KEY_MAX_INTERVAL)) {
                                    c = 2;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1690736963:
                                if (str.equals(KEY_EXACT_ALARM_DENY_LIST)) {
                                    c = 15;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 1883600258:
                                if (str.equals(KEY_ALLOW_WHILE_IDLE_COMPAT_QUOTA)) {
                                    c = 5;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 2003069970:
                                if (str.equals(KEY_MIN_INTERVAL)) {
                                    c = 1;
                                    break;
                                }
                                c = 65535;
                                break;
                            case 2099862680:
                                if (str.equals("delay_nonwakeup_alarms_while_screen_off")) {
                                    c = 20;
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
                                this.MIN_FUTURITY = properties.getLong(KEY_MIN_FUTURITY, 5000L);
                                continue;
                            case 1:
                                this.MIN_INTERVAL = properties.getLong(KEY_MIN_INTERVAL, 60000L);
                                continue;
                            case 2:
                                this.MAX_INTERVAL = properties.getLong(KEY_MAX_INTERVAL, 31536000000L);
                                continue;
                            case 3:
                                int i = properties.getInt(KEY_ALLOW_WHILE_IDLE_QUOTA, 72);
                                this.ALLOW_WHILE_IDLE_QUOTA = i;
                                if (i <= 0) {
                                    Slog.w("AlarmManager", "Must have positive allow_while_idle quota");
                                    this.ALLOW_WHILE_IDLE_QUOTA = 1;
                                    break;
                                } else {
                                    continue;
                                }
                            case 4:
                                this.MIN_WINDOW = properties.getLong(KEY_MIN_WINDOW, 600000L);
                                continue;
                            case 5:
                                int i2 = properties.getInt(KEY_ALLOW_WHILE_IDLE_COMPAT_QUOTA, 7);
                                this.ALLOW_WHILE_IDLE_COMPAT_QUOTA = i2;
                                if (i2 <= 0) {
                                    Slog.w("AlarmManager", "Must have positive allow_while_idle_compat quota");
                                    this.ALLOW_WHILE_IDLE_COMPAT_QUOTA = 1;
                                    break;
                                } else {
                                    continue;
                                }
                            case 6:
                                long j = properties.getLong(KEY_ALLOW_WHILE_IDLE_WINDOW, (long) ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS);
                                this.ALLOW_WHILE_IDLE_WINDOW = j;
                                if (j > ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS) {
                                    Slog.w("AlarmManager", "Cannot have allow_while_idle_window > 3600000");
                                    this.ALLOW_WHILE_IDLE_WINDOW = ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS;
                                    break;
                                } else if (j != ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS) {
                                    Slog.w("AlarmManager", "Using a non-default allow_while_idle_window = " + this.ALLOW_WHILE_IDLE_WINDOW);
                                    break;
                                } else {
                                    continue;
                                }
                            case 7:
                                long j2 = properties.getLong(KEY_ALLOW_WHILE_IDLE_COMPAT_WINDOW, (long) ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS);
                                this.ALLOW_WHILE_IDLE_COMPAT_WINDOW = j2;
                                if (j2 > ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS) {
                                    Slog.w("AlarmManager", "Cannot have allow_while_idle_compat_window > 3600000");
                                    this.ALLOW_WHILE_IDLE_COMPAT_WINDOW = ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS;
                                    break;
                                } else if (j2 != ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS) {
                                    Slog.w("AlarmManager", "Using a non-default allow_while_idle_compat_window = " + this.ALLOW_WHILE_IDLE_COMPAT_WINDOW);
                                    break;
                                } else {
                                    continue;
                                }
                            case '\b':
                                this.ALLOW_WHILE_IDLE_WHITELIST_DURATION = properties.getLong(KEY_ALLOW_WHILE_IDLE_WHITELIST_DURATION, 10000L);
                                updateAllowWhileIdleWhitelistDurationLocked();
                                continue;
                            case '\t':
                                this.LISTENER_TIMEOUT = properties.getLong(KEY_LISTENER_TIMEOUT, 5000L);
                                continue;
                            case '\n':
                                int i3 = properties.getInt(KEY_MAX_ALARMS_PER_UID, 500);
                                this.MAX_ALARMS_PER_UID = i3;
                                if (i3 < 500) {
                                    Slog.w("AlarmManager", "Cannot set max_alarms_per_uid lower than 500");
                                    this.MAX_ALARMS_PER_UID = 500;
                                    break;
                                } else {
                                    continue;
                                }
                            case 11:
                            case '\f':
                                updateStandbyWindowsLocked();
                                continue;
                            case '\r':
                                this.TIME_TICK_ALLOWED_WHILE_IDLE = properties.getBoolean("time_tick_allowed_while_idle", true);
                                continue;
                            case 14:
                                this.PRIORITY_ALARM_DELAY = properties.getLong(KEY_PRIORITY_ALARM_DELAY, 540000L);
                                continue;
                            case 15:
                                String string = properties.getString(KEY_EXACT_ALARM_DENY_LIST, "");
                                if (string.isEmpty()) {
                                    split = EmptyArray.STRING;
                                } else {
                                    split = string.split(",", 251);
                                }
                                if (split.length > MAX_EXACT_ALARM_DENY_LIST_SIZE) {
                                    Slog.w("AlarmManager", "Deny list too long, truncating to 250 elements.");
                                    updateExactAlarmDenyList((String[]) Arrays.copyOf(split, (int) MAX_EXACT_ALARM_DENY_LIST_SIZE));
                                    break;
                                } else {
                                    updateExactAlarmDenyList(split);
                                    continue;
                                }
                            case 16:
                            case 17:
                                if (!z) {
                                    updateDeviceIdleFuzzBoundaries();
                                    z = true;
                                    break;
                                } else {
                                    continue;
                                }
                            case 18:
                                this.KILL_ON_SCHEDULE_EXACT_ALARM_REVOKED = properties.getBoolean(KEY_KILL_ON_SCHEDULE_EXACT_ALARM_REVOKED, true);
                                continue;
                            case 19:
                                this.TEMPORARY_QUOTA_BUMP = properties.getInt(KEY_TEMPORARY_QUOTA_BUMP, 0);
                                continue;
                            case 20:
                                this.DELAY_NONWAKEUP_ALARMS_WHILE_SCREEN_OFF = properties.getBoolean("delay_nonwakeup_alarms_while_screen_off", true);
                                continue;
                            default:
                                if (str.startsWith("standby_quota_")) {
                                    if (z2) {
                                        break;
                                    } else {
                                        updateStandbyQuotasLocked();
                                        z2 = true;
                                        break;
                                    }
                                } else {
                                    continue;
                                }
                        }
                    }
                }
            }
        }

        @Override // com.android.server.tare.EconomyManagerInternal.TareStateChangeListener
        public void onTareEnabledModeChanged(int i) {
            updateTareSettings(i);
        }

        public final void updateTareSettings(int i) {
            synchronized (AlarmManagerService.this.mLock) {
                if (this.USE_TARE_POLICY != i) {
                    this.USE_TARE_POLICY = i;
                    boolean updateAlarmDeliveries = AlarmManagerService.this.mAlarmStore.updateAlarmDeliveries(new AlarmStore.AlarmDeliveryCalculator() { // from class: com.android.server.alarm.AlarmManagerService$Constants$$ExternalSyntheticLambda0
                        @Override // com.android.server.alarm.AlarmStore.AlarmDeliveryCalculator
                        public final boolean updateAlarmDelivery(Alarm alarm) {
                            boolean lambda$updateTareSettings$0;
                            lambda$updateTareSettings$0 = AlarmManagerService.Constants.this.lambda$updateTareSettings$0(alarm);
                            return lambda$updateTareSettings$0;
                        }
                    });
                    if (this.USE_TARE_POLICY != 1) {
                        AlarmManagerService.this.mAffordabilityCache.clear();
                    }
                    if (updateAlarmDeliveries) {
                        AlarmManagerService.this.rescheduleKernelAlarmsLocked();
                        AlarmManagerService.this.updateNextAlarmClockLocked();
                    }
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$updateTareSettings$0(Alarm alarm) {
            boolean adjustDeliveryTimeBasedOnBucketLocked = AlarmManagerService.this.adjustDeliveryTimeBasedOnBucketLocked(alarm);
            boolean adjustDeliveryTimeBasedOnTareLocked = AlarmManagerService.this.adjustDeliveryTimeBasedOnTareLocked(alarm);
            if (this.USE_TARE_POLICY == 1) {
                AlarmManagerService.this.registerTareListener(alarm);
            } else {
                AlarmManagerService.this.mEconomyManagerInternal.unregisterAffordabilityChangeListener(UserHandle.getUserId(alarm.uid), alarm.sourcePackage, AlarmManagerService.this.mAffordabilityChangeListener, TareBill.getAppropriateBill(alarm));
            }
            return adjustDeliveryTimeBasedOnBucketLocked || adjustDeliveryTimeBasedOnTareLocked;
        }

        public final void updateExactAlarmDenyList(String[] strArr) {
            Set<String> unmodifiableSet = Collections.unmodifiableSet(new ArraySet(strArr));
            ArraySet arraySet = new ArraySet(this.EXACT_ALARM_DENY_LIST);
            ArraySet arraySet2 = new ArraySet(strArr);
            arraySet2.removeAll(this.EXACT_ALARM_DENY_LIST);
            arraySet.removeAll(unmodifiableSet);
            if (arraySet2.size() > 0) {
                AlarmManagerService.this.mHandler.obtainMessage(9, arraySet2).sendToTarget();
            }
            if (arraySet.size() > 0) {
                AlarmManagerService.this.mHandler.obtainMessage(10, arraySet).sendToTarget();
            }
            if (strArr.length == 0) {
                this.EXACT_ALARM_DENY_LIST = Collections.emptySet();
            } else {
                this.EXACT_ALARM_DENY_LIST = unmodifiableSet;
            }
        }

        public final void updateDeviceIdleFuzzBoundaries() {
            DeviceConfig.Properties properties = DeviceConfig.getProperties("alarm_manager", new String[]{KEY_MIN_DEVICE_IDLE_FUZZ, KEY_MAX_DEVICE_IDLE_FUZZ});
            this.MIN_DEVICE_IDLE_FUZZ = properties.getLong(KEY_MIN_DEVICE_IDLE_FUZZ, 120000L);
            long j = properties.getLong(KEY_MAX_DEVICE_IDLE_FUZZ, 900000L);
            this.MAX_DEVICE_IDLE_FUZZ = j;
            if (j < this.MIN_DEVICE_IDLE_FUZZ) {
                Slog.w("AlarmManager", "max_device_idle_fuzz cannot be smaller than min_device_idle_fuzz! Increasing to " + this.MIN_DEVICE_IDLE_FUZZ);
                this.MAX_DEVICE_IDLE_FUZZ = this.MIN_DEVICE_IDLE_FUZZ;
            }
        }

        public final void updateStandbyQuotasLocked() {
            DeviceConfig.Properties properties = DeviceConfig.getProperties("alarm_manager", this.KEYS_APP_STANDBY_QUOTAS);
            this.APP_STANDBY_QUOTAS[0] = properties.getInt(this.KEYS_APP_STANDBY_QUOTAS[0], this.DEFAULT_APP_STANDBY_QUOTAS[0]);
            int i = 1;
            while (true) {
                String[] strArr = this.KEYS_APP_STANDBY_QUOTAS;
                if (i < strArr.length) {
                    int[] iArr = this.APP_STANDBY_QUOTAS;
                    iArr[i] = properties.getInt(strArr[i], Math.min(iArr[i - 1], this.DEFAULT_APP_STANDBY_QUOTAS[i]));
                    i++;
                } else {
                    this.APP_STANDBY_RESTRICTED_QUOTA = Math.max(1, DeviceConfig.getInt("alarm_manager", "standby_quota_restricted", 1));
                    return;
                }
            }
        }

        public final void updateStandbyWindowsLocked() {
            DeviceConfig.Properties properties = DeviceConfig.getProperties("alarm_manager", new String[]{"app_standby_window", "app_standby_restricted_window"});
            long j = properties.getLong("app_standby_window", (long) ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS);
            this.APP_STANDBY_WINDOW = j;
            if (j > ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS) {
                Slog.w("AlarmManager", "Cannot exceed the app_standby_window size of 3600000");
                this.APP_STANDBY_WINDOW = ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS;
            } else if (j < ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS) {
                Slog.w("AlarmManager", "Using a non-default app_standby_window of " + this.APP_STANDBY_WINDOW);
            }
            this.APP_STANDBY_RESTRICTED_WINDOW = Math.max(this.APP_STANDBY_WINDOW, properties.getLong("app_standby_restricted_window", (long) BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS));
        }

        public void dump(IndentingPrintWriter indentingPrintWriter) {
            indentingPrintWriter.println("Settings:");
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.print("version", Integer.valueOf(this.mVersion));
            indentingPrintWriter.println();
            indentingPrintWriter.print(KEY_MIN_FUTURITY);
            indentingPrintWriter.print("=");
            TimeUtils.formatDuration(this.MIN_FUTURITY, indentingPrintWriter);
            indentingPrintWriter.println();
            indentingPrintWriter.print(KEY_MIN_INTERVAL);
            indentingPrintWriter.print("=");
            TimeUtils.formatDuration(this.MIN_INTERVAL, indentingPrintWriter);
            indentingPrintWriter.println();
            indentingPrintWriter.print(KEY_MAX_INTERVAL);
            indentingPrintWriter.print("=");
            TimeUtils.formatDuration(this.MAX_INTERVAL, indentingPrintWriter);
            indentingPrintWriter.println();
            indentingPrintWriter.print(KEY_MIN_WINDOW);
            indentingPrintWriter.print("=");
            TimeUtils.formatDuration(this.MIN_WINDOW, indentingPrintWriter);
            indentingPrintWriter.println();
            indentingPrintWriter.print(KEY_LISTENER_TIMEOUT);
            indentingPrintWriter.print("=");
            TimeUtils.formatDuration(this.LISTENER_TIMEOUT, indentingPrintWriter);
            indentingPrintWriter.println();
            indentingPrintWriter.print(KEY_ALLOW_WHILE_IDLE_QUOTA, Integer.valueOf(this.ALLOW_WHILE_IDLE_QUOTA));
            indentingPrintWriter.println();
            indentingPrintWriter.print(KEY_ALLOW_WHILE_IDLE_WINDOW);
            indentingPrintWriter.print("=");
            TimeUtils.formatDuration(this.ALLOW_WHILE_IDLE_WINDOW, indentingPrintWriter);
            indentingPrintWriter.println();
            indentingPrintWriter.print(KEY_ALLOW_WHILE_IDLE_COMPAT_QUOTA, Integer.valueOf(this.ALLOW_WHILE_IDLE_COMPAT_QUOTA));
            indentingPrintWriter.println();
            indentingPrintWriter.print(KEY_ALLOW_WHILE_IDLE_COMPAT_WINDOW);
            indentingPrintWriter.print("=");
            TimeUtils.formatDuration(this.ALLOW_WHILE_IDLE_COMPAT_WINDOW, indentingPrintWriter);
            indentingPrintWriter.println();
            indentingPrintWriter.print(KEY_ALLOW_WHILE_IDLE_WHITELIST_DURATION);
            indentingPrintWriter.print("=");
            TimeUtils.formatDuration(this.ALLOW_WHILE_IDLE_WHITELIST_DURATION, indentingPrintWriter);
            indentingPrintWriter.println();
            indentingPrintWriter.print(KEY_MAX_ALARMS_PER_UID, Integer.valueOf(this.MAX_ALARMS_PER_UID));
            indentingPrintWriter.println();
            indentingPrintWriter.print("app_standby_window");
            indentingPrintWriter.print("=");
            TimeUtils.formatDuration(this.APP_STANDBY_WINDOW, indentingPrintWriter);
            indentingPrintWriter.println();
            int i = 0;
            while (true) {
                String[] strArr = this.KEYS_APP_STANDBY_QUOTAS;
                if (i < strArr.length) {
                    indentingPrintWriter.print(strArr[i], Integer.valueOf(this.APP_STANDBY_QUOTAS[i]));
                    indentingPrintWriter.println();
                    i++;
                } else {
                    indentingPrintWriter.print("standby_quota_restricted", Integer.valueOf(this.APP_STANDBY_RESTRICTED_QUOTA));
                    indentingPrintWriter.println();
                    indentingPrintWriter.print("app_standby_restricted_window");
                    indentingPrintWriter.print("=");
                    TimeUtils.formatDuration(this.APP_STANDBY_RESTRICTED_WINDOW, indentingPrintWriter);
                    indentingPrintWriter.println();
                    indentingPrintWriter.print("time_tick_allowed_while_idle", Boolean.valueOf(this.TIME_TICK_ALLOWED_WHILE_IDLE));
                    indentingPrintWriter.println();
                    indentingPrintWriter.print(KEY_PRIORITY_ALARM_DELAY);
                    indentingPrintWriter.print("=");
                    TimeUtils.formatDuration(this.PRIORITY_ALARM_DELAY, indentingPrintWriter);
                    indentingPrintWriter.println();
                    indentingPrintWriter.print(KEY_EXACT_ALARM_DENY_LIST, this.EXACT_ALARM_DENY_LIST);
                    indentingPrintWriter.println();
                    indentingPrintWriter.print(KEY_MIN_DEVICE_IDLE_FUZZ);
                    indentingPrintWriter.print("=");
                    TimeUtils.formatDuration(this.MIN_DEVICE_IDLE_FUZZ, indentingPrintWriter);
                    indentingPrintWriter.println();
                    indentingPrintWriter.print(KEY_MAX_DEVICE_IDLE_FUZZ);
                    indentingPrintWriter.print("=");
                    TimeUtils.formatDuration(this.MAX_DEVICE_IDLE_FUZZ, indentingPrintWriter);
                    indentingPrintWriter.println();
                    indentingPrintWriter.print(KEY_KILL_ON_SCHEDULE_EXACT_ALARM_REVOKED, Boolean.valueOf(this.KILL_ON_SCHEDULE_EXACT_ALARM_REVOKED));
                    indentingPrintWriter.println();
                    indentingPrintWriter.print("enable_tare", EconomyManager.enabledModeToString(this.USE_TARE_POLICY));
                    indentingPrintWriter.println();
                    indentingPrintWriter.print(KEY_TEMPORARY_QUOTA_BUMP, Integer.valueOf(this.TEMPORARY_QUOTA_BUMP));
                    indentingPrintWriter.println();
                    indentingPrintWriter.print("delay_nonwakeup_alarms_while_screen_off", Boolean.valueOf(this.DELAY_NONWAKEUP_ALARMS_WHILE_SCREEN_OFF));
                    indentingPrintWriter.println();
                    indentingPrintWriter.decreaseIndent();
                    return;
                }
            }
        }

        public void dumpProto(ProtoOutputStream protoOutputStream, long j) {
            long start = protoOutputStream.start(j);
            protoOutputStream.write(1112396529665L, this.MIN_FUTURITY);
            protoOutputStream.write(1112396529666L, this.MIN_INTERVAL);
            protoOutputStream.write(1112396529671L, this.MAX_INTERVAL);
            protoOutputStream.write(1112396529667L, this.LISTENER_TIMEOUT);
            protoOutputStream.write(1112396529670L, this.ALLOW_WHILE_IDLE_WHITELIST_DURATION);
            protoOutputStream.end(start);
        }
    }

    /* loaded from: classes.dex */
    public final class PriorityClass {
        public int priority = 2;
        public int seq;

        public PriorityClass() {
            this.seq = AlarmManagerService.this.mCurrentSeq - 1;
        }
    }

    public void calculateDeliveryPriorities(ArrayList<Alarm> arrayList) {
        int i;
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            Alarm alarm = arrayList.get(i2);
            if (alarm.listener == this.mTimeTickTrigger) {
                i = 0;
            } else {
                i = alarm.wakeup ? 1 : 2;
            }
            PriorityClass priorityClass = alarm.priorityClass;
            String str = alarm.sourcePackage;
            if (priorityClass == null) {
                priorityClass = this.mPriorities.get(str);
            }
            if (priorityClass == null) {
                priorityClass = new PriorityClass();
                alarm.priorityClass = priorityClass;
                this.mPriorities.put(str, priorityClass);
            }
            alarm.priorityClass = priorityClass;
            int i3 = priorityClass.seq;
            int i4 = this.mCurrentSeq;
            if (i3 != i4) {
                priorityClass.priority = i;
                priorityClass.seq = i4;
            } else if (i < priorityClass.priority) {
                priorityClass.priority = i;
            }
        }
    }

    @VisibleForTesting
    public AlarmManagerService(Context context, Injector injector) {
        super(context);
        this.mBackgroundIntent = new Intent().addFlags(4);
        this.mLog = new LocalLog("AlarmManager");
        this.mLock = new Object();
        this.mExactAlarmCandidates = Collections.emptySet();
        this.mLastOpScheduleExactAlarm = new SparseIntArray();
        this.mAffordabilityCache = new SparseArrayMap<>();
        this.mPendingBackgroundAlarms = new SparseArray<>();
        this.mTickHistory = new long[10];
        this.mBroadcastRefCount = 0;
        this.mAlarmsPerUid = new SparseIntArray();
        this.mPendingNonWakeupAlarms = new ArrayList<>();
        this.mInFlight = new ArrayList<>();
        this.mInFlightListeners = new ArrayList<>();
        this.mLastPriorityAlarmDispatch = new SparseLongArray();
        this.mRemovalHistory = new SparseArray<>();
        this.mDeliveryTracker = new DeliveryTracker();
        this.mInteractive = true;
        this.mAllowWhileIdleDispatches = new ArrayList<>();
        this.mStatLogger = new StatLogger("Alarm manager stats", new String[]{"REORDER_ALARMS_FOR_STANDBY", "HAS_SCHEDULE_EXACT_ALARM", "REORDER_ALARMS_FOR_TARE"});
        this.mOptsWithFgs = makeBasicAlarmBroadcastOptions();
        this.mOptsWithFgsForAlarmClock = makeBasicAlarmBroadcastOptions();
        this.mOptsWithoutFgs = makeBasicAlarmBroadcastOptions();
        this.mOptsTimeBroadcast = makeBasicAlarmBroadcastOptions();
        this.mActivityOptsRestrictBal = ActivityOptions.makeBasic();
        this.mBroadcastOptsRestrictBal = makeBasicAlarmBroadcastOptions();
        this.mNextAlarmClockForUser = new SparseArray<>();
        this.mTmpSparseAlarmClockArray = new SparseArray<>();
        this.mPendingSendNextAlarmClockChangedForUser = new SparseBooleanArray();
        this.mAlarmClockUpdater = new Runnable() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                AlarmManagerService.this.lambda$new$0();
            }
        };
        this.mHandlerSparseAlarmClockArray = new SparseArray<>();
        this.mPriorities = new HashMap<>();
        this.mCurrentSeq = 0;
        this.mAlarmDispatchComparator = new Comparator<Alarm>() { // from class: com.android.server.alarm.AlarmManagerService.1
            @Override // java.util.Comparator
            public int compare(Alarm alarm, Alarm alarm2) {
                boolean z = (alarm.flags & 16) != 0;
                if (z != ((alarm2.flags & 16) != 0)) {
                    return z ? -1 : 1;
                }
                int i = alarm.priorityClass.priority;
                int i2 = alarm2.priorityClass.priority;
                if (i < i2) {
                    return -1;
                }
                if (i > i2) {
                    return 1;
                }
                if (alarm.getRequestedElapsed() < alarm2.getRequestedElapsed()) {
                    return -1;
                }
                return alarm.getRequestedElapsed() > alarm2.getRequestedElapsed() ? 1 : 0;
            }
        };
        this.mPendingIdleUntil = null;
        this.mNextWakeFromIdle = null;
        this.mBroadcastStats = new SparseArray<>();
        this.mNumDelayedAlarms = 0;
        this.mTotalDelayTime = 0L;
        this.mMaxDelayTime = 0L;
        this.mService = new IAlarmManager.Stub() { // from class: com.android.server.alarm.AlarmManagerService.5
            public void set(String str, int i, long j, long j2, long j3, int i2, PendingIntent pendingIntent, IAlarmListener iAlarmListener, String str2, WorkSource workSource, AlarmManager.AlarmClockInfo alarmClockInfo) {
                long j4;
                int i3;
                Bundle bundle;
                boolean z;
                boolean z2;
                int i4;
                Bundle bundle2;
                int callingUid = AlarmManagerService.this.mInjector.getCallingUid();
                int userId = UserHandle.getUserId(callingUid);
                if (callingUid != AlarmManagerService.this.mPackageManagerInternal.getPackageUid(str, 0L, userId)) {
                    throw new SecurityException("Package " + str + " does not belong to the calling uid " + callingUid);
                } else if (j3 != 0 && iAlarmListener != null) {
                    throw new IllegalArgumentException("Repeating alarms cannot use AlarmReceivers");
                } else {
                    if (workSource != null) {
                        AlarmManagerService.this.getContext().enforcePermission("android.permission.UPDATE_DEVICE_STATS", Binder.getCallingPid(), callingUid, "AlarmManager.set");
                    }
                    if ((i2 & 16) == 0) {
                        j4 = j2;
                        i3 = i2;
                    } else if (callingUid != 1000) {
                        i3 = i2 & (-17);
                        j4 = j2;
                    } else {
                        i3 = i2;
                        j4 = 0;
                    }
                    int i5 = i3 & (-43);
                    if (alarmClockInfo != null) {
                        i5 |= 2;
                        j4 = 0;
                    } else if (workSource == null && (UserHandle.isCore(callingUid) || UserHandle.isSameApp(callingUid, AlarmManagerService.this.mSystemUiUid) || (AlarmManagerService.this.mAppStateTracker != null && AlarmManagerService.this.mAppStateTracker.isUidPowerSaveUserExempt(callingUid)))) {
                        i5 = (i5 | 8) & (-69);
                    }
                    int i6 = 1;
                    boolean z3 = (i5 & 4) != 0;
                    boolean z4 = j4 == 0;
                    if ((i5 & 64) != 0) {
                        AlarmManagerService.this.getContext().enforcePermission("android.permission.SCHEDULE_PRIORITIZED_ALARM", Binder.getCallingPid(), callingUid, "AlarmManager.setPrioritized");
                        i5 &= -5;
                        if (z4) {
                            i4 = 5;
                            bundle2 = null;
                        }
                        bundle2 = null;
                        i4 = -1;
                    } else {
                        if (z4 || z3) {
                            if (AlarmManagerService.isExactAlarmChangeEnabled(str, userId)) {
                                if (iAlarmListener == null) {
                                    z = !z4;
                                    z2 = z4;
                                } else {
                                    r11 = z4 ? 4 : -1;
                                    z = z3;
                                    z2 = false;
                                }
                                if (!z4) {
                                    bundle = AlarmManagerService.this.mOptsWithoutFgs.toBundle();
                                } else if (alarmClockInfo != null) {
                                    bundle = AlarmManagerService.this.mOptsWithFgsForAlarmClock.toBundle();
                                } else {
                                    bundle = AlarmManagerService.this.mOptsWithFgs.toBundle();
                                }
                            } else {
                                bundle = (z3 || alarmClockInfo != null) ? AlarmManagerService.this.mOptsWithFgs.toBundle() : null;
                                r11 = z4 ? 2 : -1;
                                z = z3;
                                z2 = false;
                            }
                            if (!z2) {
                                z3 = z;
                                i6 = r11;
                            } else if (AlarmManagerService.this.hasUseExactAlarmInternal(str, callingUid)) {
                                i6 = 3;
                                z3 = z;
                            } else if (AlarmManagerService.this.hasScheduleExactAlarmInternal(str, callingUid)) {
                                z3 = z;
                                i6 = 0;
                            } else if (!AlarmManagerService.this.isExemptFromExactAlarmPermissionNoLock(callingUid)) {
                                throw new SecurityException("Caller " + str + " needs to hold android.permission.SCHEDULE_EXACT_ALARM or android.permission.USE_EXACT_ALARM to set exact alarms.");
                            } else {
                                bundle = z3 ? AlarmManagerService.this.mOptsWithoutFgs.toBundle() : null;
                            }
                            if (z3) {
                                i5 = (i5 & (-5)) | 32;
                            }
                            i4 = i6;
                            bundle2 = bundle;
                        }
                        bundle2 = null;
                        i4 = -1;
                    }
                    AlarmManagerService.this.setImpl(i, j, j4, j3, pendingIntent, iAlarmListener, str2, z4 ? i5 | 1 : i5, workSource, alarmClockInfo, callingUid, str, bundle2, i4);
                }
            }

            public boolean canScheduleExactAlarms(String str) {
                int callingUid = AlarmManagerService.this.mInjector.getCallingUid();
                int userId = UserHandle.getUserId(callingUid);
                int packageUid = AlarmManagerService.this.mPackageManagerInternal.getPackageUid(str, 0L, userId);
                if (callingUid == packageUid) {
                    return !AlarmManagerService.isExactAlarmChangeEnabled(str, userId) || AlarmManagerService.this.isExemptFromExactAlarmPermissionNoLock(packageUid) || AlarmManagerService.this.hasScheduleExactAlarmInternal(str, packageUid) || AlarmManagerService.this.hasUseExactAlarmInternal(str, packageUid);
                }
                throw new SecurityException("Uid " + callingUid + " cannot query canScheduleExactAlarms for package " + str);
            }

            public boolean hasScheduleExactAlarm(String str, int i) {
                int callingUid = AlarmManagerService.this.mInjector.getCallingUid();
                if (UserHandle.getUserId(callingUid) != i) {
                    AlarmManagerService.this.getContext().enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL", "hasScheduleExactAlarm");
                }
                int packageUid = AlarmManagerService.this.mPackageManagerInternal.getPackageUid(str, 0L, i);
                if (callingUid == packageUid || UserHandle.isCore(callingUid)) {
                    if (packageUid > 0) {
                        return AlarmManagerService.this.hasScheduleExactAlarmInternal(str, packageUid);
                    }
                    return false;
                }
                throw new SecurityException("Uid " + callingUid + " cannot query hasScheduleExactAlarm for package " + str);
            }

            public boolean setTime(long j) {
                AlarmManagerService.this.getContext().enforceCallingOrSelfPermission("android.permission.SET_TIME", "setTime");
                return AlarmManagerService.this.setTimeImpl(j, 100, "AlarmManager.setTime() called");
            }

            public void setTimeZone(String str) {
                AlarmManagerService.this.getContext().enforceCallingOrSelfPermission("android.permission.SET_TIME_ZONE", "setTimeZone");
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    AlarmManagerService.this.setTimeZoneImpl(str, 100, "AlarmManager.setTimeZone() called");
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }

            public void remove(PendingIntent pendingIntent, IAlarmListener iAlarmListener) {
                if (pendingIntent == null && iAlarmListener == null) {
                    Slog.w("AlarmManager", "remove() with no intent or listener");
                    return;
                }
                synchronized (AlarmManagerService.this.mLock) {
                    AlarmManagerService.this.removeLocked(pendingIntent, iAlarmListener, 1);
                }
            }

            public void removeAll(String str) {
                int callingUid = AlarmManagerService.this.mInjector.getCallingUid();
                if (callingUid == 1000) {
                    Slog.wtfStack("AlarmManager", "Attempt to remove all alarms from the system uid package: " + str);
                } else if (callingUid != AlarmManagerService.this.mPackageManagerInternal.getPackageUid(str, 0L, UserHandle.getUserId(callingUid))) {
                    throw new SecurityException("Package " + str + " does not belong to the calling uid " + callingUid);
                } else {
                    synchronized (AlarmManagerService.this.mLock) {
                        AlarmManagerService.this.removeLocked(str, 1);
                    }
                }
            }

            public long getNextWakeFromIdleTime() {
                return AlarmManagerService.this.getNextWakeFromIdleTimeImpl();
            }

            public AlarmManager.AlarmClockInfo getNextAlarmClock(int i) {
                return AlarmManagerService.this.getNextAlarmClockImpl(AlarmManagerService.this.mActivityManagerInternal.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, 0, "getNextAlarmClock", (String) null));
            }

            public int getConfigVersion() {
                AlarmManagerService.this.getContext().enforceCallingOrSelfPermission("android.permission.DUMP", "getConfigVersion");
                return AlarmManagerService.this.mConstants.getVersion();
            }

            public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
                if (DumpUtils.checkDumpAndUsageStatsPermission(AlarmManagerService.this.getContext(), "AlarmManager", printWriter)) {
                    if (strArr.length > 0 && "--proto".equals(strArr[0])) {
                        AlarmManagerService.this.dumpProto(fileDescriptor);
                    } else {
                        AlarmManagerService.this.dumpImpl(new IndentingPrintWriter(printWriter, "  "));
                    }
                }
            }

            /* JADX WARN: Multi-variable type inference failed */
            public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
                new ShellCmd().exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
            }
        };
        this.mAffordabilityChangeListener = new EconomyManagerInternal.AffordabilityChangeListener() { // from class: com.android.server.alarm.AlarmManagerService.8
            @Override // com.android.server.tare.EconomyManagerInternal.AffordabilityChangeListener
            public void onAffordabilityChanged(int i, String str, EconomyManagerInternal.ActionBill actionBill, boolean z) {
                synchronized (AlarmManagerService.this.mLock) {
                    ArrayMap arrayMap = (ArrayMap) AlarmManagerService.this.mAffordabilityCache.get(i, str);
                    if (arrayMap == null) {
                        arrayMap = new ArrayMap();
                        AlarmManagerService.this.mAffordabilityCache.add(i, str, arrayMap);
                    }
                    arrayMap.put(actionBill, Boolean.valueOf(z));
                }
                AlarmManagerService.this.mHandler.obtainMessage(12, i, z ? 1 : 0, str).sendToTarget();
            }
        };
        this.mForceAppStandbyListener = new C03209();
        this.mSendCount = 0;
        this.mSendFinishCount = 0;
        this.mListenerCount = 0;
        this.mListenerFinishCount = 0;
        this.mInjector = injector;
        this.mEconomyManagerInternal = (EconomyManagerInternal) LocalServices.getService(EconomyManagerInternal.class);
    }

    public AlarmManagerService(Context context) {
        this(context, new Injector(context));
    }

    public final long convertToElapsed(long j, int i) {
        return isRtc(i) ? j - (this.mInjector.getCurrentTimeMillis() - this.mInjector.getElapsedRealtimeMillis()) : j;
    }

    public long getMinimumAllowedWindow(long j, long j2) {
        return Math.min((long) ((j2 - j) * 0.75d), this.mConstants.MIN_WINDOW);
    }

    public static long maxTriggerTime(long j, long j2, long j3) {
        int i = (j3 > 0L ? 1 : (j3 == 0L ? 0 : -1));
        if (i == 0) {
            j3 = j2 - j;
        }
        long j4 = ((long) ((j3 >= 10000 ? j3 : 0L) * 0.75d)) + j2;
        if (i == 0) {
            j4 = Math.min(j4, j2 + ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS);
        }
        return clampPositive(j4);
    }

    public void reevaluateRtcAlarms() {
        Alarm alarm;
        synchronized (this.mLock) {
            boolean updateAlarmDeliveries = this.mAlarmStore.updateAlarmDeliveries(new AlarmStore.AlarmDeliveryCalculator() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda21
                @Override // com.android.server.alarm.AlarmStore.AlarmDeliveryCalculator
                public final boolean updateAlarmDelivery(Alarm alarm2) {
                    boolean lambda$reevaluateRtcAlarms$1;
                    lambda$reevaluateRtcAlarms$1 = AlarmManagerService.this.lambda$reevaluateRtcAlarms$1(alarm2);
                    return lambda$reevaluateRtcAlarms$1;
                }
            });
            if (updateAlarmDeliveries && this.mPendingIdleUntil != null && (alarm = this.mNextWakeFromIdle) != null && isRtc(alarm.type) && this.mAlarmStore.updateAlarmDeliveries(new AlarmStore.AlarmDeliveryCalculator() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda22
                @Override // com.android.server.alarm.AlarmStore.AlarmDeliveryCalculator
                public final boolean updateAlarmDelivery(Alarm alarm2) {
                    boolean lambda$reevaluateRtcAlarms$2;
                    lambda$reevaluateRtcAlarms$2 = AlarmManagerService.this.lambda$reevaluateRtcAlarms$2(alarm2);
                    return lambda$reevaluateRtcAlarms$2;
                }
            })) {
                this.mAlarmStore.updateAlarmDeliveries(new AlarmStore.AlarmDeliveryCalculator() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda23
                    @Override // com.android.server.alarm.AlarmStore.AlarmDeliveryCalculator
                    public final boolean updateAlarmDelivery(Alarm alarm2) {
                        boolean lambda$reevaluateRtcAlarms$3;
                        lambda$reevaluateRtcAlarms$3 = AlarmManagerService.this.lambda$reevaluateRtcAlarms$3(alarm2);
                        return lambda$reevaluateRtcAlarms$3;
                    }
                });
            }
            if (updateAlarmDeliveries) {
                rescheduleKernelAlarmsLocked();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$reevaluateRtcAlarms$1(Alarm alarm) {
        if (isRtc(alarm.type)) {
            return restoreRequestedTime(alarm);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$reevaluateRtcAlarms$2(Alarm alarm) {
        return alarm == this.mPendingIdleUntil && adjustIdleUntilTime(alarm);
    }

    public boolean reorderAlarmsBasedOnStandbyBuckets(final ArraySet<UserPackage> arraySet) {
        long time = this.mStatLogger.getTime();
        boolean updateAlarmDeliveries = this.mAlarmStore.updateAlarmDeliveries(new AlarmStore.AlarmDeliveryCalculator() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda7
            @Override // com.android.server.alarm.AlarmStore.AlarmDeliveryCalculator
            public final boolean updateAlarmDelivery(Alarm alarm) {
                boolean lambda$reorderAlarmsBasedOnStandbyBuckets$4;
                lambda$reorderAlarmsBasedOnStandbyBuckets$4 = AlarmManagerService.this.lambda$reorderAlarmsBasedOnStandbyBuckets$4(arraySet, alarm);
                return lambda$reorderAlarmsBasedOnStandbyBuckets$4;
            }
        });
        this.mStatLogger.logDurationStat(0, time);
        return updateAlarmDeliveries;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$reorderAlarmsBasedOnStandbyBuckets$4(ArraySet arraySet, Alarm alarm) {
        UserPackage of = UserPackage.of(UserHandle.getUserId(alarm.creatorUid), alarm.sourcePackage);
        if (arraySet == null || arraySet.contains(of)) {
            return adjustDeliveryTimeBasedOnBucketLocked(alarm);
        }
        return false;
    }

    public boolean reorderAlarmsBasedOnTare(final ArraySet<UserPackage> arraySet) {
        long time = this.mStatLogger.getTime();
        boolean updateAlarmDeliveries = this.mAlarmStore.updateAlarmDeliveries(new AlarmStore.AlarmDeliveryCalculator() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda11
            @Override // com.android.server.alarm.AlarmStore.AlarmDeliveryCalculator
            public final boolean updateAlarmDelivery(Alarm alarm) {
                boolean lambda$reorderAlarmsBasedOnTare$5;
                lambda$reorderAlarmsBasedOnTare$5 = AlarmManagerService.this.lambda$reorderAlarmsBasedOnTare$5(arraySet, alarm);
                return lambda$reorderAlarmsBasedOnTare$5;
            }
        });
        this.mStatLogger.logDurationStat(2, time);
        return updateAlarmDeliveries;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$reorderAlarmsBasedOnTare$5(ArraySet arraySet, Alarm alarm) {
        UserPackage of = UserPackage.of(UserHandle.getUserId(alarm.creatorUid), alarm.sourcePackage);
        if (arraySet == null || arraySet.contains(of)) {
            return adjustDeliveryTimeBasedOnTareLocked(alarm);
        }
        return false;
    }

    public final boolean restoreRequestedTime(Alarm alarm) {
        return alarm.setPolicyElapsed(0, convertToElapsed(alarm.origWhen, alarm.type));
    }

    @GuardedBy({"mLock"})
    public void sendPendingBackgroundAlarmsLocked(int i, String str) {
        ArrayList<Alarm> arrayList = this.mPendingBackgroundAlarms.get(i);
        if (arrayList == null || arrayList.size() == 0) {
            return;
        }
        if (str != null) {
            ArrayList<Alarm> arrayList2 = new ArrayList<>();
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                if (arrayList.get(size).matches(str)) {
                    arrayList2.add(arrayList.remove(size));
                }
            }
            if (arrayList.size() == 0) {
                this.mPendingBackgroundAlarms.remove(i);
            }
            arrayList = arrayList2;
        } else {
            this.mPendingBackgroundAlarms.remove(i);
        }
        deliverPendingBackgroundAlarmsLocked(arrayList, this.mInjector.getElapsedRealtimeMillis());
    }

    @GuardedBy({"mLock"})
    public void sendAllUnrestrictedPendingBackgroundAlarmsLocked() {
        ArrayList<Alarm> arrayList = new ArrayList<>();
        findAllUnrestrictedPendingBackgroundAlarmsLockedInner(this.mPendingBackgroundAlarms, arrayList, new Predicate() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda20
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean isBackgroundRestricted;
                isBackgroundRestricted = AlarmManagerService.this.isBackgroundRestricted((Alarm) obj);
                return isBackgroundRestricted;
            }
        });
        if (arrayList.size() > 0) {
            deliverPendingBackgroundAlarmsLocked(arrayList, this.mInjector.getElapsedRealtimeMillis());
        }
    }

    @VisibleForTesting
    public static void findAllUnrestrictedPendingBackgroundAlarmsLockedInner(SparseArray<ArrayList<Alarm>> sparseArray, ArrayList<Alarm> arrayList, Predicate<Alarm> predicate) {
        for (int size = sparseArray.size() - 1; size >= 0; size--) {
            ArrayList<Alarm> valueAt = sparseArray.valueAt(size);
            for (int size2 = valueAt.size() - 1; size2 >= 0; size2--) {
                Alarm alarm = valueAt.get(size2);
                if (!predicate.test(alarm)) {
                    arrayList.add(alarm);
                    valueAt.remove(size2);
                }
            }
            if (valueAt.size() == 0) {
                sparseArray.removeAt(size);
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void deliverPendingBackgroundAlarmsLocked(ArrayList<Alarm> arrayList, long j) {
        ArrayList<Alarm> arrayList2;
        long j2;
        int i;
        int i2;
        AlarmManagerService alarmManagerService = this;
        ArrayList<Alarm> arrayList3 = arrayList;
        long j3 = j;
        int size = arrayList.size();
        boolean z = false;
        int i3 = 0;
        while (i3 < size) {
            Alarm alarm = arrayList3.get(i3);
            boolean z2 = alarm.wakeup ? true : z;
            alarm.count = 1;
            if (alarm.repeatInterval > 0) {
                long j4 = alarm.repeatInterval;
                int requestedElapsed = (int) (1 + ((j3 - alarm.getRequestedElapsed()) / j4));
                alarm.count = requestedElapsed;
                long j5 = requestedElapsed * j4;
                long requestedElapsed2 = alarm.getRequestedElapsed() + j5;
                i = i3;
                i2 = size;
                setImplLocked(alarm.type, alarm.origWhen + j5, requestedElapsed2, maxTriggerTime(j, requestedElapsed2, alarm.repeatInterval) - requestedElapsed2, alarm.repeatInterval, alarm.operation, null, null, alarm.flags, alarm.workSource, alarm.alarmClock, alarm.uid, alarm.packageName, null, -1);
            } else {
                i = i3;
                i2 = size;
            }
            i3 = i + 1;
            alarmManagerService = this;
            arrayList3 = arrayList;
            j3 = j;
            z = z2;
            size = i2;
        }
        if (z) {
            arrayList2 = arrayList;
            j2 = j;
        } else {
            j2 = j;
            if (checkAllowNonWakeupDelayLocked(j2)) {
                if (this.mPendingNonWakeupAlarms.size() == 0) {
                    this.mStartCurrentDelayTime = j2;
                    this.mNextNonWakeupDeliveryTime = j2 + ((currentNonWakeupFuzzLocked(j2) * 3) / 2);
                }
                this.mPendingNonWakeupAlarms.addAll(arrayList);
                this.mNumDelayedAlarms += arrayList.size();
                return;
            }
            arrayList2 = arrayList;
        }
        if (this.mPendingNonWakeupAlarms.size() > 0) {
            arrayList2.addAll(this.mPendingNonWakeupAlarms);
            long j6 = j2 - this.mStartCurrentDelayTime;
            this.mTotalDelayTime += j6;
            if (this.mMaxDelayTime < j6) {
                this.mMaxDelayTime = j6;
            }
            this.mPendingNonWakeupAlarms.clear();
        }
        calculateDeliveryPriorities(arrayList);
        Collections.sort(arrayList2, this.mAlarmDispatchComparator);
        deliverAlarmsLocked(arrayList, j);
    }

    /* loaded from: classes.dex */
    public static final class InFlight {
        public final int mAlarmType;
        public final BroadcastStats mBroadcastStats;
        public final int mCreatorUid;
        public final FilterStats mFilterStats;
        public final IBinder mListener;
        public final PendingIntent mPendingIntent;
        public final String mTag;
        public final int mUid;
        public final long mWhenElapsed;
        public final WorkSource mWorkSource;

        public InFlight(AlarmManagerService alarmManagerService, Alarm alarm, long j) {
            BroadcastStats statsLocked;
            this.mPendingIntent = alarm.operation;
            this.mWhenElapsed = j;
            IAlarmListener iAlarmListener = alarm.listener;
            this.mListener = iAlarmListener != null ? iAlarmListener.asBinder() : null;
            this.mWorkSource = alarm.workSource;
            int i = alarm.uid;
            this.mUid = i;
            this.mCreatorUid = alarm.creatorUid;
            String str = alarm.statsTag;
            this.mTag = str;
            PendingIntent pendingIntent = alarm.operation;
            if (pendingIntent != null) {
                statsLocked = alarmManagerService.getStatsLocked(pendingIntent);
            } else {
                statsLocked = alarmManagerService.getStatsLocked(i, alarm.packageName);
            }
            this.mBroadcastStats = statsLocked;
            FilterStats filterStats = statsLocked.filterStats.get(str);
            if (filterStats == null) {
                filterStats = new FilterStats(statsLocked, str);
                statsLocked.filterStats.put(str, filterStats);
            }
            filterStats.lastTime = j;
            this.mFilterStats = filterStats;
            this.mAlarmType = alarm.type;
        }

        public boolean isBroadcast() {
            PendingIntent pendingIntent = this.mPendingIntent;
            return pendingIntent != null && pendingIntent.isBroadcast();
        }

        public String toString() {
            return "InFlight{pendingIntent=" + this.mPendingIntent + ", when=" + this.mWhenElapsed + ", workSource=" + this.mWorkSource + ", uid=" + this.mUid + ", creatorUid=" + this.mCreatorUid + ", tag=" + this.mTag + ", broadcastStats=" + this.mBroadcastStats + ", filterStats=" + this.mFilterStats + ", alarmType=" + this.mAlarmType + "}";
        }

        public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
            long start = protoOutputStream.start(j);
            protoOutputStream.write(1120986464257L, this.mUid);
            protoOutputStream.write(1138166333442L, this.mTag);
            protoOutputStream.write(1112396529667L, this.mWhenElapsed);
            protoOutputStream.write(1159641169924L, this.mAlarmType);
            PendingIntent pendingIntent = this.mPendingIntent;
            if (pendingIntent != null) {
                pendingIntent.dumpDebug(protoOutputStream, 1146756268037L);
            }
            BroadcastStats broadcastStats = this.mBroadcastStats;
            if (broadcastStats != null) {
                broadcastStats.dumpDebug(protoOutputStream, 1146756268038L);
            }
            FilterStats filterStats = this.mFilterStats;
            if (filterStats != null) {
                filterStats.dumpDebug(protoOutputStream, 1146756268039L);
            }
            WorkSource workSource = this.mWorkSource;
            if (workSource != null) {
                workSource.dumpDebug(protoOutputStream, 1146756268040L);
            }
            protoOutputStream.end(start);
        }
    }

    public final void notifyBroadcastAlarmPendingLocked(int i) {
        int size = this.mInFlightListeners.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.mInFlightListeners.get(i2).broadcastAlarmPending(i);
        }
    }

    public final void notifyBroadcastAlarmCompleteLocked(int i) {
        int size = this.mInFlightListeners.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.mInFlightListeners.get(i2).broadcastAlarmComplete(i);
        }
    }

    /* loaded from: classes.dex */
    public static final class FilterStats {
        public long aggregateTime;
        public int count;
        public long lastTime;
        public final BroadcastStats mBroadcastStats;
        public final String mTag;
        public int nesting;
        public int numWakeup;
        public long startTime;

        public FilterStats(BroadcastStats broadcastStats, String str) {
            this.mBroadcastStats = broadcastStats;
            this.mTag = str;
        }

        public String toString() {
            return "FilterStats{tag=" + this.mTag + ", lastTime=" + this.lastTime + ", aggregateTime=" + this.aggregateTime + ", count=" + this.count + ", numWakeup=" + this.numWakeup + ", startTime=" + this.startTime + ", nesting=" + this.nesting + "}";
        }

        public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
            long start = protoOutputStream.start(j);
            protoOutputStream.write(1138166333441L, this.mTag);
            protoOutputStream.write(1112396529666L, this.lastTime);
            protoOutputStream.write(1112396529667L, this.aggregateTime);
            protoOutputStream.write(1120986464260L, this.count);
            protoOutputStream.write(1120986464261L, this.numWakeup);
            protoOutputStream.write(1112396529670L, this.startTime);
            protoOutputStream.write(1120986464263L, this.nesting);
            protoOutputStream.end(start);
        }
    }

    /* loaded from: classes.dex */
    public static final class BroadcastStats {
        public long aggregateTime;
        public int count;
        public final ArrayMap<String, FilterStats> filterStats = new ArrayMap<>();
        public final String mPackageName;
        public final int mUid;
        public int nesting;
        public int numWakeup;
        public long startTime;

        public BroadcastStats(int i, String str) {
            this.mUid = i;
            this.mPackageName = str;
        }

        public String toString() {
            return "BroadcastStats{uid=" + this.mUid + ", packageName=" + this.mPackageName + ", aggregateTime=" + this.aggregateTime + ", count=" + this.count + ", numWakeup=" + this.numWakeup + ", startTime=" + this.startTime + ", nesting=" + this.nesting + "}";
        }

        public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
            long start = protoOutputStream.start(j);
            protoOutputStream.write(1120986464257L, this.mUid);
            protoOutputStream.write(1138166333442L, this.mPackageName);
            protoOutputStream.write(1112396529667L, this.aggregateTime);
            protoOutputStream.write(1120986464260L, this.count);
            protoOutputStream.write(1120986464261L, this.numWakeup);
            protoOutputStream.write(1112396529670L, this.startTime);
            protoOutputStream.write(1120986464263L, this.nesting);
            protoOutputStream.end(start);
        }
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        this.mInjector.init();
        this.mOptsWithFgs.setPendingIntentBackgroundActivityLaunchAllowed(false);
        this.mOptsWithFgsForAlarmClock.setPendingIntentBackgroundActivityLaunchAllowed(false);
        this.mOptsWithoutFgs.setPendingIntentBackgroundActivityLaunchAllowed(false);
        this.mOptsTimeBroadcast.setPendingIntentBackgroundActivityLaunchAllowed(false);
        this.mActivityOptsRestrictBal.setPendingIntentBackgroundActivityLaunchAllowed(false);
        this.mBroadcastOptsRestrictBal.setPendingIntentBackgroundActivityLaunchAllowed(false);
        this.mMetricsHelper = new MetricsHelper(getContext(), this.mLock);
        this.mListenerDeathRecipient = new IBinder.DeathRecipient() { // from class: com.android.server.alarm.AlarmManagerService.2
            @Override // android.os.IBinder.DeathRecipient
            public void binderDied() {
            }

            public void binderDied(IBinder iBinder) {
                IAlarmListener asInterface = IAlarmListener.Stub.asInterface(iBinder);
                synchronized (AlarmManagerService.this.mLock) {
                    AlarmManagerService.this.removeLocked(null, asInterface, 5);
                }
            }
        };
        synchronized (this.mLock) {
            AlarmHandler alarmHandler = new AlarmHandler();
            this.mHandler = alarmHandler;
            this.mConstants = new Constants(alarmHandler);
            LazyAlarmStore lazyAlarmStore = new LazyAlarmStore();
            this.mAlarmStore = lazyAlarmStore;
            lazyAlarmStore.setAlarmClockRemovalListener(this.mAlarmClockUpdater);
            this.mAppWakeupHistory = new AppWakeupHistory(ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS);
            this.mAllowWhileIdleHistory = new AppWakeupHistory(ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS);
            this.mAllowWhileIdleCompatHistory = new AppWakeupHistory(ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS);
            this.mTemporaryQuotaReserve = new TemporaryQuotaReserve(BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS);
            this.mNextNonWakeup = 0L;
            this.mNextWakeup = 0L;
            this.mInjector.initializeTimeIfRequired();
            PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
            this.mPackageManagerInternal = packageManagerInternal;
            int systemUiUid = this.mInjector.getSystemUiUid(packageManagerInternal);
            this.mSystemUiUid = systemUiUid;
            if (systemUiUid <= 0) {
                Slog.wtf("AlarmManager", "SysUI package not found!");
            }
            this.mWakeLock = this.mInjector.getAlarmWakeLock();
            this.mTimeTickIntent = new Intent("android.intent.action.TIME_TICK").addFlags(1344274432);
            this.mTimeTickOptions = BroadcastOptions.makeBasic().setDeliveryGroupPolicy(1).setDeferUntilActive(true).toBundle();
            this.mTimeTickTrigger = new IAlarmListener$StubC03143();
            Intent intent = new Intent("android.intent.action.DATE_CHANGED");
            intent.addFlags(538968064);
            this.mDateChangeSender = PendingIntent.getBroadcastAsUser(getContext(), 0, intent, 67108864, UserHandle.ALL);
            this.mClockReceiver = this.mInjector.getClockReceiver(this);
            new ChargingReceiver();
            new InteractiveStateReceiver();
            new UninstallReceiver();
            if (this.mInjector.isAlarmDriverPresent()) {
                new AlarmThread().start();
            } else {
                Slog.w("AlarmManager", "Failed to open alarm driver. Falling back to a handler.");
            }
        }
        this.mActivityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        publishLocalService(AlarmManagerInternal.class, new LocalService());
        publishBinderService("alarm", this.mService);
    }

    /* renamed from: com.android.server.alarm.AlarmManagerService$3 */
    /* loaded from: classes.dex */
    public class IAlarmListener$StubC03143 extends IAlarmListener.Stub {
        public IAlarmListener$StubC03143() {
        }

        public void doAlarm(final IAlarmCompleteListener iAlarmCompleteListener) throws RemoteException {
            AlarmManagerService.this.mHandler.post(new Runnable() { // from class: com.android.server.alarm.AlarmManagerService$3$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AlarmManagerService.IAlarmListener$StubC03143.this.lambda$doAlarm$0(iAlarmCompleteListener);
                }
            });
            synchronized (AlarmManagerService.this.mLock) {
                AlarmManagerService alarmManagerService = AlarmManagerService.this;
                alarmManagerService.mLastTickReceived = alarmManagerService.mInjector.getCurrentTimeMillis();
            }
            AlarmManagerService.this.mClockReceiver.scheduleTimeTickEvent();
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Multi-variable type inference failed */
        public /* synthetic */ void lambda$doAlarm$0(IAlarmCompleteListener iAlarmCompleteListener) {
            Context context = AlarmManagerService.this.getContext();
            AlarmManagerService alarmManagerService = AlarmManagerService.this;
            context.sendBroadcastAsUser(alarmManagerService.mTimeTickIntent, UserHandle.ALL, null, alarmManagerService.mTimeTickOptions);
            try {
                iAlarmCompleteListener.alarmComplete(this);
            } catch (RemoteException unused) {
            }
        }
    }

    public void refreshExactAlarmCandidates() {
        String[] appOpPermissionPackages = this.mLocalPermissionManager.getAppOpPermissionPackages("android.permission.SCHEDULE_EXACT_ALARM");
        ArraySet arraySet = new ArraySet(appOpPermissionPackages.length);
        for (String str : appOpPermissionPackages) {
            int packageUid = this.mPackageManagerInternal.getPackageUid(str, 4194304L, 0);
            if (packageUid > 0) {
                arraySet.add(Integer.valueOf(UserHandle.getAppId(packageUid)));
            }
        }
        this.mExactAlarmCandidates = Collections.unmodifiableSet(arraySet);
    }

    @Override // com.android.server.SystemService
    public void onUserStarting(SystemService.TargetUser targetUser) {
        super.onUserStarting(targetUser);
        final int userIdentifier = targetUser.getUserIdentifier();
        this.mHandler.post(new Runnable() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                AlarmManagerService.this.lambda$onUserStarting$6(userIdentifier);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onUserStarting$6(int i) {
        for (Integer num : this.mExactAlarmCandidates) {
            int uid = UserHandle.getUid(i, num.intValue());
            AndroidPackage androidPackage = this.mPackageManagerInternal.getPackage(uid);
            if (androidPackage != null) {
                int checkOpNoThrow = this.mAppOps.checkOpNoThrow(107, uid, androidPackage.getPackageName());
                synchronized (this.mLock) {
                    this.mLastOpScheduleExactAlarm.put(uid, checkOpNoThrow);
                }
            }
        }
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 500) {
            synchronized (this.mLock) {
                this.mConstants.start();
                this.mAppOps = (AppOpsManager) getContext().getSystemService("appops");
                this.mLocalDeviceIdleController = (DeviceIdleInternal) LocalServices.getService(DeviceIdleInternal.class);
                this.mUsageStatsManagerInternal = (UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class);
                AppStateTrackerImpl appStateTrackerImpl = (AppStateTrackerImpl) LocalServices.getService(AppStateTracker.class);
                this.mAppStateTracker = appStateTrackerImpl;
                appStateTrackerImpl.addListener(this.mForceAppStandbyListener);
                this.mAppStandbyParole = ((BatteryManager) getContext().getSystemService(BatteryManager.class)).isCharging();
                this.mClockReceiver.scheduleTimeTickEvent();
                this.mClockReceiver.scheduleDateChangedEvent();
            }
            try {
                this.mInjector.getAppOpsService().startWatchingMode(107, (String) null, new IAppOpsCallback.Stub() { // from class: com.android.server.alarm.AlarmManagerService.4
                    public void opChanged(int i2, int i3, String str) throws RemoteException {
                        int valueAt;
                        int userId = UserHandle.getUserId(i3);
                        if (i2 == 107 && AlarmManagerService.isExactAlarmChangeEnabled(str, userId) && !AlarmManagerService.this.hasUseExactAlarmInternal(str, i3) && AlarmManagerService.this.mExactAlarmCandidates.contains(Integer.valueOf(UserHandle.getAppId(i3)))) {
                            int checkOpNoThrow = AlarmManagerService.this.mAppOps.checkOpNoThrow(107, i3, str);
                            synchronized (AlarmManagerService.this.mLock) {
                                int indexOfKey = AlarmManagerService.this.mLastOpScheduleExactAlarm.indexOfKey(i3);
                                if (indexOfKey < 0) {
                                    valueAt = AppOpsManager.opToDefaultMode(107);
                                    AlarmManagerService.this.mLastOpScheduleExactAlarm.put(i3, checkOpNoThrow);
                                } else {
                                    valueAt = AlarmManagerService.this.mLastOpScheduleExactAlarm.valueAt(indexOfKey);
                                    AlarmManagerService.this.mLastOpScheduleExactAlarm.setValueAt(indexOfKey, checkOpNoThrow);
                                }
                            }
                            if (valueAt == checkOpNoThrow) {
                                return;
                            }
                            boolean isScheduleExactAlarmAllowedByDefault = AlarmManagerService.this.isScheduleExactAlarmAllowedByDefault(str, i3);
                            boolean z = valueAt != 3 ? valueAt == 0 : isScheduleExactAlarmAllowedByDefault;
                            if (checkOpNoThrow != 3) {
                                isScheduleExactAlarmAllowedByDefault = checkOpNoThrow == 0;
                            }
                            if (z && !isScheduleExactAlarmAllowedByDefault) {
                                AlarmManagerService.this.mHandler.obtainMessage(8, i3, 0, str).sendToTarget();
                            } else if (z || !isScheduleExactAlarmAllowedByDefault) {
                            } else {
                                AlarmManagerService.this.sendScheduleExactAlarmPermissionStateChangedBroadcast(str, userId);
                            }
                        }
                    }
                });
            } catch (RemoteException unused) {
            }
            this.mLocalPermissionManager = (PermissionManagerServiceInternal) LocalServices.getService(PermissionManagerServiceInternal.class);
            refreshExactAlarmCandidates();
            ((AppStandbyInternal) LocalServices.getService(AppStandbyInternal.class)).addListener(new AppStandbyTracker());
            this.mBatteryStatsInternal = (BatteryStatsInternal) LocalServices.getService(BatteryStatsInternal.class);
            this.mRoleManager = (RoleManager) getContext().getSystemService(RoleManager.class);
            this.mMetricsHelper.registerPuller(new Supplier() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda5
                @Override // java.util.function.Supplier
                public final Object get() {
                    AlarmStore lambda$onBootPhase$7;
                    lambda$onBootPhase$7 = AlarmManagerService.this.lambda$onBootPhase$7();
                    return lambda$onBootPhase$7;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ AlarmStore lambda$onBootPhase$7() {
        return this.mAlarmStore;
    }

    public void finalize() throws Throwable {
        try {
            this.mInjector.close();
        } finally {
            super.finalize();
        }
    }

    public boolean setTimeImpl(long j, int i, String str) {
        synchronized (this.mLock) {
            this.mInjector.getCurrentTimeMillis();
            this.mInjector.setCurrentTimeMillis(j, i, str);
        }
        return true;
    }

    public void setTimeZoneImpl(String str, int i, String str2) {
        boolean timeZoneId;
        if (TextUtils.isEmpty(str)) {
            return;
        }
        TimeZone timeZone = TimeZone.getTimeZone(str);
        synchronized (this) {
            timeZoneId = SystemTimeZone.setTimeZoneId(str, i, str2);
        }
        TimeZone.setDefault(null);
        if (timeZoneId) {
            this.mClockReceiver.scheduleDateChangedEvent();
            Intent intent = new Intent("android.intent.action.TIMEZONE_CHANGED");
            intent.addFlags(622854144);
            intent.putExtra("time-zone", timeZone.getID());
            this.mOptsTimeBroadcast.setTemporaryAppAllowlist(this.mActivityManagerInternal.getBootTimeTempAllowListDuration(), 0, 204, "");
            getContext().sendBroadcastAsUser(intent, UserHandle.ALL, null, this.mOptsTimeBroadcast.toBundle());
        }
    }

    public void removeImpl(PendingIntent pendingIntent, IAlarmListener iAlarmListener) {
        synchronized (this.mLock) {
            removeLocked(pendingIntent, iAlarmListener, 0);
        }
    }

    public void setImpl(int i, long j, long j2, long j3, PendingIntent pendingIntent, IAlarmListener iAlarmListener, String str, int i2, WorkSource workSource, AlarmManager.AlarmClockInfo alarmClockInfo, int i3, String str2, Bundle bundle, int i4) {
        long j4;
        long minimumAllowedWindow;
        long j5;
        long j6 = j;
        long j7 = j3;
        if ((pendingIntent == null && iAlarmListener == null) || (pendingIntent != null && iAlarmListener != null)) {
            Slog.w("AlarmManager", "Alarms must either supply a PendingIntent or an AlarmReceiver");
            return;
        }
        if (iAlarmListener != null) {
            try {
                iAlarmListener.asBinder().linkToDeath(this.mListenerDeathRecipient, 0);
            } catch (RemoteException unused) {
                Slog.w("AlarmManager", "Dropping unreachable alarm listener " + str);
                return;
            }
        }
        Constants constants = this.mConstants;
        long j8 = constants.MIN_INTERVAL;
        if (j7 > 0 && j7 < j8) {
            Slog.w("AlarmManager", "Suspiciously short interval " + j7 + " millis; expanding to " + (j8 / 1000) + " seconds");
            j4 = j8;
        } else {
            if (j7 > constants.MAX_INTERVAL) {
                Slog.w("AlarmManager", "Suspiciously long interval " + j7 + " millis; clamping");
                j7 = this.mConstants.MAX_INTERVAL;
            }
            j4 = j7;
        }
        if (i < 0 || i > 3) {
            throw new IllegalArgumentException("Invalid alarm type " + i);
        }
        if (j6 < 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid alarm trigger time! ");
            sb.append(j6);
            sb.append(" from uid=");
            sb.append(i3);
            sb.append(" pid=");
            sb.append(Binder.getCallingPid());
            Slog.w("AlarmManager", sb.toString());
            j6 = 0;
        }
        long elapsedRealtimeMillis = this.mInjector.getElapsedRealtimeMillis();
        long max = Math.max((UserHandle.isCore(i3) ? 0L : this.mConstants.MIN_FUTURITY) + elapsedRealtimeMillis, convertToElapsed(j6, i));
        int i5 = (j2 > 0L ? 1 : (j2 == 0L ? 0 : -1));
        if (i5 == 0) {
            j5 = j2;
        } else {
            if (i5 < 0) {
                minimumAllowedWindow = maxTriggerTime(elapsedRealtimeMillis, max, j4) - max;
            } else {
                minimumAllowedWindow = getMinimumAllowedWindow(elapsedRealtimeMillis, max);
                if (j2 > BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS) {
                    Slog.w("AlarmManager", "Window length " + j2 + "ms too long; limiting to 1 day");
                    minimumAllowedWindow = BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS;
                } else if ((i2 & 64) != 0 || j2 >= minimumAllowedWindow || isExemptFromMinWindowRestrictions(i3) || !CompatChanges.isChangeEnabled(185199076L, str2, UserHandle.getUserHandleForUid(i3))) {
                    minimumAllowedWindow = j2;
                } else {
                    Slog.w("AlarmManager", "Window length " + j2 + "ms too short; expanding to " + minimumAllowedWindow + "ms.");
                }
            }
            j5 = minimumAllowedWindow;
        }
        synchronized (this.mLock) {
            try {
                try {
                    if (this.mAlarmsPerUid.get(i3, 0) >= this.mConstants.MAX_ALARMS_PER_UID) {
                        String str3 = "Maximum limit of concurrent alarms " + this.mConstants.MAX_ALARMS_PER_UID + " reached for uid: " + UserHandle.formatUid(i3) + ", callingPackage: " + str2;
                        Slog.w("AlarmManager", str3);
                        if (i3 != 1000) {
                            throw new IllegalStateException(str3);
                        }
                        EventLog.writeEvent(1397638484, "234441463", -1, str3);
                    }
                    setImplLocked(i, j6, max, j5, j4, pendingIntent, iAlarmListener, str, i2, workSource, alarmClockInfo, i3, str2, bundle, i4);
                } catch (Throwable th) {
                    th = th;
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                throw th;
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void setImplLocked(int i, long j, long j2, long j3, long j4, PendingIntent pendingIntent, IAlarmListener iAlarmListener, String str, int i2, WorkSource workSource, AlarmManager.AlarmClockInfo alarmClockInfo, int i3, String str2, Bundle bundle, int i4) {
        Alarm alarm = new Alarm(i, j, j2, j3, j4, pendingIntent, iAlarmListener, str, workSource, i2, alarmClockInfo, i3, str2, bundle, i4);
        if (this.mActivityManagerInternal.isAppStartModeDisabled(i3, str2)) {
            Slog.w("AlarmManager", "Not setting alarm from " + i3 + XmlUtils.STRING_ARRAY_SEPARATOR + alarm + " -- package not allowed to start");
            return;
        }
        int uidProcessState = this.mActivityManagerInternal.getUidProcessState(i3);
        removeLocked(pendingIntent, iAlarmListener, 0);
        incrementAlarmCount(alarm.uid);
        setImplLocked(alarm);
        MetricsHelper.pushAlarmScheduled(alarm, uidProcessState);
    }

    @VisibleForTesting
    public int getQuotaForBucketLocked(int i) {
        return this.mConstants.APP_STANDBY_QUOTAS[i <= 10 ? (char) 0 : i <= 20 ? (char) 1 : i <= 30 ? (char) 2 : i < 50 ? (char) 3 : (char) 4];
    }

    public final boolean adjustIdleUntilTime(Alarm alarm) {
        if ((alarm.flags & 16) == 0) {
            return false;
        }
        boolean restoreRequestedTime = restoreRequestedTime(alarm);
        Alarm alarm2 = this.mNextWakeFromIdle;
        if (alarm2 == null) {
            return restoreRequestedTime;
        }
        long whenElapsed = alarm2.getWhenElapsed();
        if (alarm.getWhenElapsed() < whenElapsed - this.mConstants.MIN_DEVICE_IDLE_FUZZ) {
            return restoreRequestedTime;
        }
        long elapsedRealtimeMillis = this.mInjector.getElapsedRealtimeMillis();
        long j = whenElapsed - elapsedRealtimeMillis;
        if (j <= this.mConstants.MIN_DEVICE_IDLE_FUZZ) {
            alarm.setPolicyElapsed(0, elapsedRealtimeMillis);
            return true;
        }
        alarm.setPolicyElapsed(0, whenElapsed - ThreadLocalRandom.current().nextLong(this.mConstants.MIN_DEVICE_IDLE_FUZZ, Math.min(this.mConstants.MAX_DEVICE_IDLE_FUZZ, j) + 1));
        return true;
    }

    public final boolean adjustDeliveryTimeBasedOnBatterySaver(Alarm alarm) {
        int i;
        long j;
        AppWakeupHistory appWakeupHistory;
        long elapsedRealtimeMillis = this.mInjector.getElapsedRealtimeMillis();
        if (isExemptFromBatterySaver(alarm)) {
            return false;
        }
        AppStateTrackerImpl appStateTrackerImpl = this.mAppStateTracker;
        if (appStateTrackerImpl == null || !appStateTrackerImpl.areAlarmsRestrictedByBatterySaver(alarm.creatorUid, alarm.sourcePackage)) {
            return alarm.setPolicyElapsed(3, elapsedRealtimeMillis);
        }
        if ((alarm.flags & 8) == 0) {
            if (isAllowedWhileIdleRestricted(alarm)) {
                int userId = UserHandle.getUserId(alarm.creatorUid);
                if ((alarm.flags & 4) != 0) {
                    Constants constants = this.mConstants;
                    i = constants.ALLOW_WHILE_IDLE_QUOTA;
                    j = constants.ALLOW_WHILE_IDLE_WINDOW;
                    appWakeupHistory = this.mAllowWhileIdleHistory;
                } else {
                    Constants constants2 = this.mConstants;
                    i = constants2.ALLOW_WHILE_IDLE_COMPAT_QUOTA;
                    j = constants2.ALLOW_WHILE_IDLE_COMPAT_WINDOW;
                    appWakeupHistory = this.mAllowWhileIdleCompatHistory;
                }
                if (appWakeupHistory.getTotalWakeupsInWindow(alarm.sourcePackage, userId) >= i) {
                    elapsedRealtimeMillis = appWakeupHistory.getNthLastWakeupForPackage(alarm.sourcePackage, userId, i) + j;
                }
            } else if ((alarm.flags & 64) != 0) {
                long j2 = this.mLastPriorityAlarmDispatch.get(alarm.creatorUid, 0L);
                if (j2 != 0) {
                    elapsedRealtimeMillis = this.mConstants.PRIORITY_ALARM_DELAY + j2;
                }
            } else {
                elapsedRealtimeMillis += 31536000000L;
            }
        }
        return alarm.setPolicyElapsed(3, elapsedRealtimeMillis);
    }

    public static boolean isAllowedWhileIdleRestricted(Alarm alarm) {
        return (alarm.flags & 36) != 0;
    }

    /* renamed from: adjustDeliveryTimeBasedOnDeviceIdle */
    public final boolean lambda$triggerAlarmsLocked$22(Alarm alarm) {
        int i;
        long j;
        AppWakeupHistory appWakeupHistory;
        long elapsedRealtimeMillis = this.mInjector.getElapsedRealtimeMillis();
        Alarm alarm2 = this.mPendingIdleUntil;
        if (alarm2 == null || alarm2 == alarm) {
            return alarm.setPolicyElapsed(2, elapsedRealtimeMillis);
        }
        if ((alarm.flags & 10) == 0) {
            if (isAllowedWhileIdleRestricted(alarm)) {
                int userId = UserHandle.getUserId(alarm.creatorUid);
                if ((alarm.flags & 4) != 0) {
                    Constants constants = this.mConstants;
                    i = constants.ALLOW_WHILE_IDLE_QUOTA;
                    j = constants.ALLOW_WHILE_IDLE_WINDOW;
                    appWakeupHistory = this.mAllowWhileIdleHistory;
                } else {
                    Constants constants2 = this.mConstants;
                    i = constants2.ALLOW_WHILE_IDLE_COMPAT_QUOTA;
                    j = constants2.ALLOW_WHILE_IDLE_COMPAT_WINDOW;
                    appWakeupHistory = this.mAllowWhileIdleCompatHistory;
                }
                if (appWakeupHistory.getTotalWakeupsInWindow(alarm.sourcePackage, userId) >= i) {
                    elapsedRealtimeMillis = Math.min(appWakeupHistory.getNthLastWakeupForPackage(alarm.sourcePackage, userId, i) + j, this.mPendingIdleUntil.getWhenElapsed());
                }
            } else if ((alarm.flags & 64) != 0) {
                long j2 = this.mLastPriorityAlarmDispatch.get(alarm.creatorUid, 0L);
                if (j2 != 0) {
                    elapsedRealtimeMillis = this.mConstants.PRIORITY_ALARM_DELAY + j2;
                }
                elapsedRealtimeMillis = Math.min(elapsedRealtimeMillis, this.mPendingIdleUntil.getWhenElapsed());
            } else {
                elapsedRealtimeMillis = this.mPendingIdleUntil.getWhenElapsed();
            }
        }
        return alarm.setPolicyElapsed(2, elapsedRealtimeMillis);
    }

    public final boolean adjustDeliveryTimeBasedOnBucketLocked(Alarm alarm) {
        long j;
        long elapsedRealtimeMillis = this.mInjector.getElapsedRealtimeMillis();
        if (this.mConstants.USE_TARE_POLICY == 1 || isExemptFromAppStandby(alarm) || this.mAppStandbyParole) {
            return alarm.setPolicyElapsed(1, elapsedRealtimeMillis);
        }
        String str = alarm.sourcePackage;
        int userId = UserHandle.getUserId(alarm.creatorUid);
        int appStandbyBucket = this.mUsageStatsManagerInternal.getAppStandbyBucket(str, userId, elapsedRealtimeMillis);
        int totalWakeupsInWindow = this.mAppWakeupHistory.getTotalWakeupsInWindow(str, userId);
        if (appStandbyBucket != 45) {
            int quotaForBucketLocked = getQuotaForBucketLocked(appStandbyBucket);
            if (totalWakeupsInWindow >= quotaForBucketLocked) {
                if (this.mTemporaryQuotaReserve.hasQuota(str, userId, elapsedRealtimeMillis)) {
                    alarm.mUsingReserveQuota = true;
                    return alarm.setPolicyElapsed(1, elapsedRealtimeMillis);
                }
                if (quotaForBucketLocked <= 0) {
                    j = 31536000000L;
                } else {
                    elapsedRealtimeMillis = this.mAppWakeupHistory.getNthLastWakeupForPackage(str, userId, quotaForBucketLocked);
                    j = this.mConstants.APP_STANDBY_WINDOW;
                }
                return alarm.setPolicyElapsed(1, elapsedRealtimeMillis + j);
            }
        } else if (totalWakeupsInWindow > 0) {
            long nthLastWakeupForPackage = this.mAppWakeupHistory.getNthLastWakeupForPackage(str, userId, this.mConstants.APP_STANDBY_RESTRICTED_QUOTA);
            long j2 = this.mConstants.APP_STANDBY_RESTRICTED_WINDOW;
            if (elapsedRealtimeMillis - nthLastWakeupForPackage < j2) {
                return alarm.setPolicyElapsed(1, nthLastWakeupForPackage + j2);
            }
        }
        alarm.mUsingReserveQuota = false;
        return alarm.setPolicyElapsed(1, elapsedRealtimeMillis);
    }

    public final boolean adjustDeliveryTimeBasedOnTareLocked(Alarm alarm) {
        long elapsedRealtimeMillis = this.mInjector.getElapsedRealtimeMillis();
        if (this.mConstants.USE_TARE_POLICY != 1 || isExemptFromTare(alarm) || hasEnoughWealthLocked(alarm)) {
            return alarm.setPolicyElapsed(4, elapsedRealtimeMillis);
        }
        return alarm.setPolicyElapsed(4, elapsedRealtimeMillis + 31536000000L);
    }

    public final void registerTareListener(Alarm alarm) {
        if (this.mConstants.USE_TARE_POLICY != 1) {
            return;
        }
        this.mEconomyManagerInternal.registerAffordabilityChangeListener(UserHandle.getUserId(alarm.creatorUid), alarm.sourcePackage, this.mAffordabilityChangeListener, TareBill.getAppropriateBill(alarm));
    }

    @GuardedBy({"mLock"})
    public final void maybeUnregisterTareListenerLocked(final Alarm alarm) {
        if (this.mConstants.USE_TARE_POLICY != 1) {
            return;
        }
        final EconomyManagerInternal.ActionBill appropriateBill = TareBill.getAppropriateBill(alarm);
        if (this.mAlarmStore.getCount(new Predicate() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda12
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$maybeUnregisterTareListenerLocked$8;
                lambda$maybeUnregisterTareListenerLocked$8 = AlarmManagerService.lambda$maybeUnregisterTareListenerLocked$8(Alarm.this, appropriateBill, (Alarm) obj);
                return lambda$maybeUnregisterTareListenerLocked$8;
            }
        }) == 0) {
            int userId = UserHandle.getUserId(alarm.creatorUid);
            this.mEconomyManagerInternal.unregisterAffordabilityChangeListener(userId, alarm.sourcePackage, this.mAffordabilityChangeListener, appropriateBill);
            ArrayMap arrayMap = (ArrayMap) this.mAffordabilityCache.get(userId, alarm.sourcePackage);
            if (arrayMap != null) {
                arrayMap.remove(appropriateBill);
            }
        }
    }

    public static /* synthetic */ boolean lambda$maybeUnregisterTareListenerLocked$8(Alarm alarm, EconomyManagerInternal.ActionBill actionBill, Alarm alarm2) {
        return alarm.creatorUid == alarm2.creatorUid && alarm.sourcePackage.equals(alarm2.sourcePackage) && actionBill.equals(TareBill.getAppropriateBill(alarm2));
    }

    @GuardedBy({"mLock"})
    public final void setImplLocked(Alarm alarm) {
        Alarm alarm2;
        if ((alarm.flags & 16) != 0) {
            adjustIdleUntilTime(alarm);
            Alarm alarm3 = this.mPendingIdleUntil;
            if (alarm3 != alarm && alarm3 != null) {
                Slog.wtfStack("AlarmManager", "setImplLocked: idle until changed from " + this.mPendingIdleUntil + " to " + alarm);
                AlarmStore alarmStore = this.mAlarmStore;
                final Alarm alarm4 = this.mPendingIdleUntil;
                Objects.requireNonNull(alarm4);
                alarmStore.remove(new Predicate() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda14
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        return Alarm.this.equals((Alarm) obj);
                    }
                });
            }
            this.mPendingIdleUntil = alarm;
            this.mAlarmStore.updateAlarmDeliveries(new AlarmStore.AlarmDeliveryCalculator() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda15
                @Override // com.android.server.alarm.AlarmStore.AlarmDeliveryCalculator
                public final boolean updateAlarmDelivery(Alarm alarm5) {
                    boolean lambda$setImplLocked$9;
                    lambda$setImplLocked$9 = AlarmManagerService.this.lambda$setImplLocked$9(alarm5);
                    return lambda$setImplLocked$9;
                }
            });
        } else if (this.mPendingIdleUntil != null) {
            lambda$triggerAlarmsLocked$22(alarm);
        }
        if ((alarm.flags & 2) != 0 && ((alarm2 = this.mNextWakeFromIdle) == null || alarm2.getWhenElapsed() > alarm.getWhenElapsed())) {
            this.mNextWakeFromIdle = alarm;
            if (this.mPendingIdleUntil != null && this.mAlarmStore.updateAlarmDeliveries(new AlarmStore.AlarmDeliveryCalculator() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda16
                @Override // com.android.server.alarm.AlarmStore.AlarmDeliveryCalculator
                public final boolean updateAlarmDelivery(Alarm alarm5) {
                    boolean lambda$setImplLocked$10;
                    lambda$setImplLocked$10 = AlarmManagerService.this.lambda$setImplLocked$10(alarm5);
                    return lambda$setImplLocked$10;
                }
            })) {
                this.mAlarmStore.updateAlarmDeliveries(new AlarmStore.AlarmDeliveryCalculator() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda17
                    @Override // com.android.server.alarm.AlarmStore.AlarmDeliveryCalculator
                    public final boolean updateAlarmDelivery(Alarm alarm5) {
                        boolean lambda$setImplLocked$11;
                        lambda$setImplLocked$11 = AlarmManagerService.this.lambda$setImplLocked$11(alarm5);
                        return lambda$setImplLocked$11;
                    }
                });
            }
        }
        if (alarm.alarmClock != null) {
            this.mNextAlarmClockMayChange = true;
        }
        adjustDeliveryTimeBasedOnBatterySaver(alarm);
        adjustDeliveryTimeBasedOnBucketLocked(alarm);
        adjustDeliveryTimeBasedOnTareLocked(alarm);
        registerTareListener(alarm);
        this.mAlarmStore.add(alarm);
        rescheduleKernelAlarmsLocked();
        updateNextAlarmClockLocked();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$setImplLocked$10(Alarm alarm) {
        return alarm == this.mPendingIdleUntil && adjustIdleUntilTime(alarm);
    }

    /* loaded from: classes.dex */
    public final class LocalService implements AlarmManagerInternal {
        public LocalService() {
        }

        @Override // com.android.server.AlarmManagerInternal
        public boolean isIdling() {
            return AlarmManagerService.this.isIdlingImpl();
        }

        @Override // com.android.server.AlarmManagerInternal
        public void removeAlarmsForUid(int i) {
            synchronized (AlarmManagerService.this.mLock) {
                AlarmManagerService.this.removeLocked(i, 3);
            }
        }

        @Override // com.android.server.AlarmManagerInternal
        public void remove(PendingIntent pendingIntent) {
            AlarmManagerService.this.mHandler.obtainMessage(7, pendingIntent).sendToTarget();
        }

        @Override // com.android.server.AlarmManagerInternal
        public boolean shouldGetBucketElevation(String str, int i) {
            return AlarmManagerService.this.hasUseExactAlarmInternal(str, i) || (!CompatChanges.isChangeEnabled(262645982L, str, UserHandle.getUserHandleForUid(i)) && AlarmManagerService.this.hasScheduleExactAlarmInternal(str, i));
        }

        @Override // com.android.server.AlarmManagerInternal
        public void setTimeZone(String str, int i, String str2) {
            AlarmManagerService.this.setTimeZoneImpl(str, i, str2);
        }

        @Override // com.android.server.AlarmManagerInternal
        public void setTime(long j, int i, String str) {
            AlarmManagerService.this.setTimeImpl(j, i, str);
        }

        @Override // com.android.server.AlarmManagerInternal
        public void registerInFlightListener(AlarmManagerInternal.InFlightListener inFlightListener) {
            synchronized (AlarmManagerService.this.mLock) {
                AlarmManagerService.this.mInFlightListeners.add(inFlightListener);
            }
        }
    }

    public boolean hasUseExactAlarmInternal(String str, int i) {
        return isUseExactAlarmEnabled(str, UserHandle.getUserId(i)) && PermissionChecker.checkPermissionForPreflight(getContext(), "android.permission.USE_EXACT_ALARM", -1, i, str) == 0;
    }

    public boolean isScheduleExactAlarmAllowedByDefault(String str, int i) {
        List emptyList;
        if (isScheduleExactAlarmDeniedByDefault(str, UserHandle.getUserId(i))) {
            if (this.mPackageManagerInternal.isPlatformSigned(str) || this.mPackageManagerInternal.isUidPrivileged(i)) {
                return true;
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                RoleManager roleManager = this.mRoleManager;
                if (roleManager != null) {
                    emptyList = roleManager.getRoleHolders("android.app.role.SYSTEM_WELLBEING");
                } else {
                    emptyList = Collections.emptyList();
                }
                return emptyList.contains(str);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return !this.mConstants.EXACT_ALARM_DENY_LIST.contains(str);
    }

    public boolean hasScheduleExactAlarmInternal(String str, int i) {
        long time = this.mStatLogger.getTime();
        boolean z = false;
        if (this.mExactAlarmCandidates.contains(Integer.valueOf(UserHandle.getAppId(i))) && isExactAlarmChangeEnabled(str, UserHandle.getUserId(i))) {
            int checkOpNoThrow = this.mAppOps.checkOpNoThrow(107, i, str);
            if (checkOpNoThrow == 3) {
                z = isScheduleExactAlarmAllowedByDefault(str, i);
            } else if (checkOpNoThrow == 0) {
                z = true;
            }
        }
        this.mStatLogger.logDurationStat(1, time);
        return z;
    }

    public boolean isExemptFromMinWindowRestrictions(int i) {
        return isExemptFromExactAlarmPermissionNoLock(i);
    }

    public boolean isExemptFromExactAlarmPermissionNoLock(int i) {
        DeviceIdleInternal deviceIdleInternal;
        if (Build.IS_DEBUGGABLE && Thread.holdsLock(this.mLock)) {
            Slog.wtfStack("AlarmManager", "Alarm lock held while calling into DeviceIdleController");
        }
        return UserHandle.isSameApp(this.mSystemUiUid, i) || UserHandle.isCore(i) || (deviceIdleInternal = this.mLocalDeviceIdleController) == null || deviceIdleInternal.isAppOnWhitelist(UserHandle.getAppId(i));
    }

    public static boolean isExactAlarmChangeEnabled(String str, int i) {
        return CompatChanges.isChangeEnabled(171306433L, str, UserHandle.of(i));
    }

    public static boolean isUseExactAlarmEnabled(String str, int i) {
        return CompatChanges.isChangeEnabled(218533173L, str, UserHandle.of(i));
    }

    public final boolean isScheduleExactAlarmDeniedByDefault(String str, int i) {
        return CompatChanges.isChangeEnabled(226439802L, str, UserHandle.of(i));
    }

    @NeverCompile
    public void dumpImpl(final IndentingPrintWriter indentingPrintWriter) {
        BroadcastStats broadcastStats;
        long j;
        synchronized (this.mLock) {
            indentingPrintWriter.println("Current Alarm Manager state:");
            indentingPrintWriter.increaseIndent();
            this.mConstants.dump(indentingPrintWriter);
            indentingPrintWriter.println();
            if (this.mConstants.USE_TARE_POLICY == 1) {
                indentingPrintWriter.println("TARE details:");
                indentingPrintWriter.increaseIndent();
                indentingPrintWriter.println("Affordability cache:");
                indentingPrintWriter.increaseIndent();
                this.mAffordabilityCache.forEach(new SparseArrayMap.TriConsumer() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda1
                    public final void accept(int i, Object obj, Object obj2) {
                        AlarmManagerService.lambda$dumpImpl$12(indentingPrintWriter, i, (String) obj, (ArrayMap) obj2);
                    }
                });
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.println();
            } else {
                indentingPrintWriter.println("App Standby Parole: " + this.mAppStandbyParole);
                indentingPrintWriter.println();
            }
            AppStateTrackerImpl appStateTrackerImpl = this.mAppStateTracker;
            if (appStateTrackerImpl != null) {
                appStateTrackerImpl.dump(indentingPrintWriter);
                indentingPrintWriter.println();
            }
            long elapsedRealtimeMillis = this.mInjector.getElapsedRealtimeMillis();
            long uptimeMillis = SystemClock.uptimeMillis();
            long currentTimeMillis = this.mInjector.getCurrentTimeMillis();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            indentingPrintWriter.print("nowRTC=");
            indentingPrintWriter.print(currentTimeMillis);
            indentingPrintWriter.print("=");
            indentingPrintWriter.print(simpleDateFormat.format(new Date(currentTimeMillis)));
            indentingPrintWriter.print(" nowELAPSED=");
            indentingPrintWriter.print(elapsedRealtimeMillis);
            indentingPrintWriter.println();
            indentingPrintWriter.print("mLastTimeChangeClockTime=");
            indentingPrintWriter.print(this.mLastTimeChangeClockTime);
            indentingPrintWriter.print("=");
            indentingPrintWriter.println(simpleDateFormat.format(new Date(this.mLastTimeChangeClockTime)));
            indentingPrintWriter.print("mLastTimeChangeRealtime=");
            indentingPrintWriter.println(this.mLastTimeChangeRealtime);
            indentingPrintWriter.print("mLastTickReceived=");
            indentingPrintWriter.println(simpleDateFormat.format(new Date(this.mLastTickReceived)));
            indentingPrintWriter.print("mLastTickSet=");
            indentingPrintWriter.println(simpleDateFormat.format(new Date(this.mLastTickSet)));
            indentingPrintWriter.println();
            indentingPrintWriter.println("Recent TIME_TICK history:");
            indentingPrintWriter.increaseIndent();
            int i = this.mNextTickHistory;
            while (true) {
                i--;
                if (i < 0) {
                    i = 9;
                }
                long j2 = this.mTickHistory[i];
                indentingPrintWriter.println(j2 > 0 ? simpleDateFormat.format(new Date(currentTimeMillis - (elapsedRealtimeMillis - j2))) : PackageManagerShellCommandDataLoader.STDIN_PATH);
                if (i == this.mNextTickHistory) {
                    break;
                }
            }
            indentingPrintWriter.decreaseIndent();
            SystemServiceManager systemServiceManager = (SystemServiceManager) LocalServices.getService(SystemServiceManager.class);
            if (systemServiceManager != null) {
                indentingPrintWriter.println();
                indentingPrintWriter.print("RuntimeStarted=");
                indentingPrintWriter.print(simpleDateFormat.format(new Date((currentTimeMillis - elapsedRealtimeMillis) + systemServiceManager.getRuntimeStartElapsedTime())));
                if (systemServiceManager.isRuntimeRestarted()) {
                    indentingPrintWriter.print("  (Runtime restarted)");
                }
                indentingPrintWriter.println();
                indentingPrintWriter.print("Runtime uptime (elapsed): ");
                TimeUtils.formatDuration(elapsedRealtimeMillis, systemServiceManager.getRuntimeStartElapsedTime(), indentingPrintWriter);
                indentingPrintWriter.println();
                indentingPrintWriter.print("Runtime uptime (uptime): ");
                TimeUtils.formatDuration(uptimeMillis, systemServiceManager.getRuntimeStartUptime(), indentingPrintWriter);
                indentingPrintWriter.println();
            }
            indentingPrintWriter.println();
            if (!this.mInteractive) {
                indentingPrintWriter.print("Time since non-interactive: ");
                TimeUtils.formatDuration(elapsedRealtimeMillis - this.mNonInteractiveStartTime, indentingPrintWriter);
                indentingPrintWriter.println();
            }
            indentingPrintWriter.print("Max wakeup delay: ");
            TimeUtils.formatDuration(currentNonWakeupFuzzLocked(elapsedRealtimeMillis), indentingPrintWriter);
            indentingPrintWriter.println();
            indentingPrintWriter.print("Time since last dispatch: ");
            TimeUtils.formatDuration(elapsedRealtimeMillis - this.mLastAlarmDeliveryTime, indentingPrintWriter);
            indentingPrintWriter.println();
            indentingPrintWriter.print("Next non-wakeup delivery time: ");
            TimeUtils.formatDuration(this.mNextNonWakeupDeliveryTime, elapsedRealtimeMillis, indentingPrintWriter);
            indentingPrintWriter.println();
            long j3 = currentTimeMillis - elapsedRealtimeMillis;
            long j4 = this.mNextWakeup + j3;
            long j5 = currentTimeMillis;
            long j6 = this.mNextNonWakeup + j3;
            indentingPrintWriter.print("Next non-wakeup alarm: ");
            TimeUtils.formatDuration(this.mNextNonWakeup, elapsedRealtimeMillis, indentingPrintWriter);
            indentingPrintWriter.print(" = ");
            indentingPrintWriter.print(this.mNextNonWakeup);
            indentingPrintWriter.print(" = ");
            indentingPrintWriter.println(simpleDateFormat.format(new Date(j6)));
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.print("set at ");
            TimeUtils.formatDuration(this.mNextNonWakeUpSetAt, elapsedRealtimeMillis, indentingPrintWriter);
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println();
            indentingPrintWriter.print("Next wakeup alarm: ");
            TimeUtils.formatDuration(this.mNextWakeup, elapsedRealtimeMillis, indentingPrintWriter);
            indentingPrintWriter.print(" = ");
            indentingPrintWriter.print(this.mNextWakeup);
            indentingPrintWriter.print(" = ");
            indentingPrintWriter.println(simpleDateFormat.format(new Date(j4)));
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.print("set at ");
            TimeUtils.formatDuration(this.mNextWakeUpSetAt, elapsedRealtimeMillis, indentingPrintWriter);
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println();
            indentingPrintWriter.print("Next kernel non-wakeup alarm: ");
            TimeUtils.formatDuration(this.mInjector.getNextAlarm(3), indentingPrintWriter);
            indentingPrintWriter.println();
            indentingPrintWriter.print("Next kernel wakeup alarm: ");
            TimeUtils.formatDuration(this.mInjector.getNextAlarm(2), indentingPrintWriter);
            indentingPrintWriter.println();
            indentingPrintWriter.print("Last wakeup: ");
            TimeUtils.formatDuration(this.mLastWakeup, elapsedRealtimeMillis, indentingPrintWriter);
            indentingPrintWriter.print(" = ");
            indentingPrintWriter.println(this.mLastWakeup);
            indentingPrintWriter.print("Last trigger: ");
            TimeUtils.formatDuration(this.mLastTrigger, elapsedRealtimeMillis, indentingPrintWriter);
            indentingPrintWriter.print(" = ");
            indentingPrintWriter.println(this.mLastTrigger);
            indentingPrintWriter.print("Num time change events: ");
            indentingPrintWriter.println(this.mNumTimeChanged);
            indentingPrintWriter.println();
            indentingPrintWriter.println("App ids requesting SCHEDULE_EXACT_ALARM: " + this.mExactAlarmCandidates);
            indentingPrintWriter.println();
            indentingPrintWriter.print("Last OP_SCHEDULE_EXACT_ALARM: [");
            int i2 = 0;
            for (int i3 = 0; i3 < this.mLastOpScheduleExactAlarm.size(); i3++) {
                if (i3 > 0) {
                    indentingPrintWriter.print(", ");
                }
                UserHandle.formatUid(indentingPrintWriter, this.mLastOpScheduleExactAlarm.keyAt(i3));
                indentingPrintWriter.print(XmlUtils.STRING_ARRAY_SEPARATOR + AppOpsManager.modeToName(this.mLastOpScheduleExactAlarm.valueAt(i3)));
            }
            indentingPrintWriter.println("]");
            indentingPrintWriter.println();
            indentingPrintWriter.println("Next alarm clock information: ");
            indentingPrintWriter.increaseIndent();
            TreeSet treeSet = new TreeSet();
            for (int i4 = 0; i4 < this.mNextAlarmClockForUser.size(); i4++) {
                treeSet.add(Integer.valueOf(this.mNextAlarmClockForUser.keyAt(i4)));
            }
            for (int i5 = 0; i5 < this.mPendingSendNextAlarmClockChangedForUser.size(); i5++) {
                treeSet.add(Integer.valueOf(this.mPendingSendNextAlarmClockChangedForUser.keyAt(i5)));
            }
            Iterator it = treeSet.iterator();
            while (it.hasNext()) {
                int intValue = ((Integer) it.next()).intValue();
                AlarmManager.AlarmClockInfo alarmClockInfo = this.mNextAlarmClockForUser.get(intValue);
                long triggerTime = alarmClockInfo != null ? alarmClockInfo.getTriggerTime() : 0L;
                boolean z = this.mPendingSendNextAlarmClockChangedForUser.get(intValue);
                indentingPrintWriter.print("user:");
                indentingPrintWriter.print(intValue);
                indentingPrintWriter.print(" pendingSend:");
                indentingPrintWriter.print(z);
                indentingPrintWriter.print(" time:");
                indentingPrintWriter.print(triggerTime);
                if (triggerTime > 0) {
                    indentingPrintWriter.print(" = ");
                    indentingPrintWriter.print(simpleDateFormat.format(new Date(triggerTime)));
                    indentingPrintWriter.print(" = ");
                    j = j5;
                    TimeUtils.formatDuration(triggerTime, j, indentingPrintWriter);
                } else {
                    j = j5;
                }
                indentingPrintWriter.println();
                j5 = j;
            }
            indentingPrintWriter.decreaseIndent();
            if (this.mAlarmStore.size() > 0) {
                indentingPrintWriter.println();
                this.mAlarmStore.dump(indentingPrintWriter, elapsedRealtimeMillis, simpleDateFormat);
            }
            indentingPrintWriter.println();
            indentingPrintWriter.println("Pending user blocked background alarms: ");
            indentingPrintWriter.increaseIndent();
            boolean z2 = false;
            for (int i6 = 0; i6 < this.mPendingBackgroundAlarms.size(); i6++) {
                ArrayList<Alarm> valueAt = this.mPendingBackgroundAlarms.valueAt(i6);
                if (valueAt != null && valueAt.size() > 0) {
                    dumpAlarmList(indentingPrintWriter, valueAt, elapsedRealtimeMillis, simpleDateFormat);
                    z2 = true;
                }
            }
            if (!z2) {
                indentingPrintWriter.println("none");
            }
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println();
            indentingPrintWriter.print("Pending alarms per uid: [");
            for (int i7 = 0; i7 < this.mAlarmsPerUid.size(); i7++) {
                if (i7 > 0) {
                    indentingPrintWriter.print(", ");
                }
                UserHandle.formatUid(indentingPrintWriter, this.mAlarmsPerUid.keyAt(i7));
                indentingPrintWriter.print(XmlUtils.STRING_ARRAY_SEPARATOR);
                indentingPrintWriter.print(this.mAlarmsPerUid.valueAt(i7));
            }
            indentingPrintWriter.println("]");
            indentingPrintWriter.println();
            indentingPrintWriter.println("App Alarm history:");
            this.mAppWakeupHistory.dump(indentingPrintWriter, elapsedRealtimeMillis);
            indentingPrintWriter.println();
            indentingPrintWriter.println("Temporary Quota Reserves:");
            this.mTemporaryQuotaReserve.dump(indentingPrintWriter, elapsedRealtimeMillis);
            if (this.mPendingIdleUntil != null) {
                indentingPrintWriter.println();
                indentingPrintWriter.println("Idle mode state:");
                indentingPrintWriter.increaseIndent();
                indentingPrintWriter.print("Idling until: ");
                Alarm alarm = this.mPendingIdleUntil;
                if (alarm != null) {
                    indentingPrintWriter.println(alarm);
                    this.mPendingIdleUntil.dump(indentingPrintWriter, elapsedRealtimeMillis, simpleDateFormat);
                } else {
                    indentingPrintWriter.println("null");
                }
                indentingPrintWriter.decreaseIndent();
            }
            if (this.mNextWakeFromIdle != null) {
                indentingPrintWriter.println();
                indentingPrintWriter.print("Next wake from idle: ");
                indentingPrintWriter.println(this.mNextWakeFromIdle);
                indentingPrintWriter.increaseIndent();
                this.mNextWakeFromIdle.dump(indentingPrintWriter, elapsedRealtimeMillis, simpleDateFormat);
                indentingPrintWriter.decreaseIndent();
            }
            indentingPrintWriter.println();
            indentingPrintWriter.print("Past-due non-wakeup alarms: ");
            if (this.mPendingNonWakeupAlarms.size() > 0) {
                indentingPrintWriter.println(this.mPendingNonWakeupAlarms.size());
                indentingPrintWriter.increaseIndent();
                dumpAlarmList(indentingPrintWriter, this.mPendingNonWakeupAlarms, elapsedRealtimeMillis, simpleDateFormat);
                indentingPrintWriter.decreaseIndent();
            } else {
                indentingPrintWriter.println("(none)");
            }
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.print("Number of delayed alarms: ");
            indentingPrintWriter.print(this.mNumDelayedAlarms);
            indentingPrintWriter.print(", total delay time: ");
            TimeUtils.formatDuration(this.mTotalDelayTime, indentingPrintWriter);
            indentingPrintWriter.println();
            indentingPrintWriter.print("Max delay time: ");
            TimeUtils.formatDuration(this.mMaxDelayTime, indentingPrintWriter);
            indentingPrintWriter.print(", max non-interactive time: ");
            TimeUtils.formatDuration(this.mNonInteractiveTime, indentingPrintWriter);
            indentingPrintWriter.println();
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println();
            indentingPrintWriter.print("Broadcast ref count: ");
            indentingPrintWriter.println(this.mBroadcastRefCount);
            indentingPrintWriter.print("PendingIntent send count: ");
            indentingPrintWriter.println(this.mSendCount);
            indentingPrintWriter.print("PendingIntent finish count: ");
            indentingPrintWriter.println(this.mSendFinishCount);
            indentingPrintWriter.print("Listener send count: ");
            indentingPrintWriter.println(this.mListenerCount);
            indentingPrintWriter.print("Listener finish count: ");
            indentingPrintWriter.println(this.mListenerFinishCount);
            indentingPrintWriter.println();
            if (this.mInFlight.size() > 0) {
                indentingPrintWriter.println("Outstanding deliveries:");
                indentingPrintWriter.increaseIndent();
                for (int i8 = 0; i8 < this.mInFlight.size(); i8++) {
                    indentingPrintWriter.print("#");
                    indentingPrintWriter.print(i8);
                    indentingPrintWriter.print(": ");
                    indentingPrintWriter.println(this.mInFlight.get(i8));
                }
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.println();
            }
            indentingPrintWriter.println("Allow while idle history:");
            this.mAllowWhileIdleHistory.dump(indentingPrintWriter, elapsedRealtimeMillis);
            indentingPrintWriter.println();
            indentingPrintWriter.println("Allow while idle compat history:");
            this.mAllowWhileIdleCompatHistory.dump(indentingPrintWriter, elapsedRealtimeMillis);
            indentingPrintWriter.println();
            if (this.mLastPriorityAlarmDispatch.size() > 0) {
                indentingPrintWriter.println("Last priority alarm dispatches:");
                indentingPrintWriter.increaseIndent();
                for (int i9 = 0; i9 < this.mLastPriorityAlarmDispatch.size(); i9++) {
                    indentingPrintWriter.print("UID: ");
                    UserHandle.formatUid(indentingPrintWriter, this.mLastPriorityAlarmDispatch.keyAt(i9));
                    indentingPrintWriter.print(": ");
                    TimeUtils.formatDuration(this.mLastPriorityAlarmDispatch.valueAt(i9), elapsedRealtimeMillis, indentingPrintWriter);
                    indentingPrintWriter.println();
                }
                indentingPrintWriter.decreaseIndent();
            }
            if (this.mRemovalHistory.size() > 0) {
                indentingPrintWriter.println("Removal history: ");
                indentingPrintWriter.increaseIndent();
                for (int i10 = 0; i10 < this.mRemovalHistory.size(); i10++) {
                    UserHandle.formatUid(indentingPrintWriter, this.mRemovalHistory.keyAt(i10));
                    indentingPrintWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                    indentingPrintWriter.increaseIndent();
                    for (RemovedAlarm removedAlarm : (RemovedAlarm[]) this.mRemovalHistory.valueAt(i10).toArray()) {
                        removedAlarm.dump(indentingPrintWriter, elapsedRealtimeMillis, simpleDateFormat);
                    }
                    indentingPrintWriter.decreaseIndent();
                }
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.println();
            }
            if (this.mLog.dump(indentingPrintWriter, "Recent problems:")) {
                indentingPrintWriter.println();
            }
            FilterStats[] filterStatsArr = new FilterStats[10];
            Comparator<FilterStats> comparator = new Comparator<FilterStats>() { // from class: com.android.server.alarm.AlarmManagerService.6
                @Override // java.util.Comparator
                public int compare(FilterStats filterStats, FilterStats filterStats2) {
                    long j7 = filterStats.aggregateTime;
                    long j8 = filterStats2.aggregateTime;
                    if (j7 < j8) {
                        return 1;
                    }
                    return j7 > j8 ? -1 : 0;
                }
            };
            int i11 = 0;
            int i12 = 0;
            while (i11 < this.mBroadcastStats.size()) {
                ArrayMap<String, BroadcastStats> valueAt2 = this.mBroadcastStats.valueAt(i11);
                int i13 = i2;
                while (i13 < valueAt2.size()) {
                    BroadcastStats valueAt3 = valueAt2.valueAt(i13);
                    int i14 = i2;
                    while (i14 < valueAt3.filterStats.size()) {
                        FilterStats valueAt4 = valueAt3.filterStats.valueAt(i14);
                        if (i12 > 0) {
                            i2 = Arrays.binarySearch(filterStatsArr, i2, i12, valueAt4, comparator);
                        }
                        if (i2 < 0) {
                            i2 = (-i2) - 1;
                        }
                        ArrayMap<String, BroadcastStats> arrayMap = valueAt2;
                        if (i2 < 10) {
                            int i15 = (10 - i2) - 1;
                            if (i15 > 0) {
                                broadcastStats = valueAt3;
                                System.arraycopy(filterStatsArr, i2, filterStatsArr, i2 + 1, i15);
                            } else {
                                broadcastStats = valueAt3;
                            }
                            filterStatsArr[i2] = valueAt4;
                            if (i12 < 10) {
                                i12++;
                            }
                        } else {
                            broadcastStats = valueAt3;
                        }
                        i14++;
                        valueAt2 = arrayMap;
                        valueAt3 = broadcastStats;
                        i2 = 0;
                    }
                    i13++;
                    i2 = 0;
                }
                i11++;
                i2 = 0;
            }
            if (i12 > 0) {
                indentingPrintWriter.println("Top Alarms:");
                indentingPrintWriter.increaseIndent();
                for (int i16 = 0; i16 < i12; i16++) {
                    FilterStats filterStats = filterStatsArr[i16];
                    if (filterStats.nesting > 0) {
                        indentingPrintWriter.print("*ACTIVE* ");
                    }
                    TimeUtils.formatDuration(filterStats.aggregateTime, indentingPrintWriter);
                    indentingPrintWriter.print(" running, ");
                    indentingPrintWriter.print(filterStats.numWakeup);
                    indentingPrintWriter.print(" wakeups, ");
                    indentingPrintWriter.print(filterStats.count);
                    indentingPrintWriter.print(" alarms: ");
                    UserHandle.formatUid(indentingPrintWriter, filterStats.mBroadcastStats.mUid);
                    indentingPrintWriter.print(XmlUtils.STRING_ARRAY_SEPARATOR);
                    indentingPrintWriter.print(filterStats.mBroadcastStats.mPackageName);
                    indentingPrintWriter.println();
                    indentingPrintWriter.increaseIndent();
                    indentingPrintWriter.print(filterStats.mTag);
                    indentingPrintWriter.println();
                    indentingPrintWriter.decreaseIndent();
                }
                indentingPrintWriter.decreaseIndent();
            }
            indentingPrintWriter.println();
            indentingPrintWriter.println("Alarm Stats:");
            ArrayList arrayList = new ArrayList();
            for (int i17 = 0; i17 < this.mBroadcastStats.size(); i17++) {
                ArrayMap<String, BroadcastStats> valueAt5 = this.mBroadcastStats.valueAt(i17);
                for (int i18 = 0; i18 < valueAt5.size(); i18++) {
                    BroadcastStats valueAt6 = valueAt5.valueAt(i18);
                    if (valueAt6.nesting > 0) {
                        indentingPrintWriter.print("*ACTIVE* ");
                    }
                    UserHandle.formatUid(indentingPrintWriter, valueAt6.mUid);
                    indentingPrintWriter.print(XmlUtils.STRING_ARRAY_SEPARATOR);
                    indentingPrintWriter.print(valueAt6.mPackageName);
                    indentingPrintWriter.print(" ");
                    TimeUtils.formatDuration(valueAt6.aggregateTime, indentingPrintWriter);
                    indentingPrintWriter.print(" running, ");
                    indentingPrintWriter.print(valueAt6.numWakeup);
                    indentingPrintWriter.println(" wakeups:");
                    arrayList.clear();
                    for (int i19 = 0; i19 < valueAt6.filterStats.size(); i19++) {
                        arrayList.add(valueAt6.filterStats.valueAt(i19));
                    }
                    Collections.sort(arrayList, comparator);
                    indentingPrintWriter.increaseIndent();
                    for (int i20 = 0; i20 < arrayList.size(); i20++) {
                        FilterStats filterStats2 = (FilterStats) arrayList.get(i20);
                        if (filterStats2.nesting > 0) {
                            indentingPrintWriter.print("*ACTIVE* ");
                        }
                        TimeUtils.formatDuration(filterStats2.aggregateTime, indentingPrintWriter);
                        indentingPrintWriter.print(" ");
                        indentingPrintWriter.print(filterStats2.numWakeup);
                        indentingPrintWriter.print(" wakes ");
                        indentingPrintWriter.print(filterStats2.count);
                        indentingPrintWriter.print(" alarms, last ");
                        TimeUtils.formatDuration(filterStats2.lastTime, elapsedRealtimeMillis, indentingPrintWriter);
                        indentingPrintWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                        indentingPrintWriter.increaseIndent();
                        indentingPrintWriter.print(filterStats2.mTag);
                        indentingPrintWriter.println();
                        indentingPrintWriter.decreaseIndent();
                    }
                    indentingPrintWriter.decreaseIndent();
                }
            }
            indentingPrintWriter.println();
            this.mStatLogger.dump(indentingPrintWriter);
        }
    }

    public static /* synthetic */ void lambda$dumpImpl$12(IndentingPrintWriter indentingPrintWriter, int i, String str, ArrayMap arrayMap) {
        int size = arrayMap.size();
        if (size > 0) {
            indentingPrintWriter.print(i);
            indentingPrintWriter.print(XmlUtils.STRING_ARRAY_SEPARATOR);
            indentingPrintWriter.print(str);
            indentingPrintWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
            indentingPrintWriter.increaseIndent();
            for (int i2 = 0; i2 < size; i2++) {
                indentingPrintWriter.print(TareBill.getName((EconomyManagerInternal.ActionBill) arrayMap.keyAt(i2)));
                indentingPrintWriter.print(": ");
                indentingPrintWriter.println(arrayMap.valueAt(i2));
            }
            indentingPrintWriter.decreaseIndent();
        }
    }

    public void dumpProto(FileDescriptor fileDescriptor) {
        ProtoOutputStream protoOutputStream = new ProtoOutputStream(fileDescriptor);
        synchronized (this.mLock) {
            long currentTimeMillis = this.mInjector.getCurrentTimeMillis();
            long elapsedRealtimeMillis = this.mInjector.getElapsedRealtimeMillis();
            protoOutputStream.write(1112396529665L, currentTimeMillis);
            protoOutputStream.write(1112396529666L, elapsedRealtimeMillis);
            protoOutputStream.write(1112396529667L, this.mLastTimeChangeClockTime);
            protoOutputStream.write(1112396529668L, this.mLastTimeChangeRealtime);
            this.mConstants.dumpProto(protoOutputStream, 1146756268037L);
            AppStateTrackerImpl appStateTrackerImpl = this.mAppStateTracker;
            if (appStateTrackerImpl != null) {
                appStateTrackerImpl.dumpProto(protoOutputStream, 1146756268038L);
            }
            protoOutputStream.write(1133871366151L, this.mInteractive);
            if (!this.mInteractive) {
                protoOutputStream.write(1112396529672L, elapsedRealtimeMillis - this.mNonInteractiveStartTime);
                protoOutputStream.write(1112396529673L, currentNonWakeupFuzzLocked(elapsedRealtimeMillis));
                protoOutputStream.write(1112396529674L, elapsedRealtimeMillis - this.mLastAlarmDeliveryTime);
                protoOutputStream.write(1112396529675L, elapsedRealtimeMillis - this.mNextNonWakeupDeliveryTime);
            }
            protoOutputStream.write(1112396529676L, this.mNextNonWakeup - elapsedRealtimeMillis);
            protoOutputStream.write(1112396529677L, this.mNextWakeup - elapsedRealtimeMillis);
            protoOutputStream.write(1112396529678L, elapsedRealtimeMillis - this.mLastWakeup);
            protoOutputStream.write(1112396529679L, elapsedRealtimeMillis - this.mNextWakeUpSetAt);
            protoOutputStream.write(1112396529680L, this.mNumTimeChanged);
            TreeSet treeSet = new TreeSet();
            int size = this.mNextAlarmClockForUser.size();
            int i = 0;
            for (int i2 = 0; i2 < size; i2++) {
                treeSet.add(Integer.valueOf(this.mNextAlarmClockForUser.keyAt(i2)));
            }
            int size2 = this.mPendingSendNextAlarmClockChangedForUser.size();
            for (int i3 = 0; i3 < size2; i3++) {
                treeSet.add(Integer.valueOf(this.mPendingSendNextAlarmClockChangedForUser.keyAt(i3)));
            }
            Iterator it = treeSet.iterator();
            while (it.hasNext()) {
                int intValue = ((Integer) it.next()).intValue();
                AlarmManager.AlarmClockInfo alarmClockInfo = this.mNextAlarmClockForUser.get(intValue);
                long triggerTime = alarmClockInfo != null ? alarmClockInfo.getTriggerTime() : 0L;
                boolean z = this.mPendingSendNextAlarmClockChangedForUser.get(intValue);
                long start = protoOutputStream.start(2246267895826L);
                protoOutputStream.write(1120986464257L, intValue);
                protoOutputStream.write(1133871366146L, z);
                protoOutputStream.write(1112396529667L, triggerTime);
                protoOutputStream.end(start);
            }
            this.mAlarmStore.dumpProto(protoOutputStream, elapsedRealtimeMillis);
            for (int i4 = 0; i4 < this.mPendingBackgroundAlarms.size(); i4++) {
                ArrayList<Alarm> valueAt = this.mPendingBackgroundAlarms.valueAt(i4);
                if (valueAt != null) {
                    Iterator<Alarm> it2 = valueAt.iterator();
                    while (it2.hasNext()) {
                        it2.next().dumpDebug(protoOutputStream, 2246267895828L, elapsedRealtimeMillis);
                    }
                }
            }
            Alarm alarm = this.mPendingIdleUntil;
            if (alarm != null) {
                alarm.dumpDebug(protoOutputStream, 1146756268053L, elapsedRealtimeMillis);
            }
            Alarm alarm2 = this.mNextWakeFromIdle;
            if (alarm2 != null) {
                alarm2.dumpDebug(protoOutputStream, 1146756268055L, elapsedRealtimeMillis);
            }
            Iterator<Alarm> it3 = this.mPendingNonWakeupAlarms.iterator();
            while (it3.hasNext()) {
                it3.next().dumpDebug(protoOutputStream, 2246267895832L, elapsedRealtimeMillis);
            }
            protoOutputStream.write(1120986464281L, this.mNumDelayedAlarms);
            protoOutputStream.write(1112396529690L, this.mTotalDelayTime);
            protoOutputStream.write(1112396529691L, this.mMaxDelayTime);
            protoOutputStream.write(1112396529692L, this.mNonInteractiveTime);
            protoOutputStream.write(1120986464285L, this.mBroadcastRefCount);
            protoOutputStream.write(1120986464286L, this.mSendCount);
            protoOutputStream.write(1120986464287L, this.mSendFinishCount);
            protoOutputStream.write(1120986464288L, this.mListenerCount);
            protoOutputStream.write(1120986464289L, this.mListenerFinishCount);
            Iterator<InFlight> it4 = this.mInFlight.iterator();
            while (it4.hasNext()) {
                it4.next().dumpDebug(protoOutputStream, 2246267895842L);
            }
            this.mLog.dumpDebug(protoOutputStream, 1146756268069L);
            FilterStats[] filterStatsArr = new FilterStats[10];
            Comparator<FilterStats> comparator = new Comparator<FilterStats>() { // from class: com.android.server.alarm.AlarmManagerService.7
                @Override // java.util.Comparator
                public int compare(FilterStats filterStats, FilterStats filterStats2) {
                    long j = filterStats.aggregateTime;
                    long j2 = filterStats2.aggregateTime;
                    if (j < j2) {
                        return 1;
                    }
                    return j > j2 ? -1 : 0;
                }
            };
            int i5 = 0;
            int i6 = 0;
            while (i5 < this.mBroadcastStats.size()) {
                ArrayMap<String, BroadcastStats> valueAt2 = this.mBroadcastStats.valueAt(i5);
                int i7 = i;
                while (i7 < valueAt2.size()) {
                    BroadcastStats valueAt3 = valueAt2.valueAt(i7);
                    int i8 = i;
                    while (i8 < valueAt3.filterStats.size()) {
                        FilterStats valueAt4 = valueAt3.filterStats.valueAt(i8);
                        if (i6 > 0) {
                            i = Arrays.binarySearch(filterStatsArr, i, i6, valueAt4, comparator);
                        }
                        if (i < 0) {
                            i = (-i) - 1;
                        }
                        if (i < 10) {
                            int i9 = (10 - i) - 1;
                            if (i9 > 0) {
                                System.arraycopy(filterStatsArr, i, filterStatsArr, i + 1, i9);
                            }
                            filterStatsArr[i] = valueAt4;
                            if (i6 < 10) {
                                i6++;
                            }
                        }
                        i8++;
                        i = 0;
                    }
                    i7++;
                    i = 0;
                }
                i5++;
                i = 0;
            }
            for (int i10 = 0; i10 < i6; i10++) {
                long start2 = protoOutputStream.start(2246267895846L);
                FilterStats filterStats = filterStatsArr[i10];
                protoOutputStream.write(1120986464257L, filterStats.mBroadcastStats.mUid);
                protoOutputStream.write(1138166333442L, filterStats.mBroadcastStats.mPackageName);
                filterStats.dumpDebug(protoOutputStream, 1146756268035L);
                protoOutputStream.end(start2);
            }
            ArrayList arrayList = new ArrayList();
            for (int i11 = 0; i11 < this.mBroadcastStats.size(); i11++) {
                ArrayMap<String, BroadcastStats> valueAt5 = this.mBroadcastStats.valueAt(i11);
                for (int i12 = 0; i12 < valueAt5.size(); i12++) {
                    long start3 = protoOutputStream.start(2246267895847L);
                    BroadcastStats valueAt6 = valueAt5.valueAt(i12);
                    valueAt6.dumpDebug(protoOutputStream, 1146756268033L);
                    arrayList.clear();
                    for (int i13 = 0; i13 < valueAt6.filterStats.size(); i13++) {
                        arrayList.add(valueAt6.filterStats.valueAt(i13));
                    }
                    Collections.sort(arrayList, comparator);
                    Iterator it5 = arrayList.iterator();
                    while (it5.hasNext()) {
                        ((FilterStats) it5.next()).dumpDebug(protoOutputStream, 2246267895810L);
                    }
                    protoOutputStream.end(start3);
                }
            }
        }
        protoOutputStream.flush();
    }

    public long getNextWakeFromIdleTimeImpl() {
        long whenElapsed;
        synchronized (this.mLock) {
            Alarm alarm = this.mNextWakeFromIdle;
            whenElapsed = alarm != null ? alarm.getWhenElapsed() : Long.MAX_VALUE;
        }
        return whenElapsed;
    }

    public final boolean isIdlingImpl() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mPendingIdleUntil != null;
        }
        return z;
    }

    public AlarmManager.AlarmClockInfo getNextAlarmClockImpl(int i) {
        AlarmManager.AlarmClockInfo alarmClockInfo;
        synchronized (this.mLock) {
            alarmClockInfo = this.mNextAlarmClockForUser.get(i);
        }
        return alarmClockInfo;
    }

    public final void updateNextAlarmClockLocked() {
        if (this.mNextAlarmClockMayChange) {
            this.mNextAlarmClockMayChange = false;
            SparseArray<AlarmManager.AlarmClockInfo> sparseArray = this.mTmpSparseAlarmClockArray;
            sparseArray.clear();
            Iterator<Alarm> it = this.mAlarmStore.asList().iterator();
            while (it.hasNext()) {
                Alarm next = it.next();
                if (next.alarmClock != null) {
                    int userId = UserHandle.getUserId(next.uid);
                    AlarmManager.AlarmClockInfo alarmClockInfo = this.mNextAlarmClockForUser.get(userId);
                    if (sparseArray.get(userId) == null) {
                        sparseArray.put(userId, next.alarmClock);
                    } else if (next.alarmClock.equals(alarmClockInfo) && alarmClockInfo.getTriggerTime() <= sparseArray.get(userId).getTriggerTime()) {
                        sparseArray.put(userId, alarmClockInfo);
                    }
                }
            }
            int size = sparseArray.size();
            for (int i = 0; i < size; i++) {
                AlarmManager.AlarmClockInfo valueAt = sparseArray.valueAt(i);
                int keyAt = sparseArray.keyAt(i);
                if (!valueAt.equals(this.mNextAlarmClockForUser.get(keyAt))) {
                    updateNextAlarmInfoForUserLocked(keyAt, valueAt);
                }
            }
            for (int size2 = this.mNextAlarmClockForUser.size() - 1; size2 >= 0; size2--) {
                int keyAt2 = this.mNextAlarmClockForUser.keyAt(size2);
                if (sparseArray.get(keyAt2) == null) {
                    updateNextAlarmInfoForUserLocked(keyAt2, null);
                }
            }
        }
    }

    public final void updateNextAlarmInfoForUserLocked(int i, AlarmManager.AlarmClockInfo alarmClockInfo) {
        if (alarmClockInfo != null) {
            this.mNextAlarmClockForUser.put(i, alarmClockInfo);
        } else {
            this.mNextAlarmClockForUser.remove(i);
        }
        this.mPendingSendNextAlarmClockChangedForUser.put(i, true);
        this.mHandler.removeMessages(2);
        this.mHandler.sendEmptyMessage(2);
    }

    public final void sendNextAlarmClockChanged() {
        int i;
        SparseArray<AlarmManager.AlarmClockInfo> sparseArray = this.mHandlerSparseAlarmClockArray;
        sparseArray.clear();
        synchronized (this.mLock) {
            int size = this.mPendingSendNextAlarmClockChangedForUser.size();
            for (int i2 = 0; i2 < size; i2++) {
                int keyAt = this.mPendingSendNextAlarmClockChangedForUser.keyAt(i2);
                sparseArray.append(keyAt, this.mNextAlarmClockForUser.get(keyAt));
            }
            this.mPendingSendNextAlarmClockChangedForUser.clear();
        }
        int size2 = sparseArray.size();
        for (i = 0; i < size2; i++) {
            int keyAt2 = sparseArray.keyAt(i);
            Settings.System.putStringForUser(getContext().getContentResolver(), "next_alarm_formatted", formatNextAlarm(getContext(), sparseArray.valueAt(i), keyAt2), keyAt2);
            getContext().sendBroadcastAsUser(NEXT_ALARM_CLOCK_CHANGED_INTENT, new UserHandle(keyAt2));
        }
    }

    public static String formatNextAlarm(Context context, AlarmManager.AlarmClockInfo alarmClockInfo, int i) {
        return alarmClockInfo == null ? "" : DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), DateFormat.is24HourFormat(context, i) ? "EHm" : "Ehma"), alarmClockInfo.getTriggerTime()).toString();
    }

    /* JADX WARN: Code restructure failed: missing block: B:8:0x002a, code lost:
        if (r7 != r5) goto L8;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void rescheduleKernelAlarmsLocked() {
        long j;
        long elapsedRealtimeMillis = this.mInjector.getElapsedRealtimeMillis();
        if (this.mAlarmStore.size() > 0) {
            long nextWakeupDeliveryTime = this.mAlarmStore.getNextWakeupDeliveryTime();
            j = this.mAlarmStore.getNextDeliveryTime();
            if (nextWakeupDeliveryTime != 0) {
                this.mNextWakeup = nextWakeupDeliveryTime;
                this.mNextWakeUpSetAt = elapsedRealtimeMillis;
                setLocked(2, nextWakeupDeliveryTime);
            }
        }
        j = 0;
        if (this.mPendingNonWakeupAlarms.size() > 0 && (j == 0 || this.mNextNonWakeupDeliveryTime < j)) {
            j = this.mNextNonWakeupDeliveryTime;
        }
        if (j != 0) {
            this.mNextNonWakeup = j;
            this.mNextNonWakeUpSetAt = elapsedRealtimeMillis;
            setLocked(3, j);
        }
    }

    public void handleChangesToExactAlarmDenyList(ArraySet<String> arraySet, boolean z) {
        int i;
        StringBuilder sb = new StringBuilder();
        sb.append("Packages ");
        sb.append(arraySet);
        sb.append(z ? " added to" : " removed from");
        sb.append(" the exact alarm deny list.");
        Slog.w("AlarmManager", sb.toString());
        int[] startedUserIds = this.mActivityManagerInternal.getStartedUserIds();
        for (int i2 = 0; i2 < arraySet.size(); i2++) {
            String valueAt = arraySet.valueAt(i2);
            for (int i3 : startedUserIds) {
                int packageUid = this.mPackageManagerInternal.getPackageUid(valueAt, 0L, i3);
                if (packageUid > 0 && isExactAlarmChangeEnabled(valueAt, i3) && !isScheduleExactAlarmDeniedByDefault(valueAt, i3) && !hasUseExactAlarmInternal(valueAt, packageUid) && this.mExactAlarmCandidates.contains(Integer.valueOf(UserHandle.getAppId(packageUid)))) {
                    synchronized (this.mLock) {
                        i = this.mLastOpScheduleExactAlarm.get(packageUid, AppOpsManager.opToDefaultMode(107));
                    }
                    if (i == 3) {
                        if (z) {
                            removeExactAlarmsOnPermissionRevoked(packageUid, valueAt, true);
                        } else {
                            sendScheduleExactAlarmPermissionStateChangedBroadcast(valueAt, i3);
                        }
                    }
                }
            }
        }
    }

    public void removeExactAlarmsOnPermissionRevoked(final int i, final String str, boolean z) {
        if (isExemptFromExactAlarmPermissionNoLock(i) || !isExactAlarmChangeEnabled(str, UserHandle.getUserId(i))) {
            return;
        }
        Slog.w("AlarmManager", "Package " + str + ", uid " + i + " lost permission to set exact alarms!");
        Predicate<Alarm> predicate = new Predicate() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda10
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$removeExactAlarmsOnPermissionRevoked$13;
                lambda$removeExactAlarmsOnPermissionRevoked$13 = AlarmManagerService.lambda$removeExactAlarmsOnPermissionRevoked$13(i, str, (Alarm) obj);
                return lambda$removeExactAlarmsOnPermissionRevoked$13;
            }
        };
        synchronized (this.mLock) {
            removeAlarmsInternalLocked(predicate, 2);
        }
        if (z && this.mConstants.KILL_ON_SCHEDULE_EXACT_ALARM_REVOKED) {
            PermissionManagerService.killUid(UserHandle.getAppId(i), UserHandle.getUserId(i), "schedule_exact_alarm revoked");
        }
    }

    public static /* synthetic */ boolean lambda$removeExactAlarmsOnPermissionRevoked$13(int i, String str, Alarm alarm) {
        return alarm.uid == i && alarm.packageName.equals(str) && alarm.windowLength == 0;
    }

    @GuardedBy({"mLock"})
    public final void removeAlarmsInternalLocked(Predicate<Alarm> predicate, int i) {
        boolean z;
        long currentTimeMillis = this.mInjector.getCurrentTimeMillis();
        long elapsedRealtimeMillis = this.mInjector.getElapsedRealtimeMillis();
        ArrayList<Alarm> remove = this.mAlarmStore.remove(predicate);
        int i2 = 1;
        boolean z2 = !remove.isEmpty();
        for (int size = this.mPendingBackgroundAlarms.size() - 1; size >= 0; size--) {
            ArrayList<Alarm> valueAt = this.mPendingBackgroundAlarms.valueAt(size);
            for (int size2 = valueAt.size() - 1; size2 >= 0; size2--) {
                if (predicate.test(valueAt.get(size2))) {
                    remove.add(valueAt.remove(size2));
                }
            }
            if (valueAt.size() == 0) {
                this.mPendingBackgroundAlarms.removeAt(size);
            }
        }
        for (int size3 = this.mPendingNonWakeupAlarms.size() - 1; size3 >= 0; size3--) {
            if (predicate.test(this.mPendingNonWakeupAlarms.get(size3))) {
                remove.add(this.mPendingNonWakeupAlarms.remove(size3));
            }
        }
        Iterator<Alarm> it = remove.iterator();
        while (it.hasNext()) {
            Alarm next = it.next();
            decrementAlarmCount(next.uid, i2);
            IAlarmListener iAlarmListener = next.listener;
            if (iAlarmListener != null) {
                iAlarmListener.asBinder().unlinkToDeath(this.mListenerDeathRecipient, 0);
            }
            if (RemovedAlarm.isLoggable(i)) {
                RingBuffer<RemovedAlarm> ringBuffer = this.mRemovalHistory.get(next.uid);
                if (ringBuffer == null) {
                    ringBuffer = new RingBuffer<>(RemovedAlarm.class, 10);
                    this.mRemovalHistory.put(next.uid, ringBuffer);
                }
                ringBuffer.append(new RemovedAlarm(next, i, currentTimeMillis, elapsedRealtimeMillis));
                maybeUnregisterTareListenerLocked(next);
                it = it;
                currentTimeMillis = currentTimeMillis;
                i2 = 1;
            }
        }
        if (z2) {
            Alarm alarm = this.mPendingIdleUntil;
            if (alarm == null || !predicate.test(alarm)) {
                z = false;
            } else {
                this.mPendingIdleUntil = null;
                z = true;
            }
            Alarm alarm2 = this.mNextWakeFromIdle;
            if (alarm2 != null && predicate.test(alarm2)) {
                this.mNextWakeFromIdle = this.mAlarmStore.getNextWakeFromIdleAlarm();
                if (this.mPendingIdleUntil != null) {
                    z |= this.mAlarmStore.updateAlarmDeliveries(new AlarmStore.AlarmDeliveryCalculator() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda8
                        @Override // com.android.server.alarm.AlarmStore.AlarmDeliveryCalculator
                        public final boolean updateAlarmDelivery(Alarm alarm3) {
                            boolean lambda$removeAlarmsInternalLocked$14;
                            lambda$removeAlarmsInternalLocked$14 = AlarmManagerService.this.lambda$removeAlarmsInternalLocked$14(alarm3);
                            return lambda$removeAlarmsInternalLocked$14;
                        }
                    });
                }
            }
            if (z) {
                this.mAlarmStore.updateAlarmDeliveries(new AlarmStore.AlarmDeliveryCalculator() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda9
                    @Override // com.android.server.alarm.AlarmStore.AlarmDeliveryCalculator
                    public final boolean updateAlarmDelivery(Alarm alarm3) {
                        boolean lambda$removeAlarmsInternalLocked$15;
                        lambda$removeAlarmsInternalLocked$15 = AlarmManagerService.this.lambda$removeAlarmsInternalLocked$15(alarm3);
                        return lambda$removeAlarmsInternalLocked$15;
                    }
                });
            }
            rescheduleKernelAlarmsLocked();
            updateNextAlarmClockLocked();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$removeAlarmsInternalLocked$14(Alarm alarm) {
        return alarm == this.mPendingIdleUntil && adjustIdleUntilTime(alarm);
    }

    @GuardedBy({"mLock"})
    public void removeLocked(final PendingIntent pendingIntent, final IAlarmListener iAlarmListener, int i) {
        if (pendingIntent == null && iAlarmListener == null) {
            return;
        }
        removeAlarmsInternalLocked(new Predicate() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean matches;
                matches = ((Alarm) obj).matches(pendingIntent, iAlarmListener);
                return matches;
            }
        }, i);
    }

    public static /* synthetic */ boolean lambda$removeLocked$17(int i, Alarm alarm) {
        return alarm.uid == i;
    }

    @GuardedBy({"mLock"})
    public void removeLocked(final int i, int i2) {
        if (i == 1000) {
            return;
        }
        removeAlarmsInternalLocked(new Predicate() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda18
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$removeLocked$17;
                lambda$removeLocked$17 = AlarmManagerService.lambda$removeLocked$17(i, (Alarm) obj);
                return lambda$removeLocked$17;
            }
        }, i2);
    }

    @GuardedBy({"mLock"})
    public void removeLocked(final String str, int i) {
        if (str == null) {
            return;
        }
        removeAlarmsInternalLocked(new Predicate() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda13
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean matches;
                matches = ((Alarm) obj).matches(str);
                return matches;
            }
        }, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$removeForStoppedLocked$19(int i, Alarm alarm) {
        return alarm.uid == i && this.mActivityManagerInternal.isAppStartModeDisabled(i, alarm.packageName);
    }

    @GuardedBy({"mLock"})
    public void removeForStoppedLocked(final int i) {
        if (i == 1000) {
            return;
        }
        removeAlarmsInternalLocked(new Predicate() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda24
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$removeForStoppedLocked$19;
                lambda$removeForStoppedLocked$19 = AlarmManagerService.this.lambda$removeForStoppedLocked$19(i, (Alarm) obj);
                return lambda$removeForStoppedLocked$19;
            }
        }, 0);
    }

    @GuardedBy({"mLock"})
    public void removeUserLocked(final int i) {
        if (i == 0) {
            Slog.w("AlarmManager", "Ignoring attempt to remove system-user state!");
            return;
        }
        removeAlarmsInternalLocked(new Predicate() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda6
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$removeUserLocked$20;
                lambda$removeUserLocked$20 = AlarmManagerService.lambda$removeUserLocked$20(i, (Alarm) obj);
                return lambda$removeUserLocked$20;
            }
        }, 0);
        for (int size = this.mLastPriorityAlarmDispatch.size() - 1; size >= 0; size--) {
            if (UserHandle.getUserId(this.mLastPriorityAlarmDispatch.keyAt(size)) == i) {
                this.mLastPriorityAlarmDispatch.removeAt(size);
            }
        }
        for (int size2 = this.mRemovalHistory.size() - 1; size2 >= 0; size2--) {
            if (UserHandle.getUserId(this.mRemovalHistory.keyAt(size2)) == i) {
                this.mRemovalHistory.removeAt(size2);
            }
        }
        for (int size3 = this.mLastOpScheduleExactAlarm.size() - 1; size3 >= 0; size3--) {
            if (UserHandle.getUserId(this.mLastOpScheduleExactAlarm.keyAt(size3)) == i) {
                this.mLastOpScheduleExactAlarm.removeAt(size3);
            }
        }
    }

    public static /* synthetic */ boolean lambda$removeUserLocked$20(int i, Alarm alarm) {
        return UserHandle.getUserId(alarm.uid) == i;
    }

    @GuardedBy({"mLock"})
    public void interactiveStateChangedLocked(boolean z) {
        if (this.mInteractive != z) {
            this.mInteractive = z;
            long elapsedRealtimeMillis = this.mInjector.getElapsedRealtimeMillis();
            if (z) {
                if (this.mPendingNonWakeupAlarms.size() > 0) {
                    long j = elapsedRealtimeMillis - this.mStartCurrentDelayTime;
                    this.mTotalDelayTime += j;
                    if (this.mMaxDelayTime < j) {
                        this.mMaxDelayTime = j;
                    }
                    deliverAlarmsLocked(new ArrayList<>(this.mPendingNonWakeupAlarms), elapsedRealtimeMillis);
                    this.mPendingNonWakeupAlarms.clear();
                }
                long j2 = this.mNonInteractiveStartTime;
                if (j2 > 0) {
                    long j3 = elapsedRealtimeMillis - j2;
                    if (j3 > this.mNonInteractiveTime) {
                        this.mNonInteractiveTime = j3;
                    }
                }
                this.mHandler.post(new Runnable() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        AlarmManagerService.this.lambda$interactiveStateChangedLocked$21();
                    }
                });
                return;
            }
            this.mNonInteractiveStartTime = elapsedRealtimeMillis;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$interactiveStateChangedLocked$21() {
        getContext().sendBroadcastAsUser(this.mTimeTickIntent, UserHandle.ALL, null, this.mTimeTickOptions);
    }

    public boolean lookForPackageLocked(String str) {
        Iterator<Alarm> it = this.mAlarmStore.asList().iterator();
        while (it.hasNext()) {
            if (it.next().matches(str)) {
                return true;
            }
        }
        return false;
    }

    public final void setLocked(int i, long j) {
        if (this.mInjector.isAlarmDriverPresent()) {
            this.mInjector.setAlarm(i, j);
            return;
        }
        Message obtain = Message.obtain();
        obtain.what = 1;
        this.mHandler.removeMessages(1);
        this.mHandler.sendMessageAtTime(obtain, j);
    }

    public static final void dumpAlarmList(IndentingPrintWriter indentingPrintWriter, ArrayList<Alarm> arrayList, long j, SimpleDateFormat simpleDateFormat) {
        int size = arrayList.size();
        for (int i = size - 1; i >= 0; i--) {
            Alarm alarm = arrayList.get(i);
            indentingPrintWriter.print(Alarm.typeToString(alarm.type));
            indentingPrintWriter.print(" #");
            indentingPrintWriter.print(size - i);
            indentingPrintWriter.print(": ");
            indentingPrintWriter.println(alarm);
            indentingPrintWriter.increaseIndent();
            alarm.dump(indentingPrintWriter, j, simpleDateFormat);
            indentingPrintWriter.decreaseIndent();
        }
    }

    public static boolean isExemptFromBatterySaver(Alarm alarm) {
        if (alarm.alarmClock != null) {
            return true;
        }
        PendingIntent pendingIntent = alarm.operation;
        return (pendingIntent != null && (pendingIntent.isActivity() || alarm.operation.isForegroundService())) || UserHandle.isCore(alarm.creatorUid);
    }

    public final boolean isBackgroundRestricted(Alarm alarm) {
        AppStateTrackerImpl appStateTrackerImpl;
        if (alarm.alarmClock != null) {
            return false;
        }
        PendingIntent pendingIntent = alarm.operation;
        if (pendingIntent == null || !pendingIntent.isActivity()) {
            String str = alarm.sourcePackage;
            int i = alarm.creatorUid;
            return (UserHandle.isCore(i) || (appStateTrackerImpl = this.mAppStateTracker) == null || !appStateTrackerImpl.areAlarmsRestricted(i, str)) ? false : true;
        }
        return false;
    }

    @GuardedBy({"mLock"})
    public int triggerAlarmsLocked(ArrayList<Alarm> arrayList, long j) {
        Alarm alarm;
        final AlarmManagerService alarmManagerService = this;
        ArrayList<Alarm> arrayList2 = arrayList;
        long j2 = j;
        Iterator<Alarm> it = alarmManagerService.mAlarmStore.removePendingAlarms(j2).iterator();
        int i = 0;
        while (it.hasNext()) {
            Alarm next = it.next();
            if (alarmManagerService.isBackgroundRestricted(next)) {
                ArrayList<Alarm> arrayList3 = alarmManagerService.mPendingBackgroundAlarms.get(next.creatorUid);
                if (arrayList3 == null) {
                    arrayList3 = new ArrayList<>();
                    alarmManagerService.mPendingBackgroundAlarms.put(next.creatorUid, arrayList3);
                }
                arrayList3.add(next);
            } else {
                next.count = 1;
                arrayList2.add(next);
                if ((next.flags & 2) != 0) {
                    EventLogTags.writeDeviceIdleWakeFromIdle(alarmManagerService.mPendingIdleUntil != null ? 1 : 0, next.statsTag);
                }
                if (alarmManagerService.mPendingIdleUntil == next) {
                    alarmManagerService.mPendingIdleUntil = null;
                    alarmManagerService.mAlarmStore.updateAlarmDeliveries(new AlarmStore.AlarmDeliveryCalculator() { // from class: com.android.server.alarm.AlarmManagerService$$ExternalSyntheticLambda19
                        @Override // com.android.server.alarm.AlarmStore.AlarmDeliveryCalculator
                        public final boolean updateAlarmDelivery(Alarm alarm2) {
                            boolean lambda$triggerAlarmsLocked$22;
                            lambda$triggerAlarmsLocked$22 = AlarmManagerService.this.lambda$triggerAlarmsLocked$22(alarm2);
                            return lambda$triggerAlarmsLocked$22;
                        }
                    });
                }
                if (alarmManagerService.mNextWakeFromIdle == next) {
                    alarmManagerService.mNextWakeFromIdle = alarmManagerService.mAlarmStore.getNextWakeFromIdleAlarm();
                }
                if (next.repeatInterval > 0) {
                    long j3 = next.repeatInterval;
                    int requestedElapsed = (int) (next.count + ((j2 - next.getRequestedElapsed()) / j3));
                    next.count = requestedElapsed;
                    long j4 = requestedElapsed * j3;
                    long requestedElapsed2 = next.getRequestedElapsed() + j4;
                    setImplLocked(next.type, next.origWhen + j4, requestedElapsed2, maxTriggerTime(j, requestedElapsed2, next.repeatInterval) - requestedElapsed2, next.repeatInterval, next.operation, null, null, next.flags, next.workSource, next.alarmClock, next.uid, next.packageName, null, -1);
                    alarm = next;
                } else {
                    alarm = next;
                }
                if (alarm.wakeup) {
                    i++;
                }
                if (alarm.alarmClock != null) {
                    alarmManagerService = this;
                    alarmManagerService.mNextAlarmClockMayChange = true;
                } else {
                    alarmManagerService = this;
                }
                arrayList2 = arrayList;
                j2 = j;
            }
        }
        alarmManagerService.mCurrentSeq++;
        calculateDeliveryPriorities(arrayList);
        Collections.sort(arrayList, alarmManagerService.mAlarmDispatchComparator);
        return i;
    }

    public long currentNonWakeupFuzzLocked(long j) {
        long j2 = j - this.mNonInteractiveStartTime;
        if (j2 < BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS) {
            return 120000L;
        }
        if (j2 < 1800000) {
            return 900000L;
        }
        return ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS;
    }

    @GuardedBy({"mLock"})
    public boolean checkAllowNonWakeupDelayLocked(long j) {
        if (this.mConstants.DELAY_NONWAKEUP_ALARMS_WHILE_SCREEN_OFF && !this.mInteractive && this.mLastAlarmDeliveryTime > 0) {
            return (this.mPendingNonWakeupAlarms.size() <= 0 || this.mNextNonWakeupDeliveryTime >= j) && j - this.mLastAlarmDeliveryTime <= currentNonWakeupFuzzLocked(j);
        }
        return false;
    }

    @GuardedBy({"mLock"})
    public void deliverAlarmsLocked(ArrayList<Alarm> arrayList, long j) {
        this.mLastAlarmDeliveryTime = j;
        for (int i = 0; i < arrayList.size(); i++) {
            Alarm alarm = arrayList.get(i);
            if (alarm.wakeup) {
                Trace.traceBegin(131072L, "Dispatch wakeup alarm to " + alarm.packageName);
            } else {
                Trace.traceBegin(131072L, "Dispatch non-wakeup alarm to " + alarm.packageName);
            }
            try {
                this.mActivityManagerInternal.noteAlarmStart(alarm.operation, alarm.workSource, alarm.uid, alarm.statsTag);
                this.mDeliveryTracker.deliverLocked(alarm, j);
                reportAlarmEventToTare(alarm);
                if (alarm.repeatInterval <= 0) {
                    maybeUnregisterTareListenerLocked(alarm);
                }
            } catch (RuntimeException e) {
                Slog.w("AlarmManager", "Failure sending alarm.", e);
            }
            Trace.traceEnd(131072L);
            decrementAlarmCount(alarm.uid, 1);
        }
    }

    public final void reportAlarmEventToTare(Alarm alarm) {
        int i;
        if (this.mConstants.USE_TARE_POLICY == 0) {
            return;
        }
        boolean z = (alarm.flags & 12) != 0;
        if (alarm.alarmClock != null) {
            i = 1342177288;
        } else if (alarm.wakeup) {
            i = alarm.windowLength == 0 ? z ? 1342177280 : 1342177281 : z ? 1342177282 : 1342177283;
        } else {
            i = alarm.windowLength == 0 ? z ? 1342177284 : 1342177285 : z ? 1342177286 : 1342177287;
        }
        this.mEconomyManagerInternal.noteInstantaneousEvent(UserHandle.getUserId(alarm.creatorUid), alarm.sourcePackage, i, null);
    }

    @VisibleForTesting
    public static boolean isExemptFromAppStandby(Alarm alarm) {
        return (alarm.alarmClock == null && !UserHandle.isCore(alarm.creatorUid) && (alarm.flags & 12) == 0) ? false : true;
    }

    @VisibleForTesting
    public static boolean isExemptFromTare(Alarm alarm) {
        return (alarm.alarmClock == null && !UserHandle.isCore(alarm.creatorUid) && (alarm.flags & 8) == 0) ? false : true;
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class Injector {
        public Context mContext;
        public long mNativeData;

        public Injector(Context context) {
            this.mContext = context;
        }

        public void init() {
            System.loadLibrary("alarm_jni");
            this.mNativeData = AlarmManagerService.init();
        }

        public int waitForAlarm() {
            return AlarmManagerService.waitForAlarm(this.mNativeData);
        }

        public boolean isAlarmDriverPresent() {
            return this.mNativeData != 0;
        }

        public void setAlarm(int i, long j) {
            long j2;
            long j3 = 0;
            if (j < 0) {
                j2 = 0;
            } else {
                long j4 = j / 1000;
                j2 = 1000 * (j % 1000) * 1000;
                j3 = j4;
            }
            int i2 = AlarmManagerService.set(this.mNativeData, i, j3, j2);
            if (i2 != 0) {
                Slog.wtf("AlarmManager", "Unable to set kernel alarm, now=" + SystemClock.elapsedRealtime() + " type=" + i + " @ (" + j3 + "," + j2 + "), ret = " + i2 + " = " + Os.strerror(i2));
            }
        }

        public int getCallingUid() {
            return Binder.getCallingUid();
        }

        public long getNextAlarm(int i) {
            return AlarmManagerService.getNextAlarm(this.mNativeData, i);
        }

        public void initializeTimeIfRequired() {
            SystemClockTime.initializeIfRequired();
        }

        public void setCurrentTimeMillis(long j, int i, String str) {
            SystemClockTime.setTimeAndConfidence(j, i, str);
        }

        public void close() {
            AlarmManagerService.close(this.mNativeData);
        }

        public long getElapsedRealtimeMillis() {
            return SystemClock.elapsedRealtime();
        }

        public long getCurrentTimeMillis() {
            return System.currentTimeMillis();
        }

        public PowerManager.WakeLock getAlarmWakeLock() {
            return ((PowerManager) this.mContext.getSystemService("power")).newWakeLock(1, "*alarm*");
        }

        public int getSystemUiUid(PackageManagerInternal packageManagerInternal) {
            return packageManagerInternal.getPackageUid(packageManagerInternal.getSystemUiServiceComponent().getPackageName(), 1048576L, 0);
        }

        public IAppOpsService getAppOpsService() {
            return IAppOpsService.Stub.asInterface(ServiceManager.getService("appops"));
        }

        public ClockReceiver getClockReceiver(AlarmManagerService alarmManagerService) {
            Objects.requireNonNull(alarmManagerService);
            return new ClockReceiver();
        }

        public void registerDeviceConfigListener(DeviceConfig.OnPropertiesChangedListener onPropertiesChangedListener) {
            DeviceConfig.addOnPropertiesChangedListener("alarm_manager", AppSchedulingModuleThread.getExecutor(), onPropertiesChangedListener);
        }
    }

    /* loaded from: classes.dex */
    public class AlarmThread extends Thread {
        public int mFalseWakeups;
        public int mWtfThreshold;

        public AlarmThread() {
            super("AlarmManager");
            this.mFalseWakeups = 0;
            this.mWtfThreshold = 100;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            long j;
            long j2;
            ArrayList<Alarm> arrayList = new ArrayList<>();
            while (true) {
                int waitForAlarm = AlarmManagerService.this.mInjector.waitForAlarm();
                long currentTimeMillis = AlarmManagerService.this.mInjector.getCurrentTimeMillis();
                long elapsedRealtimeMillis = AlarmManagerService.this.mInjector.getElapsedRealtimeMillis();
                synchronized (AlarmManagerService.this.mLock) {
                    AlarmManagerService.this.mLastWakeup = elapsedRealtimeMillis;
                }
                if (waitForAlarm == 0) {
                    Slog.wtf("AlarmManager", "waitForAlarm returned 0, nowRTC = " + currentTimeMillis + ", nowElapsed = " + elapsedRealtimeMillis);
                }
                arrayList.clear();
                if ((waitForAlarm & 65536) != 0) {
                    synchronized (AlarmManagerService.this.mLock) {
                        AlarmManagerService alarmManagerService = AlarmManagerService.this;
                        j = alarmManagerService.mLastTimeChangeClockTime;
                        j2 = (elapsedRealtimeMillis - alarmManagerService.mLastTimeChangeRealtime) + j;
                    }
                    if (j == 0 || currentTimeMillis < j2 - 1000 || currentTimeMillis > j2 + 1000) {
                        FrameworkStatsLog.write(45, currentTimeMillis);
                        AlarmManagerService alarmManagerService2 = AlarmManagerService.this;
                        alarmManagerService2.removeImpl(null, alarmManagerService2.mTimeTickTrigger);
                        AlarmManagerService alarmManagerService3 = AlarmManagerService.this;
                        alarmManagerService3.removeImpl(alarmManagerService3.mDateChangeSender, null);
                        AlarmManagerService.this.reevaluateRtcAlarms();
                        AlarmManagerService.this.mClockReceiver.scheduleTimeTickEvent();
                        AlarmManagerService.this.mClockReceiver.scheduleDateChangedEvent();
                        synchronized (AlarmManagerService.this.mLock) {
                            AlarmManagerService alarmManagerService4 = AlarmManagerService.this;
                            alarmManagerService4.mNumTimeChanged++;
                            alarmManagerService4.mLastTimeChangeClockTime = currentTimeMillis;
                            alarmManagerService4.mLastTimeChangeRealtime = elapsedRealtimeMillis;
                        }
                        Intent intent = new Intent("android.intent.action.TIME_SET");
                        intent.addFlags(622854144);
                        AlarmManagerService alarmManagerService5 = AlarmManagerService.this;
                        alarmManagerService5.mOptsTimeBroadcast.setTemporaryAppAllowlist(alarmManagerService5.mActivityManagerInternal.getBootTimeTempAllowListDuration(), 0, 205, "");
                        AlarmManagerService.this.getContext().sendBroadcastAsUser(intent, UserHandle.ALL, null, AlarmManagerService.this.mOptsTimeBroadcast.toBundle());
                        waitForAlarm |= 5;
                    }
                }
                if (waitForAlarm != 65536) {
                    synchronized (AlarmManagerService.this.mLock) {
                        AlarmManagerService.this.mLastTrigger = elapsedRealtimeMillis;
                        int triggerAlarmsLocked = AlarmManagerService.this.triggerAlarmsLocked(arrayList, elapsedRealtimeMillis);
                        if (triggerAlarmsLocked == 0 && AlarmManagerService.this.checkAllowNonWakeupDelayLocked(elapsedRealtimeMillis)) {
                            if (AlarmManagerService.this.mPendingNonWakeupAlarms.size() == 0) {
                                AlarmManagerService alarmManagerService6 = AlarmManagerService.this;
                                alarmManagerService6.mStartCurrentDelayTime = elapsedRealtimeMillis;
                                alarmManagerService6.mNextNonWakeupDeliveryTime = elapsedRealtimeMillis + ((alarmManagerService6.currentNonWakeupFuzzLocked(elapsedRealtimeMillis) * 3) / 2);
                            }
                            AlarmManagerService.this.mPendingNonWakeupAlarms.addAll(arrayList);
                            AlarmManagerService.this.mNumDelayedAlarms += arrayList.size();
                            AlarmManagerService.this.rescheduleKernelAlarmsLocked();
                            AlarmManagerService.this.updateNextAlarmClockLocked();
                        } else {
                            if (AlarmManagerService.this.mPendingNonWakeupAlarms.size() > 0) {
                                AlarmManagerService alarmManagerService7 = AlarmManagerService.this;
                                alarmManagerService7.calculateDeliveryPriorities(alarmManagerService7.mPendingNonWakeupAlarms);
                                arrayList.addAll(AlarmManagerService.this.mPendingNonWakeupAlarms);
                                Collections.sort(arrayList, AlarmManagerService.this.mAlarmDispatchComparator);
                                AlarmManagerService alarmManagerService8 = AlarmManagerService.this;
                                long j3 = elapsedRealtimeMillis - alarmManagerService8.mStartCurrentDelayTime;
                                alarmManagerService8.mTotalDelayTime += j3;
                                if (alarmManagerService8.mMaxDelayTime < j3) {
                                    alarmManagerService8.mMaxDelayTime = j3;
                                }
                                alarmManagerService8.mPendingNonWakeupAlarms.clear();
                            }
                            if (AlarmManagerService.this.mLastTimeChangeRealtime != elapsedRealtimeMillis && arrayList.isEmpty()) {
                                int i = this.mFalseWakeups + 1;
                                this.mFalseWakeups = i;
                                if (i >= this.mWtfThreshold) {
                                    Slog.wtf("AlarmManager", "Too many (" + this.mFalseWakeups + ") false wakeups, nowElapsed=" + elapsedRealtimeMillis);
                                    int i2 = this.mWtfThreshold;
                                    if (i2 < 100000) {
                                        this.mWtfThreshold = i2 * 10;
                                    } else {
                                        this.mFalseWakeups = 0;
                                    }
                                }
                            }
                            ArraySet<UserPackage> arraySet = new ArraySet<>();
                            IntArray intArray = new IntArray();
                            SparseIntArray sparseIntArray = new SparseIntArray();
                            SparseIntArray sparseIntArray2 = new SparseIntArray();
                            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                                Alarm alarm = arrayList.get(i3);
                                AlarmManagerService.increment(sparseIntArray, alarm.uid);
                                if (alarm.wakeup) {
                                    intArray.add(alarm.uid);
                                    AlarmManagerService.increment(sparseIntArray2, alarm.uid);
                                }
                                if (AlarmManagerService.this.mConstants.USE_TARE_POLICY == 1) {
                                    if (!AlarmManagerService.isExemptFromTare(alarm)) {
                                        arraySet.add(UserPackage.of(UserHandle.getUserId(alarm.creatorUid), alarm.sourcePackage));
                                    }
                                } else if (!AlarmManagerService.isExemptFromAppStandby(alarm)) {
                                    arraySet.add(UserPackage.of(UserHandle.getUserId(alarm.creatorUid), alarm.sourcePackage));
                                }
                            }
                            if (intArray.size() > 0 && AlarmManagerService.this.mBatteryStatsInternal != null) {
                                AlarmManagerService.this.mBatteryStatsInternal.noteCpuWakingActivity(1, elapsedRealtimeMillis, intArray.toArray());
                            }
                            AlarmManagerService.this.deliverAlarmsLocked(arrayList, elapsedRealtimeMillis);
                            AlarmManagerService.this.mTemporaryQuotaReserve.cleanUpExpiredQuotas(elapsedRealtimeMillis);
                            AlarmManagerService alarmManagerService9 = AlarmManagerService.this;
                            if (alarmManagerService9.mConstants.USE_TARE_POLICY == 1) {
                                alarmManagerService9.reorderAlarmsBasedOnTare(arraySet);
                            } else {
                                alarmManagerService9.reorderAlarmsBasedOnStandbyBuckets(arraySet);
                            }
                            AlarmManagerService.this.rescheduleKernelAlarmsLocked();
                            AlarmManagerService.this.updateNextAlarmClockLocked();
                            AlarmManagerService.this.logAlarmBatchDelivered(arrayList.size(), triggerAlarmsLocked, sparseIntArray, sparseIntArray2);
                        }
                    }
                } else {
                    synchronized (AlarmManagerService.this.mLock) {
                        AlarmManagerService.this.rescheduleKernelAlarmsLocked();
                    }
                }
            }
        }
    }

    public static void increment(SparseIntArray sparseIntArray, int i) {
        int indexOfKey = sparseIntArray.indexOfKey(i);
        if (indexOfKey >= 0) {
            sparseIntArray.setValueAt(indexOfKey, sparseIntArray.valueAt(indexOfKey) + 1);
        } else {
            sparseIntArray.put(i, 1);
        }
    }

    public final void logAlarmBatchDelivered(int i, int i2, SparseIntArray sparseIntArray, SparseIntArray sparseIntArray2) {
        int[] iArr = new int[sparseIntArray.size()];
        int[] iArr2 = new int[sparseIntArray.size()];
        int[] iArr3 = new int[sparseIntArray.size()];
        for (int i3 = 0; i3 < sparseIntArray.size(); i3++) {
            iArr[i3] = sparseIntArray.keyAt(i3);
            iArr2[i3] = sparseIntArray.valueAt(i3);
            iArr3[i3] = sparseIntArray2.get(iArr[i3], 0);
        }
        MetricsHelper.pushAlarmBatchDelivered(i, i2, iArr, iArr2, iArr3);
    }

    public void setWakelockWorkSource(WorkSource workSource, int i, String str, boolean z) {
        try {
            PowerManager.WakeLock wakeLock = this.mWakeLock;
            if (!z) {
                str = null;
            }
            wakeLock.setHistoryTag(str);
        } catch (Exception unused) {
        }
        if (workSource != null) {
            this.mWakeLock.setWorkSource(workSource);
            return;
        }
        if (i >= 0) {
            this.mWakeLock.setWorkSource(new WorkSource(i));
            return;
        }
        this.mWakeLock.setWorkSource(null);
    }

    public static int getAlarmAttributionUid(Alarm alarm) {
        WorkSource workSource = alarm.workSource;
        if (workSource != null && !workSource.isEmpty()) {
            return alarm.workSource.getAttributionUid();
        }
        return alarm.creatorUid;
    }

    @GuardedBy({"mLock"})
    public final boolean canAffordBillLocked(Alarm alarm, EconomyManagerInternal.ActionBill actionBill) {
        int userId = UserHandle.getUserId(alarm.creatorUid);
        String str = alarm.sourcePackage;
        ArrayMap arrayMap = (ArrayMap) this.mAffordabilityCache.get(userId, str);
        if (arrayMap == null) {
            arrayMap = new ArrayMap();
            this.mAffordabilityCache.add(userId, str, arrayMap);
        }
        if (arrayMap.containsKey(actionBill)) {
            return ((Boolean) arrayMap.get(actionBill)).booleanValue();
        }
        boolean canPayFor = this.mEconomyManagerInternal.canPayFor(userId, str, actionBill);
        arrayMap.put(actionBill, Boolean.valueOf(canPayFor));
        return canPayFor;
    }

    @GuardedBy({"mLock"})
    public final boolean hasEnoughWealthLocked(Alarm alarm) {
        return canAffordBillLocked(alarm, TareBill.getAppropriateBill(alarm));
    }

    public final Bundle getAlarmOperationBundle(Alarm alarm) {
        Bundle bundle = alarm.mIdleOptions;
        if (bundle != null) {
            return bundle;
        }
        if (alarm.operation.isActivity()) {
            return this.mActivityOptsRestrictBal.toBundle();
        }
        return this.mBroadcastOptsRestrictBal.toBundle();
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public class AlarmHandler extends Handler {
        public AlarmHandler() {
            super(Looper.myLooper());
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    ArrayList<Alarm> arrayList = new ArrayList<>();
                    synchronized (AlarmManagerService.this.mLock) {
                        AlarmManagerService.this.triggerAlarmsLocked(arrayList, AlarmManagerService.this.mInjector.getElapsedRealtimeMillis());
                        AlarmManagerService.this.updateNextAlarmClockLocked();
                    }
                    for (int i = 0; i < arrayList.size(); i++) {
                        Alarm alarm = arrayList.get(i);
                        try {
                            alarm.operation.send(null, 0, null, null, null, null, AlarmManagerService.this.getAlarmOperationBundle(alarm));
                        } catch (PendingIntent.CanceledException unused) {
                            if (alarm.repeatInterval > 0) {
                                AlarmManagerService.this.removeImpl(alarm.operation, null);
                            }
                        }
                        AlarmManagerService.this.decrementAlarmCount(alarm.uid, 1);
                    }
                    return;
                case 2:
                    AlarmManagerService.this.sendNextAlarmClockChanged();
                    return;
                case 3:
                    AlarmManagerService.this.mDeliveryTracker.alarmTimedOut((IBinder) message.obj);
                    return;
                case 4:
                    DeviceIdleInternal deviceIdleInternal = AlarmManagerService.this.mLocalDeviceIdleController;
                    if (deviceIdleInternal != null) {
                        deviceIdleInternal.setAlarmsActive(message.arg1 != 0);
                        return;
                    }
                    return;
                case 5:
                case 14:
                    synchronized (AlarmManagerService.this.mLock) {
                        ArraySet<UserPackage> arraySet = new ArraySet<>();
                        arraySet.add(UserPackage.of(message.arg1, (String) message.obj));
                        if (AlarmManagerService.this.reorderAlarmsBasedOnStandbyBuckets(arraySet)) {
                            AlarmManagerService.this.rescheduleKernelAlarmsLocked();
                            AlarmManagerService.this.updateNextAlarmClockLocked();
                        }
                    }
                    return;
                case 6:
                    synchronized (AlarmManagerService.this.mLock) {
                        AlarmManagerService.this.mAppStandbyParole = ((Boolean) message.obj).booleanValue();
                        if (AlarmManagerService.this.reorderAlarmsBasedOnStandbyBuckets(null)) {
                            AlarmManagerService.this.rescheduleKernelAlarmsLocked();
                            AlarmManagerService.this.updateNextAlarmClockLocked();
                        }
                    }
                    return;
                case 7:
                    PendingIntent pendingIntent = (PendingIntent) message.obj;
                    synchronized (AlarmManagerService.this.mLock) {
                        AlarmManagerService.this.removeLocked(pendingIntent, null, 4);
                    }
                    return;
                case 8:
                    AlarmManagerService.this.removeExactAlarmsOnPermissionRevoked(message.arg1, (String) message.obj, true);
                    return;
                case 9:
                    AlarmManagerService.this.handleChangesToExactAlarmDenyList((ArraySet) message.obj, true);
                    return;
                case 10:
                    AlarmManagerService.this.handleChangesToExactAlarmDenyList((ArraySet) message.obj, false);
                    return;
                case 11:
                    AlarmManagerService.this.refreshExactAlarmCandidates();
                    return;
                case 12:
                    synchronized (AlarmManagerService.this.mLock) {
                        ArraySet<UserPackage> arraySet2 = new ArraySet<>();
                        arraySet2.add(UserPackage.of(message.arg1, (String) message.obj));
                        if (AlarmManagerService.this.reorderAlarmsBasedOnTare(arraySet2)) {
                            AlarmManagerService.this.rescheduleKernelAlarmsLocked();
                            AlarmManagerService.this.updateNextAlarmClockLocked();
                        }
                    }
                    return;
                case 13:
                    String str = (String) message.obj;
                    int i2 = message.arg1;
                    if (AlarmManagerService.this.hasScheduleExactAlarmInternal(str, i2) || AlarmManagerService.this.hasUseExactAlarmInternal(str, i2)) {
                        return;
                    }
                    AlarmManagerService.this.removeExactAlarmsOnPermissionRevoked(i2, str, false);
                    return;
                default:
                    return;
            }
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public class ChargingReceiver extends BroadcastReceiver {
        public ChargingReceiver() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.os.action.CHARGING");
            intentFilter.addAction("android.os.action.DISCHARGING");
            AlarmManagerService.this.getContext().registerReceiver(this, intentFilter);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            boolean equals = "android.os.action.CHARGING".equals(intent.getAction());
            AlarmManagerService.this.mHandler.removeMessages(6);
            AlarmManagerService.this.mHandler.obtainMessage(6, Boolean.valueOf(equals)).sendToTarget();
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public class ClockReceiver extends BroadcastReceiver {
        public ClockReceiver() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.DATE_CHANGED");
            AlarmManagerService.this.getContext().registerReceiver(this, intentFilter);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.DATE_CHANGED")) {
                scheduleDateChangedEvent();
            }
        }

        public void scheduleTimeTickEvent() {
            long currentTimeMillis = AlarmManagerService.this.mInjector.getCurrentTimeMillis();
            long j = (((currentTimeMillis / 60000) + 1) * 60000) - currentTimeMillis;
            AlarmManagerService alarmManagerService = AlarmManagerService.this;
            alarmManagerService.setImpl(3, alarmManagerService.mInjector.getElapsedRealtimeMillis() + j, 0L, 0L, null, AlarmManagerService.this.mTimeTickTrigger, "TIME_TICK", (alarmManagerService.mConstants.TIME_TICK_ALLOWED_WHILE_IDLE ? 8 : 0) | 1, null, null, Process.myUid(), PackageManagerShellCommandDataLoader.PACKAGE, null, 1);
            synchronized (AlarmManagerService.this.mLock) {
                AlarmManagerService.this.mLastTickSet = currentTimeMillis;
            }
        }

        public void scheduleDateChangedEvent() {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(AlarmManagerService.this.mInjector.getCurrentTimeMillis());
            calendar.set(11, 0);
            calendar.set(12, 0);
            calendar.set(13, 0);
            calendar.set(14, 0);
            calendar.add(5, 1);
            AlarmManagerService.this.setImpl(1, calendar.getTimeInMillis(), 0L, 0L, AlarmManagerService.this.mDateChangeSender, null, null, 1, null, null, Process.myUid(), PackageManagerShellCommandDataLoader.PACKAGE, null, 1);
        }
    }

    /* loaded from: classes.dex */
    public class InteractiveStateReceiver extends BroadcastReceiver {
        public InteractiveStateReceiver() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.setPriority(1000);
            AlarmManagerService.this.getContext().registerReceiver(this, intentFilter);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            synchronized (AlarmManagerService.this.mLock) {
                AlarmManagerService.this.interactiveStateChangedLocked("android.intent.action.SCREEN_ON".equals(intent.getAction()));
            }
        }
    }

    /* loaded from: classes.dex */
    public class UninstallReceiver extends BroadcastReceiver {
        public UninstallReceiver() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
            intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
            intentFilter.addAction("android.intent.action.PACKAGE_RESTARTED");
            intentFilter.addAction("android.intent.action.QUERY_PACKAGE_RESTART");
            intentFilter.addDataScheme("package");
            AlarmManagerService.this.getContext().registerReceiverForAllUsers(this, intentFilter, null, null);
            IntentFilter intentFilter2 = new IntentFilter();
            intentFilter2.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
            intentFilter2.addAction("android.intent.action.USER_STOPPED");
            intentFilter2.addAction("android.intent.action.UID_REMOVED");
            AlarmManagerService.this.getContext().registerReceiverForAllUsers(this, intentFilter2, null, null);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            char c;
            String[] stringArrayExtra;
            String schemeSpecificPart;
            int intExtra = intent.getIntExtra("android.intent.extra.UID", -1);
            synchronized (AlarmManagerService.this.mLock) {
                String action = intent.getAction();
                switch (action.hashCode()) {
                    case -1749672628:
                        if (action.equals("android.intent.action.UID_REMOVED")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1403934493:
                        if (action.equals("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE")) {
                            c = 4;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1072806502:
                        if (action.equals("android.intent.action.QUERY_PACKAGE_RESTART")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case -757780528:
                        if (action.equals("android.intent.action.PACKAGE_RESTARTED")) {
                            c = 6;
                            break;
                        }
                        c = 65535;
                        break;
                    case -742246786:
                        if (action.equals("android.intent.action.USER_STOPPED")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 525384130:
                        if (action.equals("android.intent.action.PACKAGE_REMOVED")) {
                            c = 5;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1544582882:
                        if (action.equals("android.intent.action.PACKAGE_ADDED")) {
                            c = 3;
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
                        for (String str : intent.getStringArrayExtra("android.intent.extra.PACKAGES")) {
                            if (AlarmManagerService.this.lookForPackageLocked(str)) {
                                setResultCode(-1);
                                return;
                            }
                        }
                        return;
                    case 1:
                        int intExtra2 = intent.getIntExtra("android.intent.extra.user_handle", -1);
                        if (intExtra2 >= 0) {
                            AlarmManagerService.this.removeUserLocked(intExtra2);
                            AlarmManagerService.this.mAppWakeupHistory.removeForUser(intExtra2);
                            AlarmManagerService.this.mAllowWhileIdleHistory.removeForUser(intExtra2);
                            AlarmManagerService.this.mAllowWhileIdleCompatHistory.removeForUser(intExtra2);
                            AlarmManagerService.this.mTemporaryQuotaReserve.removeForUser(intExtra2);
                        }
                        return;
                    case 2:
                        AlarmManagerService.this.mLastPriorityAlarmDispatch.delete(intExtra);
                        AlarmManagerService.this.mRemovalHistory.delete(intExtra);
                        AlarmManagerService.this.mLastOpScheduleExactAlarm.delete(intExtra);
                        return;
                    case 3:
                        AlarmManagerService.this.mHandler.sendEmptyMessage(11);
                        if (intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                            AlarmManagerService.this.mHandler.obtainMessage(13, intExtra, -1, intent.getData().getSchemeSpecificPart()).sendToTarget();
                        }
                        return;
                    case 4:
                        stringArrayExtra = intent.getStringArrayExtra("android.intent.extra.changed_package_list");
                        break;
                    case 5:
                        if (intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                            return;
                        }
                        AlarmManagerService.this.mHandler.sendEmptyMessage(11);
                    case 6:
                        Uri data = intent.getData();
                        if (data != null && (schemeSpecificPart = data.getSchemeSpecificPart()) != null) {
                            stringArrayExtra = new String[]{schemeSpecificPart};
                            break;
                        }
                        stringArrayExtra = null;
                        break;
                    default:
                        stringArrayExtra = null;
                        break;
                }
                if (stringArrayExtra != null && stringArrayExtra.length > 0) {
                    for (String str2 : stringArrayExtra) {
                        if (intExtra >= 0) {
                            AlarmManagerService.this.mAppWakeupHistory.removeForPackage(str2, UserHandle.getUserId(intExtra));
                            AlarmManagerService.this.mAllowWhileIdleHistory.removeForPackage(str2, UserHandle.getUserId(intExtra));
                            AlarmManagerService.this.mAllowWhileIdleCompatHistory.removeForPackage(str2, UserHandle.getUserId(intExtra));
                            AlarmManagerService.this.mTemporaryQuotaReserve.removeForPackage(str2, UserHandle.getUserId(intExtra));
                            AlarmManagerService.this.removeLocked(intExtra, 0);
                        } else {
                            AlarmManagerService.this.removeLocked(str2, 0);
                        }
                        AlarmManagerService.this.mPriorities.remove(str2);
                        for (int size = AlarmManagerService.this.mBroadcastStats.size() - 1; size >= 0; size--) {
                            ArrayMap<String, BroadcastStats> valueAt = AlarmManagerService.this.mBroadcastStats.valueAt(size);
                            if (valueAt.remove(str2) != null && valueAt.size() <= 0) {
                                AlarmManagerService.this.mBroadcastStats.removeAt(size);
                            }
                        }
                    }
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public final class AppStandbyTracker extends AppStandbyInternal.AppIdleStateChangeListener {
        public AppStandbyTracker() {
        }

        public void onAppIdleStateChanged(String str, int i, boolean z, int i2, int i3) {
            AlarmManagerService.this.mHandler.obtainMessage(5, i, -1, str).sendToTarget();
        }

        public void triggerTemporaryQuotaBump(String str, int i) {
            AlarmManagerService alarmManagerService;
            int i2;
            int packageUid;
            synchronized (AlarmManagerService.this.mLock) {
                alarmManagerService = AlarmManagerService.this;
                i2 = alarmManagerService.mConstants.TEMPORARY_QUOTA_BUMP;
            }
            if (i2 > 0 && (packageUid = alarmManagerService.mPackageManagerInternal.getPackageUid(str, 0L, i)) >= 0 && !UserHandle.isCore(packageUid)) {
                synchronized (AlarmManagerService.this.mLock) {
                    AlarmManagerService alarmManagerService2 = AlarmManagerService.this;
                    alarmManagerService2.mTemporaryQuotaReserve.replenishQuota(str, i, i2, alarmManagerService2.mInjector.getElapsedRealtimeMillis());
                }
                AlarmManagerService.this.mHandler.obtainMessage(14, i, -1, str).sendToTarget();
            }
        }
    }

    /* renamed from: com.android.server.alarm.AlarmManagerService$9 */
    /* loaded from: classes.dex */
    public class C03209 extends AppStateTrackerImpl.Listener {
        public C03209() {
        }

        @Override // com.android.server.AppStateTrackerImpl.Listener
        public void updateAllAlarms() {
            synchronized (AlarmManagerService.this.mLock) {
                if (AlarmManagerService.this.mAlarmStore.updateAlarmDeliveries(new AlarmStore.AlarmDeliveryCalculator() { // from class: com.android.server.alarm.AlarmManagerService$9$$ExternalSyntheticLambda0
                    @Override // com.android.server.alarm.AlarmStore.AlarmDeliveryCalculator
                    public final boolean updateAlarmDelivery(Alarm alarm) {
                        boolean lambda$updateAllAlarms$0;
                        lambda$updateAllAlarms$0 = AlarmManagerService.C03209.this.lambda$updateAllAlarms$0(alarm);
                        return lambda$updateAllAlarms$0;
                    }
                })) {
                    AlarmManagerService.this.rescheduleKernelAlarmsLocked();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$updateAllAlarms$0(Alarm alarm) {
            return AlarmManagerService.this.adjustDeliveryTimeBasedOnBatterySaver(alarm);
        }

        @Override // com.android.server.AppStateTrackerImpl.Listener
        public void updateAlarmsForUid(final int i) {
            synchronized (AlarmManagerService.this.mLock) {
                if (AlarmManagerService.this.mAlarmStore.updateAlarmDeliveries(new AlarmStore.AlarmDeliveryCalculator() { // from class: com.android.server.alarm.AlarmManagerService$9$$ExternalSyntheticLambda1
                    @Override // com.android.server.alarm.AlarmStore.AlarmDeliveryCalculator
                    public final boolean updateAlarmDelivery(Alarm alarm) {
                        boolean lambda$updateAlarmsForUid$1;
                        lambda$updateAlarmsForUid$1 = AlarmManagerService.C03209.this.lambda$updateAlarmsForUid$1(i, alarm);
                        return lambda$updateAlarmsForUid$1;
                    }
                })) {
                    AlarmManagerService.this.rescheduleKernelAlarmsLocked();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$updateAlarmsForUid$1(int i, Alarm alarm) {
            if (alarm.creatorUid != i) {
                return false;
            }
            return AlarmManagerService.this.adjustDeliveryTimeBasedOnBatterySaver(alarm);
        }

        @Override // com.android.server.AppStateTrackerImpl.Listener
        public void unblockAllUnrestrictedAlarms() {
            synchronized (AlarmManagerService.this.mLock) {
                AlarmManagerService.this.sendAllUnrestrictedPendingBackgroundAlarmsLocked();
            }
        }

        @Override // com.android.server.AppStateTrackerImpl.Listener
        public void unblockAlarmsForUid(int i) {
            synchronized (AlarmManagerService.this.mLock) {
                AlarmManagerService.this.sendPendingBackgroundAlarmsLocked(i, null);
            }
        }

        @Override // com.android.server.AppStateTrackerImpl.Listener
        public void unblockAlarmsForUidPackage(int i, String str) {
            synchronized (AlarmManagerService.this.mLock) {
                AlarmManagerService.this.sendPendingBackgroundAlarmsLocked(i, str);
            }
        }

        @Override // com.android.server.AppStateTrackerImpl.Listener
        public void removeAlarmsForUid(int i) {
            synchronized (AlarmManagerService.this.mLock) {
                AlarmManagerService.this.removeForStoppedLocked(i);
            }
        }

        @Override // com.android.server.AppStateTrackerImpl.Listener
        public void removeListenerAlarmsForCachedUid(final int i) {
            if (CompatChanges.isChangeEnabled(265195908L, i)) {
                synchronized (AlarmManagerService.this.mLock) {
                    AlarmManagerService.this.removeAlarmsInternalLocked(new Predicate() { // from class: com.android.server.alarm.AlarmManagerService$9$$ExternalSyntheticLambda2
                        @Override // java.util.function.Predicate
                        public final boolean test(Object obj) {
                            boolean lambda$removeListenerAlarmsForCachedUid$2;
                            lambda$removeListenerAlarmsForCachedUid$2 = AlarmManagerService.C03209.lambda$removeListenerAlarmsForCachedUid$2(i, (Alarm) obj);
                            return lambda$removeListenerAlarmsForCachedUid$2;
                        }
                    }, 6);
                }
            }
        }

        public static /* synthetic */ boolean lambda$removeListenerAlarmsForCachedUid$2(int i, Alarm alarm) {
            if (alarm.uid == i && alarm.listener != null && alarm.windowLength == 0) {
                Slog.wtf("AlarmManager", "Alarm " + alarm.listenerTag + " being removed for " + alarm.packageName + " because the app went into cached state");
                return true;
            }
            return false;
        }
    }

    public final BroadcastStats getStatsLocked(PendingIntent pendingIntent) {
        return getStatsLocked(pendingIntent.getCreatorUid(), pendingIntent.getCreatorPackage());
    }

    public final BroadcastStats getStatsLocked(int i, String str) {
        ArrayMap<String, BroadcastStats> arrayMap = this.mBroadcastStats.get(i);
        if (arrayMap == null) {
            arrayMap = new ArrayMap<>();
            this.mBroadcastStats.put(i, arrayMap);
        }
        BroadcastStats broadcastStats = arrayMap.get(str);
        if (broadcastStats == null) {
            BroadcastStats broadcastStats2 = new BroadcastStats(i, str);
            arrayMap.put(str, broadcastStats2);
            return broadcastStats2;
        }
        return broadcastStats;
    }

    /* loaded from: classes.dex */
    public class DeliveryTracker extends IAlarmCompleteListener.Stub implements PendingIntent.OnFinished {
        public DeliveryTracker() {
        }

        @GuardedBy({"mLock"})
        public final InFlight removeLocked(PendingIntent pendingIntent, Intent intent) {
            for (int i = 0; i < AlarmManagerService.this.mInFlight.size(); i++) {
                InFlight inFlight = AlarmManagerService.this.mInFlight.get(i);
                if (inFlight.mPendingIntent == pendingIntent) {
                    if (pendingIntent.isBroadcast()) {
                        AlarmManagerService.this.notifyBroadcastAlarmCompleteLocked(inFlight.mUid);
                    }
                    return AlarmManagerService.this.mInFlight.remove(i);
                }
            }
            LocalLog localLog = AlarmManagerService.this.mLog;
            localLog.w("No in-flight alarm for " + pendingIntent + " " + intent);
            return null;
        }

        @GuardedBy({"mLock"})
        public final InFlight removeLocked(IBinder iBinder) {
            for (int i = 0; i < AlarmManagerService.this.mInFlight.size(); i++) {
                if (AlarmManagerService.this.mInFlight.get(i).mListener == iBinder) {
                    return AlarmManagerService.this.mInFlight.remove(i);
                }
            }
            LocalLog localLog = AlarmManagerService.this.mLog;
            localLog.w("No in-flight alarm for listener " + iBinder);
            return null;
        }

        public final void updateStatsLocked(InFlight inFlight) {
            long elapsedRealtimeMillis = AlarmManagerService.this.mInjector.getElapsedRealtimeMillis();
            BroadcastStats broadcastStats = inFlight.mBroadcastStats;
            int i = broadcastStats.nesting - 1;
            broadcastStats.nesting = i;
            if (i <= 0) {
                broadcastStats.nesting = 0;
                broadcastStats.aggregateTime += elapsedRealtimeMillis - broadcastStats.startTime;
            }
            FilterStats filterStats = inFlight.mFilterStats;
            int i2 = filterStats.nesting - 1;
            filterStats.nesting = i2;
            if (i2 <= 0) {
                filterStats.nesting = 0;
                filterStats.aggregateTime += elapsedRealtimeMillis - filterStats.startTime;
            }
            AlarmManagerService.this.mActivityManagerInternal.noteAlarmFinish(inFlight.mPendingIntent, inFlight.mWorkSource, inFlight.mUid, inFlight.mTag);
        }

        public final void updateTrackingLocked(InFlight inFlight) {
            if (inFlight != null) {
                updateStatsLocked(inFlight);
            }
            AlarmManagerService alarmManagerService = AlarmManagerService.this;
            int i = alarmManagerService.mBroadcastRefCount - 1;
            alarmManagerService.mBroadcastRefCount = i;
            if (i == 0) {
                alarmManagerService.mHandler.obtainMessage(4, 0, 0).sendToTarget();
                AlarmManagerService.this.mWakeLock.release();
                if (AlarmManagerService.this.mInFlight.size() > 0) {
                    AlarmManagerService.this.mLog.w("Finished all dispatches with " + AlarmManagerService.this.mInFlight.size() + " remaining inflights");
                    for (int i2 = 0; i2 < AlarmManagerService.this.mInFlight.size(); i2++) {
                        AlarmManagerService.this.mLog.w("  Remaining #" + i2 + ": " + AlarmManagerService.this.mInFlight.get(i2));
                    }
                    AlarmManagerService.this.mInFlight.clear();
                }
            } else if (alarmManagerService.mInFlight.size() > 0) {
                InFlight inFlight2 = AlarmManagerService.this.mInFlight.get(0);
                AlarmManagerService.this.setWakelockWorkSource(inFlight2.mWorkSource, inFlight2.mCreatorUid, inFlight2.mTag, false);
            } else {
                AlarmManagerService.this.mLog.w("Alarm wakelock still held but sent queue empty");
                AlarmManagerService.this.mWakeLock.setWorkSource(null);
            }
        }

        public void alarmComplete(IBinder iBinder) {
            if (iBinder == null) {
                AlarmManagerService.this.mLog.w("Invalid alarmComplete: uid=" + Binder.getCallingUid() + " pid=" + Binder.getCallingPid());
                return;
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (AlarmManagerService.this.mLock) {
                    AlarmManagerService.this.mHandler.removeMessages(3, iBinder);
                    InFlight removeLocked = removeLocked(iBinder);
                    if (removeLocked != null) {
                        updateTrackingLocked(removeLocked);
                        AlarmManagerService.this.mListenerFinishCount++;
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        @Override // android.app.PendingIntent.OnFinished
        public void onSendFinished(PendingIntent pendingIntent, Intent intent, int i, String str, Bundle bundle) {
            synchronized (AlarmManagerService.this.mLock) {
                AlarmManagerService.this.mSendFinishCount++;
                updateTrackingLocked(removeLocked(pendingIntent, intent));
            }
        }

        public void alarmTimedOut(IBinder iBinder) {
            synchronized (AlarmManagerService.this.mLock) {
                InFlight removeLocked = removeLocked(iBinder);
                if (removeLocked != null) {
                    updateTrackingLocked(removeLocked);
                    AlarmManagerService.this.mListenerFinishCount++;
                } else {
                    AlarmManagerService.this.mLog.w("Spurious timeout of listener " + iBinder);
                }
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:22:0x00cb  */
        /* JADX WARN: Removed duplicated region for block: B:25:0x0101  */
        /* JADX WARN: Removed duplicated region for block: B:28:0x010e  */
        /* JADX WARN: Removed duplicated region for block: B:29:0x0110  */
        /* JADX WARN: Removed duplicated region for block: B:39:0x012e  */
        /* JADX WARN: Removed duplicated region for block: B:44:0x0154  */
        /* JADX WARN: Removed duplicated region for block: B:49:0x0177  */
        /* JADX WARN: Removed duplicated region for block: B:55:0x019f  */
        /* JADX WARN: Removed duplicated region for block: B:56:0x01a4  */
        /* JADX WARN: Removed duplicated region for block: B:59:0x01b2  */
        /* JADX WARN: Removed duplicated region for block: B:60:0x01b7  */
        @GuardedBy({"mLock"})
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void deliverLocked(final Alarm alarm, long j) {
            AlarmManagerService alarmManagerService;
            InFlight inFlight;
            AlarmManagerService alarmManagerService2;
            final boolean z;
            AppWakeupHistory appWakeupHistory;
            BroadcastStats broadcastStats;
            int i;
            FilterStats filterStats;
            int i2;
            int i3;
            long uid = ThreadLocalWorkSource.setUid(AlarmManagerService.getAlarmAttributionUid(alarm));
            try {
                final boolean z2 = false;
                if (alarm.operation != null) {
                    AlarmManagerService.this.mSendCount++;
                    try {
                        Bundle alarmOperationBundle = AlarmManagerService.this.getAlarmOperationBundle(alarm);
                        PendingIntent pendingIntent = alarm.operation;
                        Context context = AlarmManagerService.this.getContext();
                        Intent putExtra = AlarmManagerService.this.mBackgroundIntent.putExtra("android.intent.extra.ALARM_COUNT", alarm.count);
                        AlarmManagerService alarmManagerService3 = AlarmManagerService.this;
                        pendingIntent.send(context, 0, putExtra, alarmManagerService3.mDeliveryTracker, alarmManagerService3.mHandler, null, alarmOperationBundle);
                        ThreadLocalWorkSource.restore(uid);
                        alarmManagerService = AlarmManagerService.this;
                        if (alarmManagerService.mBroadcastRefCount == 0) {
                            alarmManagerService.setWakelockWorkSource(alarm.workSource, alarm.creatorUid, alarm.statsTag, true);
                            AlarmManagerService.this.mWakeLock.acquire();
                            AlarmManagerService.this.mHandler.obtainMessage(4, 1, 0).sendToTarget();
                        }
                        inFlight = new InFlight(AlarmManagerService.this, alarm, j);
                        AlarmManagerService.this.mInFlight.add(inFlight);
                        AlarmManagerService.this.mBroadcastRefCount++;
                        if (inFlight.isBroadcast()) {
                            AlarmManagerService.this.notifyBroadcastAlarmPendingLocked(alarm.uid);
                        }
                        alarmManagerService2 = AlarmManagerService.this;
                        z = alarmManagerService2.mPendingIdleUntil == null;
                        if (alarmManagerService2.mAppStateTracker != null && AlarmManagerService.this.mAppStateTracker.isForceAllAppsStandbyEnabled()) {
                            z2 = true;
                        }
                        if (!z || z2) {
                            if (!AlarmManagerService.isAllowedWhileIdleRestricted(alarm)) {
                                if ((alarm.flags & 4) != 0) {
                                    appWakeupHistory = AlarmManagerService.this.mAllowWhileIdleHistory;
                                } else {
                                    appWakeupHistory = AlarmManagerService.this.mAllowWhileIdleCompatHistory;
                                }
                                appWakeupHistory.recordAlarmForPackage(alarm.sourcePackage, UserHandle.getUserId(alarm.creatorUid), j);
                                AlarmManagerService.this.mAlarmStore.updateAlarmDeliveries(new AlarmStore.AlarmDeliveryCalculator() { // from class: com.android.server.alarm.AlarmManagerService$DeliveryTracker$$ExternalSyntheticLambda0
                                    @Override // com.android.server.alarm.AlarmStore.AlarmDeliveryCalculator
                                    public final boolean updateAlarmDelivery(Alarm alarm2) {
                                        boolean lambda$deliverLocked$0;
                                        lambda$deliverLocked$0 = AlarmManagerService.DeliveryTracker.this.lambda$deliverLocked$0(alarm, z, z2, alarm2);
                                        return lambda$deliverLocked$0;
                                    }
                                });
                            } else if ((alarm.flags & 64) != 0) {
                                AlarmManagerService.this.mLastPriorityAlarmDispatch.put(alarm.creatorUid, j);
                                AlarmManagerService.this.mAlarmStore.updateAlarmDeliveries(new AlarmStore.AlarmDeliveryCalculator() { // from class: com.android.server.alarm.AlarmManagerService$DeliveryTracker$$ExternalSyntheticLambda1
                                    @Override // com.android.server.alarm.AlarmStore.AlarmDeliveryCalculator
                                    public final boolean updateAlarmDelivery(Alarm alarm2) {
                                        boolean lambda$deliverLocked$1;
                                        lambda$deliverLocked$1 = AlarmManagerService.DeliveryTracker.this.lambda$deliverLocked$1(alarm, z, z2, alarm2);
                                        return lambda$deliverLocked$1;
                                    }
                                });
                            }
                        }
                        if (!AlarmManagerService.isExemptFromAppStandby(alarm)) {
                            int userId = UserHandle.getUserId(alarm.creatorUid);
                            if (alarm.mUsingReserveQuota) {
                                AlarmManagerService.this.mTemporaryQuotaReserve.recordUsage(alarm.sourcePackage, userId, j);
                            } else {
                                AlarmManagerService.this.mAppWakeupHistory.recordAlarmForPackage(alarm.sourcePackage, userId, j);
                            }
                        }
                        broadcastStats = inFlight.mBroadcastStats;
                        broadcastStats.count++;
                        i = broadcastStats.nesting;
                        if (i != 0) {
                            broadcastStats.nesting = 1;
                            broadcastStats.startTime = j;
                        } else {
                            broadcastStats.nesting = i + 1;
                        }
                        filterStats = inFlight.mFilterStats;
                        filterStats.count++;
                        i2 = filterStats.nesting;
                        if (i2 != 0) {
                            filterStats.nesting = 1;
                            filterStats.startTime = j;
                        } else {
                            filterStats.nesting = i2 + 1;
                        }
                        i3 = alarm.type;
                        if (i3 != 2 || i3 == 0) {
                            broadcastStats.numWakeup++;
                            filterStats.numWakeup++;
                            AlarmManagerService.this.mActivityManagerInternal.noteWakeupAlarm(alarm.operation, alarm.workSource, alarm.uid, alarm.packageName, alarm.statsTag);
                        }
                        return;
                    } catch (PendingIntent.CanceledException unused) {
                        if (alarm.repeatInterval > 0) {
                            AlarmManagerService.this.removeImpl(alarm.operation, null);
                        }
                        AlarmManagerService.this.mSendFinishCount++;
                        ThreadLocalWorkSource.restore(uid);
                        return;
                    }
                }
                AlarmManagerService.this.mListenerCount++;
                alarm.listener.asBinder().unlinkToDeath(AlarmManagerService.this.mListenerDeathRecipient, 0);
                IAlarmListener iAlarmListener = alarm.listener;
                AlarmManagerService alarmManagerService4 = AlarmManagerService.this;
                if (iAlarmListener == alarmManagerService4.mTimeTickTrigger) {
                    long[] jArr = alarmManagerService4.mTickHistory;
                    AlarmManagerService alarmManagerService5 = AlarmManagerService.this;
                    int i4 = alarmManagerService5.mNextTickHistory;
                    alarmManagerService5.mNextTickHistory = i4 + 1;
                    jArr[i4] = j;
                    if (AlarmManagerService.this.mNextTickHistory >= 10) {
                        AlarmManagerService.this.mNextTickHistory = 0;
                    }
                }
                try {
                    alarm.listener.doAlarm(this);
                    AlarmHandler alarmHandler = AlarmManagerService.this.mHandler;
                    alarmHandler.sendMessageDelayed(alarmHandler.obtainMessage(3, alarm.listener.asBinder()), AlarmManagerService.this.mConstants.LISTENER_TIMEOUT);
                    ThreadLocalWorkSource.restore(uid);
                    alarmManagerService = AlarmManagerService.this;
                    if (alarmManagerService.mBroadcastRefCount == 0) {
                    }
                    inFlight = new InFlight(AlarmManagerService.this, alarm, j);
                    AlarmManagerService.this.mInFlight.add(inFlight);
                    AlarmManagerService.this.mBroadcastRefCount++;
                    if (inFlight.isBroadcast()) {
                    }
                    alarmManagerService2 = AlarmManagerService.this;
                    if (alarmManagerService2.mPendingIdleUntil == null) {
                    }
                    if (alarmManagerService2.mAppStateTracker != null) {
                        z2 = true;
                    }
                    if (!z) {
                    }
                    if (!AlarmManagerService.isAllowedWhileIdleRestricted(alarm)) {
                    }
                    if (!AlarmManagerService.isExemptFromAppStandby(alarm)) {
                    }
                    broadcastStats = inFlight.mBroadcastStats;
                    broadcastStats.count++;
                    i = broadcastStats.nesting;
                    if (i != 0) {
                    }
                    filterStats = inFlight.mFilterStats;
                    filterStats.count++;
                    i2 = filterStats.nesting;
                    if (i2 != 0) {
                    }
                    i3 = alarm.type;
                    if (i3 != 2) {
                    }
                    broadcastStats.numWakeup++;
                    filterStats.numWakeup++;
                    AlarmManagerService.this.mActivityManagerInternal.noteWakeupAlarm(alarm.operation, alarm.workSource, alarm.uid, alarm.packageName, alarm.statsTag);
                } catch (Exception unused2) {
                    AlarmManagerService.this.mListenerFinishCount++;
                    ThreadLocalWorkSource.restore(uid);
                }
            } catch (Throwable th) {
                ThreadLocalWorkSource.restore(uid);
                throw th;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$deliverLocked$0(Alarm alarm, boolean z, boolean z2, Alarm alarm2) {
            if (alarm2.creatorUid == alarm.creatorUid && AlarmManagerService.isAllowedWhileIdleRestricted(alarm2)) {
                return (z && AlarmManagerService.this.lambda$triggerAlarmsLocked$22(alarm2)) || (z2 && AlarmManagerService.this.adjustDeliveryTimeBasedOnBatterySaver(alarm2));
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$deliverLocked$1(Alarm alarm, boolean z, boolean z2, Alarm alarm2) {
            if (alarm2.creatorUid != alarm.creatorUid || (alarm.flags & 64) == 0) {
                return false;
            }
            return (z && AlarmManagerService.this.lambda$triggerAlarmsLocked$22(alarm2)) || (z2 && AlarmManagerService.this.adjustDeliveryTimeBasedOnBatterySaver(alarm2));
        }
    }

    public final void incrementAlarmCount(int i) {
        increment(this.mAlarmsPerUid, i);
    }

    public final void sendScheduleExactAlarmPermissionStateChangedBroadcast(String str, int i) {
        Intent intent = new Intent("android.app.action.SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED");
        intent.addFlags(872415232);
        intent.setPackage(str);
        BroadcastOptions makeBasic = BroadcastOptions.makeBasic();
        makeBasic.setTemporaryAppAllowlist(this.mActivityManagerInternal.getBootTimeTempAllowListDuration(), 0, 207, "");
        getContext().sendBroadcastAsUser(intent, UserHandle.of(i), null, makeBasic.toBundle());
    }

    public final void decrementAlarmCount(int i, int i2) {
        int i3;
        int indexOfKey = this.mAlarmsPerUid.indexOfKey(i);
        if (indexOfKey >= 0) {
            i3 = this.mAlarmsPerUid.valueAt(indexOfKey);
            if (i3 > i2) {
                this.mAlarmsPerUid.setValueAt(indexOfKey, i3 - i2);
            } else {
                this.mAlarmsPerUid.removeAt(indexOfKey);
            }
        } else {
            i3 = 0;
        }
        if (i3 < i2) {
            Slog.wtf("AlarmManager", "Attempt to decrement existing alarm count " + i3 + " by " + i2 + " for uid " + i);
        }
    }

    /* loaded from: classes.dex */
    public class ShellCmd extends ShellCommand {
        public ShellCmd() {
        }

        public IAlarmManager getBinderService() {
            return IAlarmManager.Stub.asInterface(AlarmManagerService.this.mService);
        }

        /* JADX WARN: Removed duplicated region for block: B:24:0x0046  */
        /* JADX WARN: Removed duplicated region for block: B:32:0x0067 A[Catch: Exception -> 0x007b, TRY_LEAVE, TryCatch #0 {Exception -> 0x007b, blocks: (B:6:0x000c, B:26:0x004a, B:28:0x004f, B:30:0x005b, B:32:0x0067, B:13:0x0023, B:16:0x002e, B:19:0x0039), top: B:39:0x000c }] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public int onCommand(String str) {
            boolean z;
            if (str == null) {
                return handleDefaultCommands(str);
            }
            PrintWriter outPrintWriter = getOutPrintWriter();
            try {
                int hashCode = str.hashCode();
                if (hashCode == -2120488796) {
                    if (str.equals("get-config-version")) {
                        z = true;
                        if (z) {
                        }
                    }
                    z = true;
                    if (z) {
                    }
                } else if (hashCode != 1369384280) {
                    if (hashCode == 2023087364 && str.equals("set-timezone")) {
                        z = true;
                        if (z) {
                            return getBinderService().setTime(Long.parseLong(getNextArgRequired())) ? 0 : -1;
                        } else if (z) {
                            getBinderService().setTimeZone(getNextArgRequired());
                            return 0;
                        } else if (z) {
                            outPrintWriter.println(getBinderService().getConfigVersion());
                            return 0;
                        } else {
                            return handleDefaultCommands(str);
                        }
                    }
                    z = true;
                    if (z) {
                    }
                } else {
                    if (str.equals("set-time")) {
                        z = false;
                        if (z) {
                        }
                    }
                    z = true;
                    if (z) {
                    }
                }
            } catch (Exception e) {
                outPrintWriter.println(e);
                return -1;
            }
        }

        public void onHelp() {
            PrintWriter outPrintWriter = getOutPrintWriter();
            outPrintWriter.println("Alarm manager service (alarm) commands:");
            outPrintWriter.println("  help");
            outPrintWriter.println("    Print this help text.");
            outPrintWriter.println("  set-time TIME");
            outPrintWriter.println("    Set the system clock time to TIME where TIME is milliseconds");
            outPrintWriter.println("    since the Epoch.");
            outPrintWriter.println("  set-timezone TZ");
            outPrintWriter.println("    Set the system timezone to TZ where TZ is an Olson id.");
            outPrintWriter.println("  get-config-version");
            outPrintWriter.println("    Returns an integer denoting the version of device_config keys the service is sync'ed to. As long as this returns the same version, the values of the config are guaranteed to remain the same.");
        }
    }
}
