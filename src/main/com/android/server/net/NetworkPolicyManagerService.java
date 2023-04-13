package com.android.server.net;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.app.IUidObserver;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.app.usage.UsageStatsManagerInternal;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.INetworkManagementEventObserver;
import android.net.INetworkPolicyListener;
import android.net.INetworkPolicyManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkIdentity;
import android.net.NetworkPolicy;
import android.net.NetworkPolicyManager;
import android.net.NetworkRequest;
import android.net.NetworkStack;
import android.net.NetworkStateSnapshot;
import android.net.NetworkTemplate;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.BestClock;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.HandlerThread;
import android.os.INetworkManagementService;
import android.os.Message;
import android.os.MessageQueue;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.os.PowerManagerInternal;
import android.os.PowerSaveState;
import android.os.PowerWhitelistManager;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.p005os.IInstalld;
import android.provider.Settings;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.SubscriptionPlan;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.DataUnit;
import android.util.IntArray;
import android.util.Log;
import android.util.Pair;
import android.util.Range;
import android.util.RecurrenceRule;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.util.SparseSetArray;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.CollectionUtils;
import com.android.internal.util.ConcurrentUtils;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.StatLogger;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.net.module.util.NetworkIdentityUtils;
import com.android.net.module.util.PermissionUtils;
import com.android.server.EventLogTags;
import com.android.server.LocalServices;
import com.android.server.ServiceThread;
import com.android.server.SystemConfig;
import com.android.server.backup.BackupManagerConstants;
import com.android.server.connectivity.MultipathPolicyTracker;
import com.android.server.location.gnss.hal.GnssNative;
import com.android.server.net.NetworkPolicyManagerService;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.usage.AppStandbyInternal;
import dalvik.annotation.optimization.NeverCompile;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import libcore.io.IoUtils;
/* loaded from: classes2.dex */
public class NetworkPolicyManagerService extends INetworkPolicyManager.Stub {
    public static final boolean LOGD = NetworkPolicyLogger.LOGD;
    public static final boolean LOGV = NetworkPolicyLogger.LOGV;
    public static final long QUOTA_UNLIMITED_DEFAULT = DataUnit.MEBIBYTES.toBytes(20);
    @VisibleForTesting
    public static final int TYPE_LIMIT = 35;
    @VisibleForTesting
    public static final int TYPE_LIMIT_SNOOZED = 36;
    @VisibleForTesting
    public static final int TYPE_RAPID = 45;
    @VisibleForTesting
    public static final int TYPE_WARNING = 34;
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final ArraySet<NotificationId> mActiveNotifs;
    public final IActivityManager mActivityManager;
    public ActivityManagerInternal mActivityManagerInternal;
    public final CountDownLatch mAdminDataAvailableLatch;
    public final INetworkManagementEventObserver mAlertObserver;
    @GuardedBy({"mUidRulesFirstLock"})
    public final SparseBooleanArray mAppIdleTempWhitelistAppIds;
    public final AppOpsManager mAppOps;
    public AppStandbyInternal mAppStandby;
    public final CarrierConfigManager mCarrierConfigManager;
    public BroadcastReceiver mCarrierConfigReceiver;
    public final Clock mClock;
    public ConnectivityManager mConnManager;
    public BroadcastReceiver mConnReceiver;
    public final Context mContext;
    @GuardedBy({"mUidRulesFirstLock"})
    public final SparseBooleanArray mDefaultRestrictBackgroundAllowlistUids;
    public final Dependencies mDeps;
    @GuardedBy({"mUidRulesFirstLock"})
    public volatile boolean mDeviceIdleMode;
    @GuardedBy({"mUidRulesFirstLock"})
    public final SparseBooleanArray mFirewallChainStates;
    public final Handler mHandler;
    public final Handler.Callback mHandlerCallback;
    public final IPackageManager mIPm;
    @GuardedBy({"mUidRulesFirstLock"})
    public final SparseBooleanArray mInternetPermissionMap;
    public final RemoteCallbackList<INetworkPolicyListener> mListeners;
    public boolean mLoadedRestrictBackground;
    public final NetworkPolicyLogger mLogger;
    @GuardedBy({"mUidRulesFirstLock"})
    public volatile boolean mLowPowerStandbyActive;
    @GuardedBy({"mUidRulesFirstLock"})
    public final SparseBooleanArray mLowPowerStandbyAllowlistUids;
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public List<String[]> mMergedSubscriberIds;
    @GuardedBy({"mMeteredIfacesLock"})
    public ArraySet<String> mMeteredIfaces;
    public final Object mMeteredIfacesLock;
    @GuardedBy({"mUidRulesFirstLock"})
    public final SparseArray<Set<Integer>> mMeteredRestrictedUids;
    public final MultipathPolicyTracker mMultipathPolicyTracker;
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final SparseIntArray mNetIdToSubId;
    public final ConnectivityManager.NetworkCallback mNetworkCallback;
    public final INetworkManagementService mNetworkManager;
    public volatile boolean mNetworkManagerReady;
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final SparseBooleanArray mNetworkMetered;
    public final Object mNetworkPoliciesSecondLock;
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final ArrayMap<NetworkTemplate, NetworkPolicy> mNetworkPolicy;
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final SparseBooleanArray mNetworkRoaming;
    public NetworkStatsManager mNetworkStats;
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public SparseSetArray<String> mNetworkToIfaces;
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final ArraySet<NetworkTemplate> mOverLimitNotified;
    public final BroadcastReceiver mPackageReceiver;
    @GuardedBy({"mUidRulesFirstLock", "mNetworkPoliciesSecondLock"})
    public final AtomicFile mPolicyFile;
    public PowerManagerInternal mPowerManagerInternal;
    @GuardedBy({"mUidRulesFirstLock"})
    public final SparseBooleanArray mPowerSaveTempWhitelistAppIds;
    @GuardedBy({"mUidRulesFirstLock"})
    public final SparseBooleanArray mPowerSaveWhitelistAppIds;
    @GuardedBy({"mUidRulesFirstLock"})
    public final SparseBooleanArray mPowerSaveWhitelistExceptIdleAppIds;
    public final BroadcastReceiver mPowerSaveWhitelistReceiver;
    public PowerWhitelistManager mPowerWhitelistManager;
    @GuardedBy({"mUidRulesFirstLock"})
    public volatile boolean mRestrictBackground;
    @GuardedBy({"mUidRulesFirstLock"})
    public final SparseBooleanArray mRestrictBackgroundAllowlistRevokedUids;
    public boolean mRestrictBackgroundBeforeBsm;
    @GuardedBy({"mUidRulesFirstLock"})
    public volatile boolean mRestrictBackgroundChangedInBsm;
    @GuardedBy({"mUidRulesFirstLock"})
    public boolean mRestrictBackgroundLowPowerMode;
    @GuardedBy({"mUidRulesFirstLock"})
    public volatile boolean mRestrictPower;
    public RestrictedModeObserver mRestrictedModeObserver;
    @GuardedBy({"mUidRulesFirstLock"})
    public volatile boolean mRestrictedNetworkingMode;
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public int mSetSubscriptionPlansIdCounter;
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final SparseIntArray mSetSubscriptionPlansIds;
    public final BroadcastReceiver mSnoozeReceiver;
    public final StatLogger mStatLogger;
    public final StatsCallback mStatsCallback;
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final SparseArray<PersistableBundle> mSubIdToCarrierConfig;
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final SparseArray<String> mSubIdToSubscriberId;
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final SparseLongArray mSubscriptionOpportunisticQuota;
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final SparseArray<SubscriptionPlan[]> mSubscriptionPlans;
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final SparseArray<String> mSubscriptionPlansOwner;
    public final boolean mSuppressDefaultPolicy;
    @GuardedBy(anyOf = {"mUidRulesFirstLock", "mNetworkPoliciesSecondLock"})
    public volatile boolean mSystemReady;
    @GuardedBy({"mUidRulesFirstLock"})
    public final SparseArray<UidBlockedState> mTmpUidBlockedState;
    @GuardedBy({"mUidBlockedState"})
    public final SparseArray<UidBlockedState> mUidBlockedState;
    @VisibleForTesting
    final Handler mUidEventHandler;
    public final Handler.Callback mUidEventHandlerCallback;
    public final ServiceThread mUidEventThread;
    @GuardedBy({"mUidRulesFirstLock"})
    public final SparseIntArray mUidFirewallDozableRules;
    @GuardedBy({"mUidRulesFirstLock"})
    public final SparseIntArray mUidFirewallLowPowerStandbyModeRules;
    @GuardedBy({"mUidRulesFirstLock"})
    public final SparseIntArray mUidFirewallPowerSaveRules;
    @GuardedBy({"mUidRulesFirstLock"})
    public final SparseIntArray mUidFirewallRestrictedModeRules;
    @GuardedBy({"mUidRulesFirstLock"})
    public final SparseIntArray mUidFirewallStandbyRules;
    public final IUidObserver mUidObserver;
    @GuardedBy({"mUidRulesFirstLock"})
    public final SparseIntArray mUidPolicy;
    public final BroadcastReceiver mUidRemovedReceiver;
    public final Object mUidRulesFirstLock;
    @GuardedBy({"mUidRulesFirstLock"})
    public final SparseArray<NetworkPolicyManager.UidState> mUidState;
    @GuardedBy({"mUidStateCallbackInfos"})
    public final SparseArray<UidStateCallbackInfo> mUidStateCallbackInfos;
    public UsageStatsManagerInternal mUsageStats;
    public final UserManager mUserManager;
    public final BroadcastReceiver mUserReceiver;
    public final BroadcastReceiver mWifiReceiver;

    public static int getRestrictedModeFirewallRule(int i) {
        return (i & 8) != 0 ? 0 : 1;
    }

    public static boolean isSystem(int i) {
        return i < 10000;
    }

    public final long getPlatformDefaultLimitBytes() {
        return -1L;
    }

    /* loaded from: classes2.dex */
    public static class RestrictedModeObserver extends ContentObserver {
        public final Context mContext;
        public final RestrictedModeListener mListener;

        /* loaded from: classes2.dex */
        public interface RestrictedModeListener {
            void onChange(boolean z);
        }

        public RestrictedModeObserver(Context context, RestrictedModeListener restrictedModeListener) {
            super(null);
            this.mContext = context;
            this.mListener = restrictedModeListener;
            context.getContentResolver().registerContentObserver(Settings.Global.getUriFor("restricted_networking_mode"), false, this);
        }

        public boolean isRestrictedModeEnabled() {
            return Settings.Global.getInt(this.mContext.getContentResolver(), "restricted_networking_mode", 0) != 0;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            this.mListener.onChange(isRestrictedModeEnabled());
        }
    }

    public NetworkPolicyManagerService(Context context, IActivityManager iActivityManager, INetworkManagementService iNetworkManagementService) {
        this(context, iActivityManager, iNetworkManagementService, AppGlobals.getPackageManager(), getDefaultClock(), getDefaultSystemDir(), false, new Dependencies(context));
    }

    public static File getDefaultSystemDir() {
        return new File(Environment.getDataDirectory(), "system");
    }

    public static Clock getDefaultClock() {
        return new BestClock(ZoneOffset.UTC, new Clock[]{SystemClock.currentNetworkTimeClock(), Clock.systemUTC()});
    }

    /* loaded from: classes2.dex */
    public static class Dependencies {
        public final Context mContext;
        public final NetworkStatsManager mNetworkStatsManager;

        public Dependencies(Context context) {
            this.mContext = context;
            NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NetworkStatsManager.class);
            this.mNetworkStatsManager = networkStatsManager;
            networkStatsManager.setPollOnOpen(false);
        }

        public long getNetworkTotalBytes(NetworkTemplate networkTemplate, long j, long j2) {
            Trace.traceBegin(2097152L, "getNetworkTotalBytes");
            try {
                try {
                    NetworkStats.Bucket querySummaryForDevice = this.mNetworkStatsManager.querySummaryForDevice(networkTemplate, j, j2);
                    return querySummaryForDevice.getRxBytes() + querySummaryForDevice.getTxBytes();
                } catch (RuntimeException e) {
                    Slog.w("NetworkPolicy", "Failed to read network stats: " + e);
                    Trace.traceEnd(2097152L);
                    return 0L;
                }
            } finally {
                Trace.traceEnd(2097152L);
            }
        }

        public List<NetworkStats.Bucket> getNetworkUidBytes(NetworkTemplate networkTemplate, long j, long j2) {
            Trace.traceBegin(2097152L, "getNetworkUidBytes");
            ArrayList arrayList = new ArrayList();
            try {
                try {
                    NetworkStats querySummary = this.mNetworkStatsManager.querySummary(networkTemplate, j, j2);
                    while (querySummary.hasNextBucket()) {
                        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                        querySummary.getNextBucket(bucket);
                        arrayList.add(bucket);
                    }
                } catch (RuntimeException e) {
                    Slog.w("NetworkPolicy", "Failed to read network stats: " + e);
                }
                return arrayList;
            } finally {
                Trace.traceEnd(2097152L);
            }
        }
    }

    @VisibleForTesting
    public NetworkPolicyManagerService(Context context, IActivityManager iActivityManager, INetworkManagementService iNetworkManagementService, IPackageManager iPackageManager, Clock clock, File file, boolean z, Dependencies dependencies) {
        this.mUidRulesFirstLock = new Object();
        this.mNetworkPoliciesSecondLock = new Object();
        this.mAdminDataAvailableLatch = new CountDownLatch(1);
        this.mNetworkPolicy = new ArrayMap<>();
        this.mSubscriptionPlans = new SparseArray<>();
        this.mSubscriptionPlansOwner = new SparseArray<>();
        this.mSetSubscriptionPlansIds = new SparseIntArray();
        this.mSetSubscriptionPlansIdCounter = 0;
        this.mSubscriptionOpportunisticQuota = new SparseLongArray();
        this.mUidPolicy = new SparseIntArray();
        this.mUidFirewallStandbyRules = new SparseIntArray();
        this.mUidFirewallDozableRules = new SparseIntArray();
        this.mUidFirewallPowerSaveRules = new SparseIntArray();
        this.mUidFirewallRestrictedModeRules = new SparseIntArray();
        this.mUidFirewallLowPowerStandbyModeRules = new SparseIntArray();
        this.mFirewallChainStates = new SparseBooleanArray();
        this.mPowerSaveWhitelistExceptIdleAppIds = new SparseBooleanArray();
        this.mPowerSaveWhitelistAppIds = new SparseBooleanArray();
        this.mPowerSaveTempWhitelistAppIds = new SparseBooleanArray();
        this.mLowPowerStandbyAllowlistUids = new SparseBooleanArray();
        this.mAppIdleTempWhitelistAppIds = new SparseBooleanArray();
        this.mDefaultRestrictBackgroundAllowlistUids = new SparseBooleanArray();
        this.mRestrictBackgroundAllowlistRevokedUids = new SparseBooleanArray();
        this.mMeteredIfacesLock = new Object();
        this.mMeteredIfaces = new ArraySet<>();
        this.mOverLimitNotified = new ArraySet<>();
        this.mActiveNotifs = new ArraySet<>();
        this.mUidState = new SparseArray<>();
        this.mUidBlockedState = new SparseArray<>();
        this.mTmpUidBlockedState = new SparseArray<>();
        this.mNetworkMetered = new SparseBooleanArray();
        this.mNetworkRoaming = new SparseBooleanArray();
        this.mNetworkToIfaces = new SparseSetArray<>();
        this.mNetIdToSubId = new SparseIntArray();
        this.mSubIdToSubscriberId = new SparseArray<>();
        this.mMergedSubscriberIds = new ArrayList();
        this.mSubIdToCarrierConfig = new SparseArray<>();
        this.mMeteredRestrictedUids = new SparseArray<>();
        this.mListeners = new RemoteCallbackList<>();
        this.mLogger = new NetworkPolicyLogger();
        this.mInternetPermissionMap = new SparseBooleanArray();
        this.mUidStateCallbackInfos = new SparseArray<>();
        this.mStatLogger = new StatLogger(new String[]{"updateNetworkEnabledNL()", "isUidNetworkingBlocked()"});
        this.mUidObserver = new IUidObserver.Stub() { // from class: com.android.server.net.NetworkPolicyManagerService.4
            public void onUidActive(int i) {
            }

            public void onUidCachedChanged(int i, boolean z2) {
            }

            public void onUidIdle(int i, boolean z2) {
            }

            public void onUidProcAdjChanged(int i) {
            }

            public void onUidStateChanged(int i, int i2, long j, int i3) {
                synchronized (NetworkPolicyManagerService.this.mUidStateCallbackInfos) {
                    UidStateCallbackInfo uidStateCallbackInfo = (UidStateCallbackInfo) NetworkPolicyManagerService.this.mUidStateCallbackInfos.get(i);
                    if (uidStateCallbackInfo == null) {
                        uidStateCallbackInfo = new UidStateCallbackInfo();
                        NetworkPolicyManagerService.this.mUidStateCallbackInfos.put(i, uidStateCallbackInfo);
                    }
                    UidStateCallbackInfo uidStateCallbackInfo2 = uidStateCallbackInfo;
                    long j2 = uidStateCallbackInfo2.procStateSeq;
                    if (j2 == -1 || j > j2) {
                        uidStateCallbackInfo2.update(i, i2, j, i3);
                    }
                    if (!uidStateCallbackInfo2.isPending) {
                        NetworkPolicyManagerService.this.mUidEventHandler.obtainMessage(100, uidStateCallbackInfo2).sendToTarget();
                    }
                }
            }

            public void onUidGone(int i, boolean z2) {
                NetworkPolicyManagerService.this.mUidEventHandler.obtainMessage(101, i, 0).sendToTarget();
            }
        };
        this.mPowerSaveWhitelistReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkPolicyManagerService.5
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                    NetworkPolicyManagerService.this.updatePowerSaveWhitelistUL();
                    NetworkPolicyManagerService.this.updateRulesForRestrictPowerUL();
                    NetworkPolicyManagerService.this.updateRulesForAppIdleUL();
                }
            }
        };
        this.mPackageReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkPolicyManagerService.6
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                int intExtra = intent.getIntExtra("android.intent.extra.UID", -1);
                if (intExtra != -1 && "android.intent.action.PACKAGE_ADDED".equals(action)) {
                    if (NetworkPolicyManagerService.LOGV) {
                        Slog.v("NetworkPolicy", "ACTION_PACKAGE_ADDED for uid=" + intExtra);
                    }
                    synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                        NetworkPolicyManagerService.this.mInternetPermissionMap.delete(intExtra);
                        NetworkPolicyManagerService.this.updateRestrictionRulesForUidUL(intExtra);
                    }
                }
            }
        };
        this.mUidRemovedReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkPolicyManagerService.7
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                int intExtra = intent.getIntExtra("android.intent.extra.UID", -1);
                if (intExtra == -1) {
                    return;
                }
                if (NetworkPolicyManagerService.LOGV) {
                    Slog.v("NetworkPolicy", "ACTION_UID_REMOVED for uid=" + intExtra);
                }
                synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                    NetworkPolicyManagerService.this.onUidDeletedUL(intExtra);
                    synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                        NetworkPolicyManagerService.this.writePolicyAL();
                    }
                }
            }
        };
        this.mUserReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkPolicyManagerService.8
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                int intExtra = intent.getIntExtra("android.intent.extra.user_handle", -1);
                if (intExtra == -1) {
                    return;
                }
                action.hashCode();
                if (action.equals("android.intent.action.USER_REMOVED") || action.equals("android.intent.action.USER_ADDED")) {
                    synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                        NetworkPolicyManagerService.this.removeUserStateUL(intExtra, true, false);
                        NetworkPolicyManagerService.this.mMeteredRestrictedUids.remove(intExtra);
                        if (action == "android.intent.action.USER_ADDED") {
                            NetworkPolicyManagerService.this.addDefaultRestrictBackgroundAllowlistUidsUL(intExtra);
                        }
                        synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                            NetworkPolicyManagerService.this.updateRulesForGlobalChangeAL(true);
                        }
                    }
                }
            }
        };
        this.mStatsCallback = new StatsCallback();
        this.mSnoozeReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkPolicyManagerService.9
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                NetworkTemplate networkTemplate = (NetworkTemplate) intent.getParcelableExtra("android.net.NETWORK_TEMPLATE", NetworkTemplate.class);
                if ("com.android.server.net.action.SNOOZE_WARNING".equals(intent.getAction())) {
                    NetworkPolicyManagerService.this.performSnooze(networkTemplate, 34);
                } else if ("com.android.server.net.action.SNOOZE_RAPID".equals(intent.getAction())) {
                    NetworkPolicyManagerService.this.performSnooze(networkTemplate, 45);
                }
            }
        };
        this.mWifiReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkPolicyManagerService.10
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                NetworkPolicyManagerService.this.upgradeWifiMeteredOverride();
                NetworkPolicyManagerService.this.mContext.unregisterReceiver(this);
            }
        };
        this.mNetworkCallback = new ConnectivityManager.NetworkCallback() { // from class: com.android.server.net.NetworkPolicyManagerService.11
            /* JADX WARN: Code restructure failed: missing block: B:19:0x0039, code lost:
                r6.this$0.mLogger.meterednessChanged(r7.getNetId(), r1);
             */
            @Override // android.net.ConnectivityManager.NetworkCallback
            /*
                Code decompiled incorrectly, please refer to instructions dump.
            */
            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                    boolean z2 = true;
                    boolean z3 = !networkCapabilities.hasCapability(11);
                    boolean updateCapabilityChange = NetworkPolicyManagerService.updateCapabilityChange(NetworkPolicyManagerService.this.mNetworkMetered, z3, network);
                    boolean z4 = !networkCapabilities.hasCapability(18);
                    boolean updateCapabilityChange2 = NetworkPolicyManagerService.updateCapabilityChange(NetworkPolicyManagerService.this.mNetworkRoaming, z4, network);
                    if (!updateCapabilityChange && !updateCapabilityChange2) {
                        z2 = false;
                    }
                    if (updateCapabilityChange2) {
                        NetworkPolicyManagerService.this.mLogger.roamingChanged(network.getNetId(), z4);
                    }
                    if (z2) {
                        NetworkPolicyManagerService.this.updateNetworkRulesNL();
                    }
                }
            }

            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
                synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                    ArraySet<String> arraySet = new ArraySet<>(linkProperties.getAllInterfaceNames());
                    if (NetworkPolicyManagerService.this.updateNetworkToIfacesNL(network.getNetId(), arraySet)) {
                        NetworkPolicyManagerService.this.mLogger.interfacesChanged(network.getNetId(), arraySet);
                        NetworkPolicyManagerService.this.updateNetworkRulesNL();
                    }
                }
            }

            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onLost(Network network) {
                synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                    NetworkPolicyManagerService.this.mNetworkToIfaces.remove(network.getNetId());
                }
            }
        };
        this.mAlertObserver = new BaseNetworkObserver() { // from class: com.android.server.net.NetworkPolicyManagerService.12
            public void limitReached(String str, String str2) {
                NetworkStack.checkNetworkStackPermission(NetworkPolicyManagerService.this.mContext);
                if ("globalAlert".equals(str)) {
                    return;
                }
                NetworkPolicyManagerService.this.mHandler.obtainMessage(5, str2).sendToTarget();
            }
        };
        this.mConnReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkPolicyManagerService.13
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                NetworkPolicyManagerService.this.updateNetworksInternal();
            }
        };
        this.mCarrierConfigReceiver = new BroadcastReceiver() { // from class: com.android.server.net.NetworkPolicyManagerService.14
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (intent.hasExtra("android.telephony.extra.SUBSCRIPTION_INDEX")) {
                    int intExtra = intent.getIntExtra("android.telephony.extra.SUBSCRIPTION_INDEX", -1);
                    NetworkPolicyManagerService.this.updateSubscriptions();
                    synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                        synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                            String str = (String) NetworkPolicyManagerService.this.mSubIdToSubscriberId.get(intExtra, null);
                            if (str != null) {
                                NetworkPolicyManagerService.this.ensureActiveCarrierPolicyAL(intExtra, str);
                                NetworkPolicyManagerService.this.maybeUpdateCarrierPolicyCycleAL(intExtra, str);
                            } else {
                                Slog.wtf("NetworkPolicy", "Missing subscriberId for subId " + intExtra);
                            }
                            NetworkPolicyManagerService.this.handleNetworkPoliciesUpdateAL(true);
                        }
                    }
                }
            }
        };
        Handler.Callback callback = new Handler.Callback() { // from class: com.android.server.net.NetworkPolicyManagerService.15
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case 1:
                        int i = message.arg1;
                        int i2 = message.arg2;
                        if (NetworkPolicyManagerService.LOGV) {
                            Slog.v("NetworkPolicy", "Dispatching rules=" + NetworkPolicyManager.uidRulesToString(i2) + " for uid=" + i);
                        }
                        int beginBroadcast = NetworkPolicyManagerService.this.mListeners.beginBroadcast();
                        for (int i3 = 0; i3 < beginBroadcast; i3++) {
                            NetworkPolicyManagerService.this.dispatchUidRulesChanged(NetworkPolicyManagerService.this.mListeners.getBroadcastItem(i3), i, i2);
                        }
                        NetworkPolicyManagerService.this.mListeners.finishBroadcast();
                        return true;
                    case 2:
                        String[] strArr = (String[]) message.obj;
                        int beginBroadcast2 = NetworkPolicyManagerService.this.mListeners.beginBroadcast();
                        for (int i4 = 0; i4 < beginBroadcast2; i4++) {
                            NetworkPolicyManagerService.this.dispatchMeteredIfacesChanged(NetworkPolicyManagerService.this.mListeners.getBroadcastItem(i4), strArr);
                        }
                        NetworkPolicyManagerService.this.mListeners.finishBroadcast();
                        return true;
                    case 3:
                    case 4:
                    case 8:
                    case 9:
                    case 12:
                    case 14:
                    default:
                        return false;
                    case 5:
                        String str = (String) message.obj;
                        synchronized (NetworkPolicyManagerService.this.mMeteredIfacesLock) {
                            if (NetworkPolicyManagerService.this.mMeteredIfaces.contains(str)) {
                                NetworkPolicyManagerService.this.mNetworkStats.forceUpdate();
                                synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                                    NetworkPolicyManagerService.this.updateNetworkRulesNL();
                                    NetworkPolicyManagerService.this.updateNetworkEnabledNL();
                                    NetworkPolicyManagerService.this.updateNotificationsNL();
                                }
                                return true;
                            }
                            return true;
                        }
                    case 6:
                        boolean z2 = message.arg1 != 0;
                        int beginBroadcast3 = NetworkPolicyManagerService.this.mListeners.beginBroadcast();
                        for (int i5 = 0; i5 < beginBroadcast3; i5++) {
                            NetworkPolicyManagerService.this.dispatchRestrictBackgroundChanged(NetworkPolicyManagerService.this.mListeners.getBroadcastItem(i5), z2);
                        }
                        NetworkPolicyManagerService.this.mListeners.finishBroadcast();
                        Intent intent = new Intent("android.net.conn.RESTRICT_BACKGROUND_CHANGED");
                        intent.setFlags(1073741824);
                        NetworkPolicyManagerService.this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
                        return true;
                    case 7:
                        NetworkPolicyManagerService.this.mNetworkStats.setDefaultGlobalAlert(((Long) message.obj).longValue() / 1000);
                        return true;
                    case 10:
                        IfaceQuotas ifaceQuotas = (IfaceQuotas) message.obj;
                        NetworkPolicyManagerService.this.removeInterfaceLimit(ifaceQuotas.iface);
                        NetworkPolicyManagerService.this.setInterfaceLimit(ifaceQuotas.iface, ifaceQuotas.limit);
                        NetworkPolicyManagerService.this.mNetworkStats.setStatsProviderWarningAndLimitAsync(ifaceQuotas.iface, ifaceQuotas.warning, ifaceQuotas.limit);
                        return true;
                    case 11:
                        String str2 = (String) message.obj;
                        NetworkPolicyManagerService.this.removeInterfaceLimit(str2);
                        NetworkPolicyManagerService.this.mNetworkStats.setStatsProviderWarningAndLimitAsync(str2, -1L, -1L);
                        return true;
                    case 13:
                        int i6 = message.arg1;
                        int i7 = message.arg2;
                        Boolean bool = (Boolean) message.obj;
                        int beginBroadcast4 = NetworkPolicyManagerService.this.mListeners.beginBroadcast();
                        for (int i8 = 0; i8 < beginBroadcast4; i8++) {
                            NetworkPolicyManagerService.this.dispatchUidPoliciesChanged(NetworkPolicyManagerService.this.mListeners.getBroadcastItem(i8), i6, i7);
                        }
                        NetworkPolicyManagerService.this.mListeners.finishBroadcast();
                        if (bool.booleanValue()) {
                            NetworkPolicyManagerService.this.broadcastRestrictBackgroundChanged(i6, bool);
                        }
                        return true;
                    case 15:
                        NetworkPolicyManagerService.this.resetUidFirewallRules(message.arg1);
                        return true;
                    case 16:
                        SomeArgs someArgs = (SomeArgs) message.obj;
                        int intValue = ((Integer) someArgs.arg1).intValue();
                        int intValue2 = ((Integer) someArgs.arg2).intValue();
                        int intValue3 = ((Integer) someArgs.arg3).intValue();
                        int[] iArr = (int[]) someArgs.arg4;
                        int beginBroadcast5 = NetworkPolicyManagerService.this.mListeners.beginBroadcast();
                        for (int i9 = 0; i9 < beginBroadcast5; i9++) {
                            NetworkPolicyManagerService.this.dispatchSubscriptionOverride(NetworkPolicyManagerService.this.mListeners.getBroadcastItem(i9), intValue, intValue2, intValue3, iArr);
                        }
                        NetworkPolicyManagerService.this.mListeners.finishBroadcast();
                        return true;
                    case 17:
                        NetworkPolicyManagerService.this.setMeteredRestrictedPackagesInternal((Set) message.obj, message.arg1);
                        return true;
                    case 18:
                        NetworkPolicyManagerService.this.setNetworkTemplateEnabledInner((NetworkTemplate) message.obj, message.arg1 != 0);
                        return true;
                    case 19:
                        SubscriptionPlan[] subscriptionPlanArr = (SubscriptionPlan[]) message.obj;
                        int i10 = message.arg1;
                        int beginBroadcast6 = NetworkPolicyManagerService.this.mListeners.beginBroadcast();
                        for (int i11 = 0; i11 < beginBroadcast6; i11++) {
                            NetworkPolicyManagerService.this.dispatchSubscriptionPlansChanged(NetworkPolicyManagerService.this.mListeners.getBroadcastItem(i11), i10, subscriptionPlanArr);
                        }
                        NetworkPolicyManagerService.this.mListeners.finishBroadcast();
                        return true;
                    case 20:
                        NetworkPolicyManagerService.this.mNetworkStats.forceUpdate();
                        synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                            NetworkPolicyManagerService.this.updateNetworkRulesNL();
                            NetworkPolicyManagerService.this.updateNetworkEnabledNL();
                            NetworkPolicyManagerService.this.updateNotificationsNL();
                        }
                        return true;
                    case 21:
                        int i12 = message.arg1;
                        int i13 = message.arg2;
                        int intValue4 = ((Integer) message.obj).intValue();
                        int beginBroadcast7 = NetworkPolicyManagerService.this.mListeners.beginBroadcast();
                        for (int i14 = 0; i14 < beginBroadcast7; i14++) {
                            NetworkPolicyManagerService.this.dispatchBlockedReasonChanged(NetworkPolicyManagerService.this.mListeners.getBroadcastItem(i14), i12, intValue4, i13);
                        }
                        NetworkPolicyManagerService.this.mListeners.finishBroadcast();
                        return true;
                    case 22:
                        synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                            synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                                int i15 = message.arg1;
                                if (message.arg2 == NetworkPolicyManagerService.this.mSetSubscriptionPlansIds.get(i15)) {
                                    if (NetworkPolicyManagerService.LOGD) {
                                        Slog.d("NetworkPolicy", "Clearing expired subscription plans.");
                                    }
                                    NetworkPolicyManagerService.this.setSubscriptionPlansInternal(i15, new SubscriptionPlan[0], 0L, (String) message.obj);
                                } else if (NetworkPolicyManagerService.LOGD) {
                                    Slog.d("NetworkPolicy", "Ignoring stale CLEAR_SUBSCRIPTION_PLANS.");
                                }
                            }
                        }
                        return true;
                    case 23:
                        SparseArray sparseArray = (SparseArray) message.obj;
                        int size = sparseArray.size();
                        int beginBroadcast8 = NetworkPolicyManagerService.this.mListeners.beginBroadcast();
                        for (int i16 = 0; i16 < beginBroadcast8; i16++) {
                            INetworkPolicyListener broadcastItem = NetworkPolicyManagerService.this.mListeners.getBroadcastItem(i16);
                            for (int i17 = 0; i17 < size; i17++) {
                                int keyAt = sparseArray.keyAt(i17);
                                SomeArgs someArgs2 = (SomeArgs) sparseArray.valueAt(i17);
                                int i18 = someArgs2.argi1;
                                int i19 = someArgs2.argi2;
                                int i20 = someArgs2.argi3;
                                NetworkPolicyManagerService.this.dispatchBlockedReasonChanged(broadcastItem, keyAt, i18, i19);
                                if (NetworkPolicyManagerService.LOGV) {
                                    Slog.v("NetworkPolicy", "Dispatching rules=" + NetworkPolicyManager.uidRulesToString(i20) + " for uid=" + keyAt);
                                }
                                NetworkPolicyManagerService.this.dispatchUidRulesChanged(broadcastItem, keyAt, i20);
                            }
                        }
                        NetworkPolicyManagerService.this.mListeners.finishBroadcast();
                        for (int i21 = 0; i21 < size; i21++) {
                            ((SomeArgs) sparseArray.valueAt(i21)).recycle();
                        }
                        return true;
                }
            }
        };
        this.mHandlerCallback = callback;
        Handler.Callback callback2 = new Handler.Callback() { // from class: com.android.server.net.NetworkPolicyManagerService.16
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message message) {
                int i = message.what;
                if (i == 100) {
                    NetworkPolicyManagerService.this.handleUidChanged((UidStateCallbackInfo) message.obj);
                    return true;
                } else if (i != 101) {
                    return false;
                } else {
                    NetworkPolicyManagerService.this.handleUidGone(message.arg1);
                    return true;
                }
            }
        };
        this.mUidEventHandlerCallback = callback2;
        Objects.requireNonNull(context, "missing context");
        this.mContext = context;
        Objects.requireNonNull(iActivityManager, "missing activityManager");
        this.mActivityManager = iActivityManager;
        Objects.requireNonNull(iNetworkManagementService, "missing networkManagement");
        this.mNetworkManager = iNetworkManagementService;
        this.mPowerWhitelistManager = (PowerWhitelistManager) context.getSystemService(PowerWhitelistManager.class);
        Objects.requireNonNull(clock, "missing Clock");
        this.mClock = clock;
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        this.mIPm = iPackageManager;
        HandlerThread handlerThread = new HandlerThread("NetworkPolicy");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper(), callback);
        this.mHandler = handler;
        ServiceThread serviceThread = new ServiceThread("NetworkPolicy.uid", -2, false);
        this.mUidEventThread = serviceThread;
        serviceThread.start();
        this.mUidEventHandler = new Handler(serviceThread.getLooper(), callback2);
        this.mSuppressDefaultPolicy = z;
        Objects.requireNonNull(dependencies, "missing Dependencies");
        this.mDeps = dependencies;
        this.mPolicyFile = new AtomicFile(new File(file, "netpolicy.xml"), "net-policy");
        this.mAppOps = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        this.mNetworkStats = (NetworkStatsManager) context.getSystemService(NetworkStatsManager.class);
        this.mMultipathPolicyTracker = new MultipathPolicyTracker(context, handler);
        LocalServices.addService(NetworkPolicyManagerInternal.class, new NetworkPolicyManagerInternalImpl());
    }

    public void bindConnectivityManager() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class);
        Objects.requireNonNull(connectivityManager, "missing ConnectivityManager");
        this.mConnManager = connectivityManager;
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final void updatePowerSaveWhitelistUL() {
        int[] whitelistedAppIds = this.mPowerWhitelistManager.getWhitelistedAppIds(false);
        this.mPowerSaveWhitelistExceptIdleAppIds.clear();
        for (int i : whitelistedAppIds) {
            this.mPowerSaveWhitelistExceptIdleAppIds.put(i, true);
        }
        int[] whitelistedAppIds2 = this.mPowerWhitelistManager.getWhitelistedAppIds(true);
        this.mPowerSaveWhitelistAppIds.clear();
        for (int i2 : whitelistedAppIds2) {
            this.mPowerSaveWhitelistAppIds.put(i2, true);
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public boolean addDefaultRestrictBackgroundAllowlistUidsUL() {
        List users = this.mUserManager.getUsers();
        int size = users.size();
        boolean z = false;
        for (int i = 0; i < size; i++) {
            z = addDefaultRestrictBackgroundAllowlistUidsUL(((UserInfo) users.get(i)).id) || z;
        }
        return z;
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final boolean addDefaultRestrictBackgroundAllowlistUidsUL(int i) {
        SystemConfig systemConfig = SystemConfig.getInstance();
        PackageManager packageManager = this.mContext.getPackageManager();
        ArraySet<String> allowInDataUsageSave = systemConfig.getAllowInDataUsageSave();
        boolean z = false;
        for (int i2 = 0; i2 < allowInDataUsageSave.size(); i2++) {
            String valueAt = allowInDataUsageSave.valueAt(i2);
            boolean z2 = LOGD;
            if (z2) {
                Slog.d("NetworkPolicy", "checking restricted background exemption for package " + valueAt + " and user " + i);
            }
            try {
                ApplicationInfo applicationInfoAsUser = packageManager.getApplicationInfoAsUser(valueAt, 1048576, i);
                if (applicationInfoAsUser.isPrivilegedApp()) {
                    int uid = UserHandle.getUid(i, applicationInfoAsUser.uid);
                    this.mDefaultRestrictBackgroundAllowlistUids.append(uid, true);
                    if (z2) {
                        Slog.d("NetworkPolicy", "Adding uid " + uid + " (user " + i + ") to default restricted background allowlist. Revoked status: " + this.mRestrictBackgroundAllowlistRevokedUids.get(uid));
                    }
                    if (!this.mRestrictBackgroundAllowlistRevokedUids.get(uid)) {
                        if (z2) {
                            Slog.d("NetworkPolicy", "adding default package " + valueAt + " (uid " + uid + " for user " + i + ") to restrict background allowlist");
                        }
                        setUidPolicyUncheckedUL(uid, 4, false);
                        z = true;
                    }
                } else {
                    Slog.e("NetworkPolicy", "addDefaultRestrictBackgroundAllowlistUidsUL(): skipping non-privileged app  " + valueAt);
                }
            } catch (PackageManager.NameNotFoundException unused) {
                if (LOGD) {
                    Slog.d("NetworkPolicy", "No ApplicationInfo for package " + valueAt);
                }
            }
        }
        return z;
    }

    /* renamed from: initService */
    public final void lambda$networkScoreAndNetworkManagementServiceReady$1(CountDownLatch countDownLatch) {
        Trace.traceBegin(2097152L, "systemReady");
        int threadPriority = Process.getThreadPriority(Process.myTid());
        try {
            Process.setThreadPriority(-2);
            if (!isBandwidthControlEnabled()) {
                Slog.w("NetworkPolicy", "bandwidth controls disabled, unable to enforce policy");
                return;
            }
            this.mUsageStats = (UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class);
            this.mAppStandby = (AppStandbyInternal) LocalServices.getService(AppStandbyInternal.class);
            this.mActivityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
            synchronized (this.mUidRulesFirstLock) {
                synchronized (this.mNetworkPoliciesSecondLock) {
                    updatePowerSaveWhitelistUL();
                    PowerManagerInternal powerManagerInternal = (PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class);
                    this.mPowerManagerInternal = powerManagerInternal;
                    powerManagerInternal.registerLowPowerModeObserver(new PowerManagerInternal.LowPowerModeListener() { // from class: com.android.server.net.NetworkPolicyManagerService.1
                        public int getServiceType() {
                            return 6;
                        }

                        public void onLowPowerModeChanged(PowerSaveState powerSaveState) {
                            boolean z = powerSaveState.batterySaverEnabled;
                            if (NetworkPolicyManagerService.LOGD) {
                                Slog.d("NetworkPolicy", "onLowPowerModeChanged(" + z + ")");
                            }
                            synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                                if (NetworkPolicyManagerService.this.mRestrictPower != z) {
                                    NetworkPolicyManagerService.this.mRestrictPower = z;
                                    NetworkPolicyManagerService.this.updateRulesForRestrictPowerUL();
                                }
                            }
                        }
                    });
                    this.mRestrictPower = this.mPowerManagerInternal.getLowPowerState(6).batterySaverEnabled;
                    RestrictedModeObserver restrictedModeObserver = new RestrictedModeObserver(this.mContext, new RestrictedModeObserver.RestrictedModeListener() { // from class: com.android.server.net.NetworkPolicyManagerService$$ExternalSyntheticLambda7
                        @Override // com.android.server.net.NetworkPolicyManagerService.RestrictedModeObserver.RestrictedModeListener
                        public final void onChange(boolean z) {
                            NetworkPolicyManagerService.this.lambda$initService$0(z);
                        }
                    });
                    this.mRestrictedModeObserver = restrictedModeObserver;
                    this.mRestrictedNetworkingMode = restrictedModeObserver.isRestrictedModeEnabled();
                    this.mSystemReady = true;
                    waitForAdminData();
                    readPolicyAL();
                    this.mRestrictBackgroundBeforeBsm = this.mLoadedRestrictBackground;
                    boolean z = this.mPowerManagerInternal.getLowPowerState(10).batterySaverEnabled;
                    this.mRestrictBackgroundLowPowerMode = z;
                    if (z && !this.mLoadedRestrictBackground) {
                        this.mLoadedRestrictBackground = true;
                    }
                    this.mPowerManagerInternal.registerLowPowerModeObserver(new PowerManagerInternal.LowPowerModeListener() { // from class: com.android.server.net.NetworkPolicyManagerService.2
                        public int getServiceType() {
                            return 10;
                        }

                        public void onLowPowerModeChanged(PowerSaveState powerSaveState) {
                            synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                                NetworkPolicyManagerService.this.updateRestrictBackgroundByLowPowerModeUL(powerSaveState);
                            }
                        }
                    });
                    if (addDefaultRestrictBackgroundAllowlistUidsUL()) {
                        writePolicyAL();
                    }
                    setRestrictBackgroundUL(this.mLoadedRestrictBackground, "init_service");
                    updateRulesForGlobalChangeAL(false);
                    updateNotificationsNL();
                }
            }
            try {
                this.mActivityManagerInternal.registerNetworkPolicyUidObserver(this.mUidObserver, 35, 5, PackageManagerShellCommandDataLoader.PACKAGE);
                this.mNetworkManager.registerObserver(this.mAlertObserver);
            } catch (RemoteException unused) {
            }
            this.mContext.registerReceiver(this.mPowerSaveWhitelistReceiver, new IntentFilter("android.os.action.POWER_SAVE_WHITELIST_CHANGED"), null, this.mHandler);
            this.mContext.registerReceiver(this.mConnReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"), "android.permission.NETWORK_STACK", this.mHandler);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
            intentFilter.addDataScheme("package");
            this.mContext.registerReceiverForAllUsers(this.mPackageReceiver, intentFilter, null, this.mHandler);
            this.mContext.registerReceiverForAllUsers(this.mUidRemovedReceiver, new IntentFilter("android.intent.action.UID_REMOVED"), null, this.mHandler);
            IntentFilter intentFilter2 = new IntentFilter();
            intentFilter2.addAction("android.intent.action.USER_ADDED");
            intentFilter2.addAction("android.intent.action.USER_REMOVED");
            this.mContext.registerReceiver(this.mUserReceiver, intentFilter2, null, this.mHandler);
            Executor handlerExecutor = new HandlerExecutor(this.mHandler);
            this.mNetworkStats.registerUsageCallback(new NetworkTemplate.Builder(1).build(), 0L, handlerExecutor, this.mStatsCallback);
            this.mNetworkStats.registerUsageCallback(new NetworkTemplate.Builder(4).build(), 0L, handlerExecutor, this.mStatsCallback);
            this.mContext.registerReceiver(this.mSnoozeReceiver, new IntentFilter("com.android.server.net.action.SNOOZE_WARNING"), "android.permission.MANAGE_NETWORK_POLICY", this.mHandler);
            this.mContext.registerReceiver(this.mSnoozeReceiver, new IntentFilter("com.android.server.net.action.SNOOZE_RAPID"), "android.permission.MANAGE_NETWORK_POLICY", this.mHandler);
            this.mContext.registerReceiver(this.mWifiReceiver, new IntentFilter("android.net.wifi.CONFIGURED_NETWORKS_CHANGE"), null, this.mHandler);
            this.mContext.registerReceiver(this.mCarrierConfigReceiver, new IntentFilter("android.telephony.action.CARRIER_CONFIG_CHANGED"), null, this.mHandler);
            this.mConnManager.registerNetworkCallback(new NetworkRequest.Builder().build(), this.mNetworkCallback);
            this.mAppStandby.addListener(new NetPolicyAppIdleStateChangeListener());
            synchronized (this.mUidRulesFirstLock) {
                updateRulesForAppIdleParoleUL();
            }
            ((SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class)).addOnSubscriptionsChangedListener(new HandlerExecutor(this.mHandler), new SubscriptionManager.OnSubscriptionsChangedListener() { // from class: com.android.server.net.NetworkPolicyManagerService.3
                @Override // android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
                public void onSubscriptionsChanged() {
                    NetworkPolicyManagerService.this.updateNetworksInternal();
                }
            });
            countDownLatch.countDown();
        } finally {
            Process.setThreadPriority(threadPriority);
            Trace.traceEnd(2097152L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initService$0(boolean z) {
        synchronized (this.mUidRulesFirstLock) {
            this.mRestrictedNetworkingMode = z;
            updateRestrictedModeAllowlistUL();
        }
    }

    public CountDownLatch networkScoreAndNetworkManagementServiceReady() {
        this.mNetworkManagerReady = true;
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.mHandler.post(new Runnable() { // from class: com.android.server.net.NetworkPolicyManagerService$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                NetworkPolicyManagerService.this.lambda$networkScoreAndNetworkManagementServiceReady$1(countDownLatch);
            }
        });
        return countDownLatch;
    }

    public void systemReady(CountDownLatch countDownLatch) {
        try {
            if (!countDownLatch.await(30L, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Service NetworkPolicy init timeout");
            }
            this.mMultipathPolicyTracker.start();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Service NetworkPolicy init interrupted", e);
        }
    }

    /* loaded from: classes2.dex */
    public static final class UidStateCallbackInfo {
        public int capability;
        public boolean isPending;
        public int procState;
        public long procStateSeq;
        public int uid;

        public UidStateCallbackInfo() {
            this.procState = 20;
            this.procStateSeq = -1L;
        }

        public void update(int i, int i2, long j, int i3) {
            this.uid = i;
            this.procState = i2;
            this.procStateSeq = j;
            this.capability = i3;
        }
    }

    /* loaded from: classes2.dex */
    public class StatsCallback extends NetworkStatsManager.UsageCallback {
        public boolean mIsAnyCallbackReceived;

        public StatsCallback() {
            this.mIsAnyCallbackReceived = false;
        }

        @Override // android.app.usage.NetworkStatsManager.UsageCallback
        public void onThresholdReached(int i, String str) {
            this.mIsAnyCallbackReceived = true;
            synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                NetworkPolicyManagerService.this.updateNetworkRulesNL();
                NetworkPolicyManagerService.this.updateNetworkEnabledNL();
                NetworkPolicyManagerService.this.updateNotificationsNL();
            }
        }

        public boolean isAnyCallbackReceived() {
            return this.mIsAnyCallbackReceived;
        }
    }

    public static boolean updateCapabilityChange(SparseBooleanArray sparseBooleanArray, boolean z, Network network) {
        boolean z2 = false;
        z2 = (sparseBooleanArray.get(network.getNetId(), false) != z || sparseBooleanArray.indexOfKey(network.getNetId()) < 0) ? true : true;
        if (z2) {
            sparseBooleanArray.put(network.getNetId(), z);
        }
        return z2;
    }

    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final boolean updateNetworkToIfacesNL(int i, ArraySet<String> arraySet) {
        ArraySet arraySet2 = this.mNetworkToIfaces.get(i);
        boolean z = true;
        if (arraySet2 != null && arraySet2.equals(arraySet)) {
            z = false;
        }
        if (z) {
            this.mNetworkToIfaces.remove(i);
            Iterator<String> it = arraySet.iterator();
            while (it.hasNext()) {
                this.mNetworkToIfaces.add(i, it.next());
            }
        }
        return z;
    }

    /* JADX WARN: Removed duplicated region for block: B:32:0x00d6  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x0139  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x0168  */
    @GuardedBy({"mNetworkPoliciesSecondLock"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void updateNotificationsNL() {
        long j;
        long j2;
        long j3;
        long j4;
        if (LOGV) {
            Slog.v("NetworkPolicy", "updateNotificationsNL()");
        }
        Trace.traceBegin(2097152L, "updateNotificationsNL");
        ArraySet arraySet = new ArraySet((ArraySet) this.mActiveNotifs);
        this.mActiveNotifs.clear();
        long millis = this.mClock.millis();
        for (int size = this.mNetworkPolicy.size() - 1; size >= 0; size--) {
            NetworkPolicy valueAt = this.mNetworkPolicy.valueAt(size);
            int findRelevantSubIdNL = findRelevantSubIdNL(valueAt.template);
            if (findRelevantSubIdNL != -1 && valueAt.hasCycle()) {
                Pair pair = (Pair) NetworkPolicyManager.cycleIterator(valueAt).next();
                long epochMilli = ((ZonedDateTime) pair.first).toInstant().toEpochMilli();
                long epochMilli2 = ((ZonedDateTime) pair.second).toInstant().toEpochMilli();
                long totalBytes = getTotalBytes(valueAt.template, epochMilli, epochMilli2);
                PersistableBundle persistableBundle = this.mSubIdToCarrierConfig.get(findRelevantSubIdNL);
                if (!CarrierConfigManager.isConfigForIdentifiedCarrier(persistableBundle)) {
                    if (LOGV) {
                        Slog.v("NetworkPolicy", "isConfigForIdentifiedCarrier returned false");
                    }
                } else {
                    boolean booleanDefeatingNullable = getBooleanDefeatingNullable(persistableBundle, "data_warning_notification_bool", true);
                    boolean booleanDefeatingNullable2 = getBooleanDefeatingNullable(persistableBundle, "data_limit_notification_bool", true);
                    boolean booleanDefeatingNullable3 = getBooleanDefeatingNullable(persistableBundle, "data_rapid_notification_bool", true);
                    if (booleanDefeatingNullable && valueAt.isOverWarning(totalBytes) && !valueAt.isOverLimit(totalBytes)) {
                        if (!(valueAt.lastWarningSnooze >= epochMilli)) {
                            j = totalBytes;
                            enqueueNotification(valueAt, 34, totalBytes, null);
                            if (booleanDefeatingNullable2) {
                                long j5 = j;
                                if (valueAt.isOverLimit(j5)) {
                                    if (valueAt.lastLimitSnooze >= epochMilli) {
                                        enqueueNotification(valueAt, 36, j5, null);
                                    } else {
                                        enqueueNotification(valueAt, 35, j5, null);
                                        notifyOverLimitNL(valueAt.template);
                                    }
                                } else {
                                    notifyUnderLimitNL(valueAt.template);
                                }
                            }
                            if (booleanDefeatingNullable3 && valueAt.limitBytes != -1) {
                                long millis2 = TimeUnit.DAYS.toMillis(4L);
                                j2 = millis - millis2;
                                long totalBytes2 = getTotalBytes(valueAt.template, j2, millis);
                                j3 = ((epochMilli2 - epochMilli) * totalBytes2) / millis2;
                                j4 = (valueAt.limitBytes * 3) / 2;
                                if (LOGD) {
                                    Slog.d("NetworkPolicy", "Rapid usage considering recent " + totalBytes2 + " projected " + j3 + " alert " + j4);
                                }
                                boolean z = valueAt.lastRapidSnooze >= millis - BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS;
                                if (j3 > j4 && !z) {
                                    enqueueNotification(valueAt, 45, 0L, findRapidBlame(valueAt.template, j2, millis));
                                }
                            }
                        }
                    }
                    j = totalBytes;
                    if (booleanDefeatingNullable2) {
                    }
                    if (booleanDefeatingNullable3) {
                        long millis22 = TimeUnit.DAYS.toMillis(4L);
                        j2 = millis - millis22;
                        long totalBytes22 = getTotalBytes(valueAt.template, j2, millis);
                        j3 = ((epochMilli2 - epochMilli) * totalBytes22) / millis22;
                        j4 = (valueAt.limitBytes * 3) / 2;
                        if (LOGD) {
                        }
                        if (valueAt.lastRapidSnooze >= millis - BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS) {
                        }
                        if (j3 > j4) {
                            enqueueNotification(valueAt, 45, 0L, findRapidBlame(valueAt.template, j2, millis));
                        }
                    }
                }
            }
        }
        for (int size2 = arraySet.size() - 1; size2 >= 0; size2--) {
            NotificationId notificationId = (NotificationId) arraySet.valueAt(size2);
            if (!this.mActiveNotifs.contains(notificationId)) {
                cancelNotification(notificationId);
            }
        }
        Trace.traceEnd(2097152L);
    }

    public final ApplicationInfo findRapidBlame(NetworkTemplate networkTemplate, long j, long j2) {
        String[] packagesForUid;
        if (this.mStatsCallback.isAnyCallbackReceived()) {
            long j3 = 0;
            long j4 = 0;
            int i = 0;
            for (NetworkStats.Bucket bucket : this.mDeps.getNetworkUidBytes(networkTemplate, j, j2)) {
                long rxBytes = bucket.getRxBytes() + bucket.getTxBytes();
                j4 += rxBytes;
                if (rxBytes > j3) {
                    i = bucket.getUid();
                    j3 = rxBytes;
                }
            }
            if (j3 > 0 && j3 > j4 / 2 && (packagesForUid = this.mContext.getPackageManager().getPackagesForUid(i)) != null && packagesForUid.length == 1) {
                try {
                    return this.mContext.getPackageManager().getApplicationInfo(packagesForUid[0], 4989440);
                } catch (PackageManager.NameNotFoundException unused) {
                }
            }
            return null;
        }
        return null;
    }

    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final int findRelevantSubIdNL(NetworkTemplate networkTemplate) {
        for (int i = 0; i < this.mSubIdToSubscriberId.size(); i++) {
            int keyAt = this.mSubIdToSubscriberId.keyAt(i);
            if (networkTemplate.matches(new NetworkIdentity.Builder().setType(0).setSubscriberId(this.mSubIdToSubscriberId.valueAt(i)).setMetered(true).setDefaultNetwork(true).setSubId(keyAt).build())) {
                return keyAt;
            }
        }
        return -1;
    }

    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final void notifyOverLimitNL(NetworkTemplate networkTemplate) {
        if (this.mOverLimitNotified.contains(networkTemplate)) {
            return;
        }
        Context context = this.mContext;
        context.startActivity(buildNetworkOverLimitIntent(context.getResources(), networkTemplate));
        this.mOverLimitNotified.add(networkTemplate);
    }

    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final void notifyUnderLimitNL(NetworkTemplate networkTemplate) {
        this.mOverLimitNotified.remove(networkTemplate);
    }

    public final void enqueueNotification(NetworkPolicy networkPolicy, int i, long j, ApplicationInfo applicationInfo) {
        CharSequence text;
        String string;
        NotificationId notificationId = new NotificationId(networkPolicy, i);
        Notification.Builder builder = new Notification.Builder(this.mContext, SystemNotificationChannels.NETWORK_ALERTS);
        builder.setOnlyAlertOnce(true);
        builder.setWhen(0L);
        builder.setColor(this.mContext.getColor(17170460));
        Resources resources = this.mContext.getResources();
        if (i != 45) {
            switch (i) {
                case 34:
                    text = resources.getText(17040078);
                    string = resources.getString(17040077, Formatter.formatFileSize(this.mContext, j, 8));
                    builder.setSmallIcon(17301624);
                    builder.setDeleteIntent(PendingIntent.getBroadcast(this.mContext, 0, buildSnoozeWarningIntent(networkPolicy.template, this.mContext.getPackageName()), 201326592));
                    setContentIntent(builder, buildViewDataUsageIntent(resources, networkPolicy.template));
                    break;
                case 35:
                    int matchRule = networkPolicy.template.getMatchRule();
                    if (matchRule != 1) {
                        if (matchRule == 4) {
                            text = resources.getText(17040080);
                            string = resources.getText(17040068);
                            builder.setOngoing(true);
                            builder.setSmallIcon(17303586);
                            setContentIntent(builder, buildNetworkOverLimitIntent(resources, networkPolicy.template));
                            break;
                        } else if (matchRule != 10) {
                            return;
                        }
                    }
                    text = resources.getText(17040071);
                    string = resources.getText(17040068);
                    builder.setOngoing(true);
                    builder.setSmallIcon(17303586);
                    setContentIntent(builder, buildNetworkOverLimitIntent(resources, networkPolicy.template));
                case 36:
                    int matchRule2 = networkPolicy.template.getMatchRule();
                    if (matchRule2 != 1) {
                        if (matchRule2 == 4) {
                            text = resources.getText(17040079);
                            string = resources.getString(17040069, Formatter.formatFileSize(this.mContext, j - networkPolicy.limitBytes, 8));
                            builder.setOngoing(true);
                            builder.setSmallIcon(17301624);
                            builder.setChannelId(SystemNotificationChannels.NETWORK_STATUS);
                            setContentIntent(builder, buildViewDataUsageIntent(resources, networkPolicy.template));
                            break;
                        } else if (matchRule2 != 10) {
                            return;
                        }
                    }
                    text = resources.getText(17040070);
                    string = resources.getString(17040069, Formatter.formatFileSize(this.mContext, j - networkPolicy.limitBytes, 8));
                    builder.setOngoing(true);
                    builder.setSmallIcon(17301624);
                    builder.setChannelId(SystemNotificationChannels.NETWORK_STATUS);
                    setContentIntent(builder, buildViewDataUsageIntent(resources, networkPolicy.template));
                default:
                    return;
            }
        } else {
            text = resources.getText(17040074);
            if (applicationInfo != null) {
                string = resources.getString(17040072, applicationInfo.loadLabel(this.mContext.getPackageManager()));
            } else {
                string = resources.getString(17040073);
            }
            builder.setSmallIcon(17301624);
            builder.setDeleteIntent(PendingIntent.getBroadcast(this.mContext, 0, buildSnoozeRapidIntent(networkPolicy.template, this.mContext.getPackageName()), 201326592));
            setContentIntent(builder, buildViewDataUsageIntent(resources, networkPolicy.template));
        }
        builder.setTicker(text);
        builder.setContentTitle(text);
        builder.setContentText(string);
        builder.setStyle(new Notification.BigTextStyle().bigText(string));
        ((NotificationManager) this.mContext.getSystemService(NotificationManager.class)).notifyAsUser(notificationId.getTag(), notificationId.getId(), builder.build(), UserHandle.ALL);
        this.mActiveNotifs.add(notificationId);
    }

    public final void setContentIntent(Notification.Builder builder, Intent intent) {
        if (UserManager.isHeadlessSystemUserMode()) {
            builder.setContentIntent(PendingIntent.getActivityAsUser(this.mContext, 0, intent, 201326592, null, UserHandle.CURRENT));
        } else {
            builder.setContentIntent(PendingIntent.getActivity(this.mContext, 0, intent, 201326592));
        }
    }

    public final void cancelNotification(NotificationId notificationId) {
        ((NotificationManager) this.mContext.getSystemService(NotificationManager.class)).cancel(notificationId.getTag(), notificationId.getId());
    }

    public final void updateNetworksInternal() {
        updateSubscriptions();
        synchronized (this.mUidRulesFirstLock) {
            synchronized (this.mNetworkPoliciesSecondLock) {
                ensureActiveCarrierPolicyAL();
                normalizePoliciesNL();
                updateNetworkEnabledNL();
                updateNetworkRulesNL();
                updateNotificationsNL();
            }
        }
    }

    @VisibleForTesting
    public void updateNetworks() throws InterruptedException {
        updateNetworksInternal();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.mHandler.post(new Runnable() { // from class: com.android.server.net.NetworkPolicyManagerService$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(5L, TimeUnit.SECONDS);
    }

    @VisibleForTesting
    public Handler getHandlerForTesting() {
        return this.mHandler;
    }

    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final boolean maybeUpdateCarrierPolicyCycleAL(int i, String str) {
        if (LOGV) {
            Slog.v("NetworkPolicy", "maybeUpdateCarrierPolicyCycleAL()");
        }
        boolean z = false;
        NetworkIdentity build = new NetworkIdentity.Builder().setType(0).setSubscriberId(str).setMetered(true).setDefaultNetwork(true).setSubId(i).build();
        for (int size = this.mNetworkPolicy.size() - 1; size >= 0; size--) {
            if (this.mNetworkPolicy.keyAt(size).matches(build)) {
                z = updateDefaultCarrierPolicyAL(i, this.mNetworkPolicy.valueAt(size)) | z;
            }
        }
        return z;
    }

    @VisibleForTesting
    public int getCycleDayFromCarrierConfig(PersistableBundle persistableBundle, int i) {
        int i2;
        if (persistableBundle == null || (i2 = persistableBundle.getInt("monthly_data_cycle_day_int")) == -1) {
            return i;
        }
        Calendar calendar = Calendar.getInstance();
        if (i2 < calendar.getMinimum(5) || i2 > calendar.getMaximum(5)) {
            Slog.e("NetworkPolicy", "Invalid date in CarrierConfigManager.KEY_MONTHLY_DATA_CYCLE_DAY_INT: " + i2);
            return i;
        }
        return i2;
    }

    @VisibleForTesting
    public long getWarningBytesFromCarrierConfig(PersistableBundle persistableBundle, long j) {
        if (persistableBundle == null) {
            return j;
        }
        long j2 = persistableBundle.getLong("data_warning_threshold_bytes_long");
        if (j2 == -2) {
            return -1L;
        }
        if (j2 == -1) {
            return getPlatformDefaultWarningBytes();
        }
        if (j2 < 0) {
            Slog.e("NetworkPolicy", "Invalid value in CarrierConfigManager.KEY_DATA_WARNING_THRESHOLD_BYTES_LONG; expected a non-negative value but got: " + j2);
            return j;
        }
        return j2;
    }

    @VisibleForTesting
    public long getLimitBytesFromCarrierConfig(PersistableBundle persistableBundle, long j) {
        if (persistableBundle == null) {
            return j;
        }
        long j2 = persistableBundle.getLong("data_limit_threshold_bytes_long");
        if (j2 == -2) {
            return -1L;
        }
        if (j2 == -1) {
            return getPlatformDefaultLimitBytes();
        }
        if (j2 < 0) {
            Slog.e("NetworkPolicy", "Invalid value in CarrierConfigManager.KEY_DATA_LIMIT_THRESHOLD_BYTES_LONG; expected a non-negative value but got: " + j2);
            return j;
        }
        return j2;
    }

    @GuardedBy({"mUidRulesFirstLock", "mNetworkPoliciesSecondLock"})
    public void handleNetworkPoliciesUpdateAL(boolean z) {
        if (z) {
            normalizePoliciesNL();
        }
        updateNetworkEnabledNL();
        updateNetworkRulesNL();
        updateNotificationsNL();
        writePolicyAL();
    }

    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public void updateNetworkEnabledNL() {
        if (LOGV) {
            Slog.v("NetworkPolicy", "updateNetworkEnabledNL()");
        }
        Trace.traceBegin(2097152L, "updateNetworkEnabledNL");
        long time = this.mStatLogger.getTime();
        int size = this.mNetworkPolicy.size() - 1;
        while (true) {
            boolean z = false;
            if (size >= 0) {
                NetworkPolicy valueAt = this.mNetworkPolicy.valueAt(size);
                if (valueAt.limitBytes == -1 || !valueAt.hasCycle()) {
                    setNetworkTemplateEnabled(valueAt.template, true);
                } else {
                    Pair pair = (Pair) NetworkPolicyManager.cycleIterator(valueAt).next();
                    long epochMilli = ((ZonedDateTime) pair.first).toInstant().toEpochMilli();
                    if (valueAt.isOverLimit(getTotalBytes(valueAt.template, epochMilli, ((ZonedDateTime) pair.second).toInstant().toEpochMilli())) && valueAt.lastLimitSnooze < epochMilli) {
                        z = true;
                    }
                    setNetworkTemplateEnabled(valueAt.template, !z);
                }
                size--;
            } else {
                this.mStatLogger.logDurationStat(0, time);
                Trace.traceEnd(2097152L);
                return;
            }
        }
    }

    public final void setNetworkTemplateEnabled(NetworkTemplate networkTemplate, boolean z) {
        this.mHandler.obtainMessage(18, z ? 1 : 0, 0, networkTemplate).sendToTarget();
    }

    public final void setNetworkTemplateEnabledInner(NetworkTemplate networkTemplate, boolean z) {
        int i;
        if (networkTemplate.getMatchRule() == 1 || networkTemplate.getMatchRule() == 10) {
            IntArray intArray = new IntArray();
            synchronized (this.mNetworkPoliciesSecondLock) {
                for (int i2 = 0; i2 < this.mSubIdToSubscriberId.size(); i2++) {
                    int keyAt = this.mSubIdToSubscriberId.keyAt(i2);
                    if (networkTemplate.matches(new NetworkIdentity.Builder().setType(0).setSubscriberId(this.mSubIdToSubscriberId.valueAt(i2)).setMetered(true).setDefaultNetwork(true).setSubId(keyAt).build())) {
                        intArray.add(keyAt);
                    }
                }
            }
            TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
            for (i = 0; i < intArray.size(); i++) {
                telephonyManager.createForSubscriptionId(intArray.get(i)).setPolicyDataEnabled(z);
            }
        }
    }

    public static void collectIfaces(ArraySet<String> arraySet, NetworkStateSnapshot networkStateSnapshot) {
        arraySet.addAll(networkStateSnapshot.getLinkProperties().getAllInterfaceNames());
    }

    public void updateSubscriptions() {
        if (LOGV) {
            Slog.v("NetworkPolicy", "updateSubscriptions()");
        }
        Trace.traceBegin(2097152L, "updateSubscriptions");
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
        List<SubscriptionInfo> emptyIfNull = CollectionUtils.emptyIfNull(((SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class)).getActiveSubscriptionInfoList());
        ArrayList arrayList = new ArrayList();
        SparseArray sparseArray = new SparseArray(emptyIfNull.size());
        SparseArray sparseArray2 = new SparseArray();
        for (SubscriptionInfo subscriptionInfo : emptyIfNull) {
            int subscriptionId = subscriptionInfo.getSubscriptionId();
            TelephonyManager createForSubscriptionId = telephonyManager.createForSubscriptionId(subscriptionId);
            String subscriberId = createForSubscriptionId.getSubscriberId();
            if (TextUtils.isEmpty(subscriberId)) {
                Slog.wtf("NetworkPolicy", "Missing subscriberId for subId " + createForSubscriptionId.getSubscriptionId());
            } else {
                sparseArray.put(createForSubscriptionId.getSubscriptionId(), subscriberId);
            }
            arrayList.add(ArrayUtils.defeatNullable(createForSubscriptionId.getMergedImsisFromGroup()));
            PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(subscriptionId);
            if (configForSubId != null) {
                sparseArray2.put(subscriptionId, configForSubId);
            } else {
                Slog.e("NetworkPolicy", "Missing CarrierConfig for subId " + subscriptionId);
            }
        }
        synchronized (this.mNetworkPoliciesSecondLock) {
            this.mSubIdToSubscriberId.clear();
            for (int i = 0; i < sparseArray.size(); i++) {
                this.mSubIdToSubscriberId.put(sparseArray.keyAt(i), (String) sparseArray.valueAt(i));
            }
            this.mMergedSubscriberIds = arrayList;
            this.mSubIdToCarrierConfig.clear();
            for (int i2 = 0; i2 < sparseArray2.size(); i2++) {
                this.mSubIdToCarrierConfig.put(sparseArray2.keyAt(i2), (PersistableBundle) sparseArray2.valueAt(i2));
            }
        }
        Trace.traceEnd(2097152L);
    }

    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public void updateNetworkRulesNL() {
        int i;
        String[] strArr;
        int subIdLocked;
        SubscriptionPlan primarySubscriptionPlanLocked;
        Instant instant;
        String subscriberId;
        long max;
        int i2;
        NetworkPolicy networkPolicy;
        long j;
        long j2;
        if (LOGV) {
            Slog.v("NetworkPolicy", "updateNetworkRulesNL()");
        }
        Trace.traceBegin(2097152L, "updateNetworkRulesNL");
        List<NetworkStateSnapshot> allNetworkStateSnapshots = this.mConnManager.getAllNetworkStateSnapshots();
        this.mNetIdToSubId.clear();
        ArrayMap arrayMap = new ArrayMap();
        Iterator it = allNetworkStateSnapshots.iterator();
        while (true) {
            i = 1;
            if (!it.hasNext()) {
                break;
            }
            NetworkStateSnapshot networkStateSnapshot = (NetworkStateSnapshot) it.next();
            this.mNetIdToSubId.put(networkStateSnapshot.getNetwork().getNetId(), networkStateSnapshot.getSubId());
            arrayMap.put(networkStateSnapshot, new NetworkIdentity.Builder().setNetworkStateSnapshot(networkStateSnapshot).setDefaultNetwork(true).build());
        }
        ArraySet<String> arraySet = new ArraySet<>();
        ArraySet arraySet2 = new ArraySet();
        int size = this.mNetworkPolicy.size() - 1;
        long j3 = Long.MAX_VALUE;
        while (true) {
            if (size < 0) {
                break;
            }
            NetworkPolicy valueAt = this.mNetworkPolicy.valueAt(size);
            arraySet2.clear();
            for (int size2 = arrayMap.size() - i; size2 >= 0; size2--) {
                if (valueAt.template.matches((NetworkIdentity) arrayMap.valueAt(size2))) {
                    collectIfaces(arraySet2, (NetworkStateSnapshot) arrayMap.keyAt(size2));
                }
            }
            if (LOGD) {
                Slog.d("NetworkPolicy", "Applying " + valueAt + " to ifaces " + arraySet2);
            }
            int i3 = valueAt.warningBytes != -1 ? i : 0;
            int i4 = valueAt.limitBytes != -1 ? i : 0;
            if (!(i4 == 0 && i3 == 0) && valueAt.hasCycle()) {
                Pair pair = (Pair) NetworkPolicyManager.cycleIterator(valueAt).next();
                long epochMilli = ((ZonedDateTime) pair.first).toInstant().toEpochMilli();
                i2 = size;
                networkPolicy = valueAt;
                long totalBytes = getTotalBytes(valueAt.template, epochMilli, ((ZonedDateTime) pair.second).toInstant().toEpochMilli());
                long max2 = (i4 == 0 || networkPolicy.lastLimitSnooze >= epochMilli) ? Long.MAX_VALUE : Math.max(1L, networkPolicy.limitBytes - totalBytes);
                if (i3 == 0 || networkPolicy.lastWarningSnooze >= epochMilli || networkPolicy.isOverWarning(totalBytes)) {
                    j = max2;
                    j2 = Long.MAX_VALUE;
                } else {
                    j2 = Math.max(1L, networkPolicy.warningBytes - totalBytes);
                    j = max2;
                }
            } else {
                i2 = size;
                networkPolicy = valueAt;
                j2 = Long.MAX_VALUE;
                j = Long.MAX_VALUE;
            }
            if (i3 != 0 || i4 != 0 || networkPolicy.metered) {
                if (arraySet2.size() > i) {
                    Slog.w("NetworkPolicy", "shared quota unsupported; generating rule for each iface");
                }
                for (int size3 = arraySet2.size() - i; size3 >= 0; size3--) {
                    String str = (String) arraySet2.valueAt(size3);
                    setInterfaceQuotasAsync(str, j2, j);
                    arraySet.add(str);
                }
            }
            if (i3 != 0) {
                long j4 = networkPolicy.warningBytes;
                if (j4 < j3) {
                    j3 = j4;
                }
            }
            if (i4 != 0) {
                long j5 = networkPolicy.limitBytes;
                if (j5 < j3) {
                    j3 = j5;
                }
            }
            size = i2 - 1;
            i = 1;
        }
        for (NetworkStateSnapshot networkStateSnapshot2 : allNetworkStateSnapshots) {
            if (!networkStateSnapshot2.getNetworkCapabilities().hasCapability(11)) {
                arraySet2.clear();
                collectIfaces(arraySet2, networkStateSnapshot2);
                for (int size4 = arraySet2.size() - 1; size4 >= 0; size4--) {
                    String str2 = (String) arraySet2.valueAt(size4);
                    if (!arraySet.contains(str2)) {
                        setInterfaceQuotasAsync(str2, Long.MAX_VALUE, Long.MAX_VALUE);
                        arraySet.add(str2);
                    }
                }
            }
        }
        synchronized (this.mMeteredIfacesLock) {
            for (int size5 = this.mMeteredIfaces.size() - 1; size5 >= 0; size5--) {
                String valueAt2 = this.mMeteredIfaces.valueAt(size5);
                if (!arraySet.contains(valueAt2)) {
                    removeInterfaceQuotasAsync(valueAt2);
                }
            }
            this.mMeteredIfaces = arraySet;
        }
        ContentResolver contentResolver = this.mContext.getContentResolver();
        boolean z = Settings.Global.getInt(contentResolver, "netpolicy_quota_enabled", 1) != 0;
        long j6 = Settings.Global.getLong(contentResolver, "netpolicy_quota_unlimited", QUOTA_UNLIMITED_DEFAULT);
        float f = Settings.Global.getFloat(contentResolver, "netpolicy_quota_limited", 0.1f);
        this.mSubscriptionOpportunisticQuota.clear();
        for (NetworkStateSnapshot networkStateSnapshot3 : allNetworkStateSnapshots) {
            if (z && networkStateSnapshot3.getNetwork() != null && (subIdLocked = getSubIdLocked(networkStateSnapshot3.getNetwork())) != -1 && (primarySubscriptionPlanLocked = getPrimarySubscriptionPlanLocked(subIdLocked)) != null) {
                long dataLimitBytes = primarySubscriptionPlanLocked.getDataLimitBytes();
                if (!networkStateSnapshot3.getNetworkCapabilities().hasCapability(18)) {
                    max = 0;
                } else if (dataLimitBytes == -1) {
                    max = -1;
                } else {
                    if (dataLimitBytes == Long.MAX_VALUE) {
                        max = j6;
                    } else {
                        Range<ZonedDateTime> next = primarySubscriptionPlanLocked.cycleIterator().next();
                        long epochMilli2 = next.getLower().toInstant().toEpochMilli();
                        long epochMilli3 = next.getUpper().toInstant().toEpochMilli();
                        long epochMilli4 = ZonedDateTime.ofInstant(this.mClock.instant(), next.getLower().getZone()).truncatedTo(ChronoUnit.DAYS).toInstant().toEpochMilli();
                        max = Math.max(0L, ((float) ((dataLimitBytes - (networkStateSnapshot3.getSubscriberId() == null ? 0L : getTotalBytes(buildTemplateCarrierMetered(subscriberId), epochMilli2, epochMilli4))) / ((((epochMilli3 - instant.toEpochMilli()) - 1) / TimeUnit.DAYS.toMillis(1L)) + 1))) * f);
                    }
                    this.mSubscriptionOpportunisticQuota.put(subIdLocked, max);
                }
                this.mSubscriptionOpportunisticQuota.put(subIdLocked, max);
            }
        }
        synchronized (this.mMeteredIfacesLock) {
            ArraySet<String> arraySet3 = this.mMeteredIfaces;
            strArr = (String[]) arraySet3.toArray(new String[arraySet3.size()]);
        }
        this.mHandler.obtainMessage(2, strArr).sendToTarget();
        this.mHandler.obtainMessage(7, Long.valueOf(j3)).sendToTarget();
        Trace.traceEnd(2097152L);
    }

    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final void ensureActiveCarrierPolicyAL() {
        if (LOGV) {
            Slog.v("NetworkPolicy", "ensureActiveCarrierPolicyAL()");
        }
        if (this.mSuppressDefaultPolicy) {
            return;
        }
        for (int i = 0; i < this.mSubIdToSubscriberId.size(); i++) {
            ensureActiveCarrierPolicyAL(this.mSubIdToSubscriberId.keyAt(i), this.mSubIdToSubscriberId.valueAt(i));
        }
    }

    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final boolean ensureActiveCarrierPolicyAL(int i, String str) {
        NetworkIdentity build = new NetworkIdentity.Builder().setType(0).setSubscriberId(str).setMetered(true).setDefaultNetwork(true).setSubId(i).build();
        for (int size = this.mNetworkPolicy.size() - 1; size >= 0; size--) {
            NetworkTemplate keyAt = this.mNetworkPolicy.keyAt(size);
            if (keyAt.matches(build)) {
                if (LOGD) {
                    Slog.d("NetworkPolicy", "Found template " + keyAt + " which matches subscriber " + NetworkIdentityUtils.scrubSubscriberId(str));
                }
                return false;
            }
        }
        Slog.i("NetworkPolicy", "No policy for subscriber " + NetworkIdentityUtils.scrubSubscriberId(str) + "; generating default policy");
        addNetworkPolicyAL(buildDefaultCarrierPolicy(i, str));
        return true;
    }

    public final long getPlatformDefaultWarningBytes() {
        long integer = this.mContext.getResources().getInteger(17694905);
        if (integer == -1) {
            return -1L;
        }
        return DataUnit.MEBIBYTES.toBytes(integer);
    }

    @VisibleForTesting
    public NetworkPolicy buildDefaultCarrierPolicy(int i, String str) {
        NetworkPolicy networkPolicy = new NetworkPolicy(buildTemplateCarrierMetered(str), NetworkPolicy.buildRule(ZonedDateTime.now().getDayOfMonth(), ZoneId.systemDefault()), getPlatformDefaultWarningBytes(), getPlatformDefaultLimitBytes(), -1L, -1L, true, true);
        synchronized (this.mUidRulesFirstLock) {
            synchronized (this.mNetworkPoliciesSecondLock) {
                updateDefaultCarrierPolicyAL(i, networkPolicy);
            }
        }
        return networkPolicy;
    }

    public static NetworkTemplate buildTemplateCarrierMetered(String str) {
        Objects.requireNonNull(str);
        return new NetworkTemplate.Builder(10).setSubscriberIds(Set.of(str)).setMeteredness(1).build();
    }

    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final boolean updateDefaultCarrierPolicyAL(int i, NetworkPolicy networkPolicy) {
        if (!networkPolicy.inferred) {
            if (LOGD) {
                Slog.d("NetworkPolicy", "Ignoring user-defined policy " + networkPolicy);
            }
            return false;
        }
        NetworkPolicy networkPolicy2 = new NetworkPolicy(networkPolicy.template, networkPolicy.cycleRule, networkPolicy.warningBytes, networkPolicy.limitBytes, networkPolicy.lastWarningSnooze, networkPolicy.lastLimitSnooze, networkPolicy.metered, networkPolicy.inferred);
        SubscriptionPlan[] subscriptionPlanArr = this.mSubscriptionPlans.get(i);
        if (!ArrayUtils.isEmpty(subscriptionPlanArr)) {
            SubscriptionPlan subscriptionPlan = subscriptionPlanArr[0];
            networkPolicy.cycleRule = subscriptionPlan.getCycleRule();
            long dataLimitBytes = subscriptionPlan.getDataLimitBytes();
            if (dataLimitBytes == -1) {
                networkPolicy.warningBytes = getPlatformDefaultWarningBytes();
                networkPolicy.limitBytes = getPlatformDefaultLimitBytes();
            } else if (dataLimitBytes == Long.MAX_VALUE) {
                networkPolicy.warningBytes = -1L;
                networkPolicy.limitBytes = -1L;
            } else {
                networkPolicy.warningBytes = (9 * dataLimitBytes) / 10;
                int dataLimitBehavior = subscriptionPlan.getDataLimitBehavior();
                if (dataLimitBehavior == 0 || dataLimitBehavior == 1) {
                    networkPolicy.limitBytes = dataLimitBytes;
                } else {
                    networkPolicy.limitBytes = -1L;
                }
            }
        } else {
            PersistableBundle persistableBundle = this.mSubIdToCarrierConfig.get(i);
            networkPolicy.cycleRule = NetworkPolicy.buildRule(getCycleDayFromCarrierConfig(persistableBundle, networkPolicy.cycleRule.isMonthly() ? networkPolicy.cycleRule.start.getDayOfMonth() : -1), ZoneId.systemDefault());
            networkPolicy.warningBytes = getWarningBytesFromCarrierConfig(persistableBundle, networkPolicy.warningBytes);
            networkPolicy.limitBytes = getLimitBytesFromCarrierConfig(persistableBundle, networkPolicy.limitBytes);
        }
        if (networkPolicy.equals(networkPolicy2)) {
            return false;
        }
        Slog.d("NetworkPolicy", "Updated " + networkPolicy2 + " to " + networkPolicy);
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:53:0x011e A[Catch: all -> 0x02c0, Exception -> 0x02c3, FileNotFoundException -> 0x02c6, TryCatch #4 {FileNotFoundException -> 0x02c6, Exception -> 0x02c3, all -> 0x02c0, blocks: (B:7:0x0029, B:8:0x0035, B:10:0x003b, B:13:0x0046, B:15:0x004f, B:17:0x0058, B:21:0x0064, B:22:0x006a, B:24:0x0072, B:26:0x0083, B:30:0x008f, B:37:0x00af, B:43:0x00f4, B:45:0x0106, B:53:0x011e, B:59:0x0131, B:63:0x013f, B:65:0x014a, B:67:0x0155, B:69:0x0162, B:70:0x0169, B:72:0x0173, B:49:0x0112, B:38:0x00d6, B:40:0x00df, B:42:0x00ea, B:32:0x00a0, B:73:0x0183, B:76:0x0198, B:78:0x01a6, B:79:0x01ab, B:80:0x01c1, B:82:0x01c9, B:84:0x01de, B:85:0x01e2, B:86:0x01f8, B:90:0x0201, B:93:0x020c, B:94:0x0215, B:97:0x0220, B:100:0x022f, B:104:0x023a, B:106:0x0241, B:108:0x0250, B:109:0x0272, B:111:0x0278, B:113:0x027e, B:114:0x029e, B:115:0x02a3), top: B:138:0x0029 }] */
    /* JADX WARN: Removed duplicated region for block: B:54:0x0127  */
    /* JADX WARN: Removed duplicated region for block: B:59:0x0131 A[Catch: all -> 0x02c0, Exception -> 0x02c3, FileNotFoundException -> 0x02c6, TryCatch #4 {FileNotFoundException -> 0x02c6, Exception -> 0x02c3, all -> 0x02c0, blocks: (B:7:0x0029, B:8:0x0035, B:10:0x003b, B:13:0x0046, B:15:0x004f, B:17:0x0058, B:21:0x0064, B:22:0x006a, B:24:0x0072, B:26:0x0083, B:30:0x008f, B:37:0x00af, B:43:0x00f4, B:45:0x0106, B:53:0x011e, B:59:0x0131, B:63:0x013f, B:65:0x014a, B:67:0x0155, B:69:0x0162, B:70:0x0169, B:72:0x0173, B:49:0x0112, B:38:0x00d6, B:40:0x00df, B:42:0x00ea, B:32:0x00a0, B:73:0x0183, B:76:0x0198, B:78:0x01a6, B:79:0x01ab, B:80:0x01c1, B:82:0x01c9, B:84:0x01de, B:85:0x01e2, B:86:0x01f8, B:90:0x0201, B:93:0x020c, B:94:0x0215, B:97:0x0220, B:100:0x022f, B:104:0x023a, B:106:0x0241, B:108:0x0250, B:109:0x0272, B:111:0x0278, B:113:0x027e, B:114:0x029e, B:115:0x02a3), top: B:138:0x0029 }] */
    /* JADX WARN: Removed duplicated region for block: B:60:0x013a  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x013f A[Catch: all -> 0x02c0, Exception -> 0x02c3, FileNotFoundException -> 0x02c6, TryCatch #4 {FileNotFoundException -> 0x02c6, Exception -> 0x02c3, all -> 0x02c0, blocks: (B:7:0x0029, B:8:0x0035, B:10:0x003b, B:13:0x0046, B:15:0x004f, B:17:0x0058, B:21:0x0064, B:22:0x006a, B:24:0x0072, B:26:0x0083, B:30:0x008f, B:37:0x00af, B:43:0x00f4, B:45:0x0106, B:53:0x011e, B:59:0x0131, B:63:0x013f, B:65:0x014a, B:67:0x0155, B:69:0x0162, B:70:0x0169, B:72:0x0173, B:49:0x0112, B:38:0x00d6, B:40:0x00df, B:42:0x00ea, B:32:0x00a0, B:73:0x0183, B:76:0x0198, B:78:0x01a6, B:79:0x01ab, B:80:0x01c1, B:82:0x01c9, B:84:0x01de, B:85:0x01e2, B:86:0x01f8, B:90:0x0201, B:93:0x020c, B:94:0x0215, B:97:0x0220, B:100:0x022f, B:104:0x023a, B:106:0x0241, B:108:0x0250, B:109:0x0272, B:111:0x0278, B:113:0x027e, B:114:0x029e, B:115:0x02a3), top: B:138:0x0029 }] */
    /* JADX WARN: Removed duplicated region for block: B:64:0x0148  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x0155 A[Catch: all -> 0x02c0, Exception -> 0x02c3, FileNotFoundException -> 0x02c6, TryCatch #4 {FileNotFoundException -> 0x02c6, Exception -> 0x02c3, all -> 0x02c0, blocks: (B:7:0x0029, B:8:0x0035, B:10:0x003b, B:13:0x0046, B:15:0x004f, B:17:0x0058, B:21:0x0064, B:22:0x006a, B:24:0x0072, B:26:0x0083, B:30:0x008f, B:37:0x00af, B:43:0x00f4, B:45:0x0106, B:53:0x011e, B:59:0x0131, B:63:0x013f, B:65:0x014a, B:67:0x0155, B:69:0x0162, B:70:0x0169, B:72:0x0173, B:49:0x0112, B:38:0x00d6, B:40:0x00df, B:42:0x00ea, B:32:0x00a0, B:73:0x0183, B:76:0x0198, B:78:0x01a6, B:79:0x01ab, B:80:0x01c1, B:82:0x01c9, B:84:0x01de, B:85:0x01e2, B:86:0x01f8, B:90:0x0201, B:93:0x020c, B:94:0x0215, B:97:0x0220, B:100:0x022f, B:104:0x023a, B:106:0x0241, B:108:0x0250, B:109:0x0272, B:111:0x0278, B:113:0x027e, B:114:0x029e, B:115:0x02a3), top: B:138:0x0029 }] */
    /* JADX WARN: Removed duplicated region for block: B:69:0x0162 A[Catch: all -> 0x02c0, Exception -> 0x02c3, FileNotFoundException -> 0x02c6, TryCatch #4 {FileNotFoundException -> 0x02c6, Exception -> 0x02c3, all -> 0x02c0, blocks: (B:7:0x0029, B:8:0x0035, B:10:0x003b, B:13:0x0046, B:15:0x004f, B:17:0x0058, B:21:0x0064, B:22:0x006a, B:24:0x0072, B:26:0x0083, B:30:0x008f, B:37:0x00af, B:43:0x00f4, B:45:0x0106, B:53:0x011e, B:59:0x0131, B:63:0x013f, B:65:0x014a, B:67:0x0155, B:69:0x0162, B:70:0x0169, B:72:0x0173, B:49:0x0112, B:38:0x00d6, B:40:0x00df, B:42:0x00ea, B:32:0x00a0, B:73:0x0183, B:76:0x0198, B:78:0x01a6, B:79:0x01ab, B:80:0x01c1, B:82:0x01c9, B:84:0x01de, B:85:0x01e2, B:86:0x01f8, B:90:0x0201, B:93:0x020c, B:94:0x0215, B:97:0x0220, B:100:0x022f, B:104:0x023a, B:106:0x0241, B:108:0x0250, B:109:0x0272, B:111:0x0278, B:113:0x027e, B:114:0x029e, B:115:0x02a3), top: B:138:0x0029 }] */
    /* JADX WARN: Removed duplicated region for block: B:72:0x0173 A[Catch: all -> 0x02c0, Exception -> 0x02c3, FileNotFoundException -> 0x02c6, TryCatch #4 {FileNotFoundException -> 0x02c6, Exception -> 0x02c3, all -> 0x02c0, blocks: (B:7:0x0029, B:8:0x0035, B:10:0x003b, B:13:0x0046, B:15:0x004f, B:17:0x0058, B:21:0x0064, B:22:0x006a, B:24:0x0072, B:26:0x0083, B:30:0x008f, B:37:0x00af, B:43:0x00f4, B:45:0x0106, B:53:0x011e, B:59:0x0131, B:63:0x013f, B:65:0x014a, B:67:0x0155, B:69:0x0162, B:70:0x0169, B:72:0x0173, B:49:0x0112, B:38:0x00d6, B:40:0x00df, B:42:0x00ea, B:32:0x00a0, B:73:0x0183, B:76:0x0198, B:78:0x01a6, B:79:0x01ab, B:80:0x01c1, B:82:0x01c9, B:84:0x01de, B:85:0x01e2, B:86:0x01f8, B:90:0x0201, B:93:0x020c, B:94:0x0215, B:97:0x0220, B:100:0x022f, B:104:0x023a, B:106:0x0241, B:108:0x0250, B:109:0x0272, B:111:0x0278, B:113:0x027e, B:114:0x029e, B:115:0x02a3), top: B:138:0x0029 }] */
    /* JADX WARN: Type inference failed for: r7v23 */
    @GuardedBy({"mUidRulesFirstLock", "mNetworkPoliciesSecondLock"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void readPolicyAL() {
        FileInputStream openRead;
        int i;
        String str;
        int i2;
        int i3;
        Object obj;
        String str2;
        RecurrenceRule buildRule;
        Object obj2;
        long j;
        long readLongAttribute;
        boolean z;
        NetworkTemplate build;
        if (LOGV) {
            Slog.v("NetworkPolicy", "readPolicyAL()");
        }
        this.mNetworkPolicy.clear();
        this.mSubscriptionPlans.clear();
        this.mSubscriptionPlansOwner.clear();
        this.mUidPolicy.clear();
        FileInputStream fileInputStream = null;
        String str3 = null;
        fileInputStream = null;
        fileInputStream = null;
        fileInputStream = null;
        try {
            try {
                openRead = this.mPolicyFile.openRead();
            } catch (Throwable th) {
                th = th;
            }
        } catch (FileNotFoundException unused) {
        } catch (Exception e) {
            e = e;
        } catch (Throwable th2) {
            th = th2;
        }
        try {
            TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(openRead);
            SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
            int i4 = 1;
            int i5 = 1;
            boolean z2 = false;
            while (true) {
                int next = resolvePullParser.next();
                if (next == i4) {
                    break;
                }
                String name = resolvePullParser.getName();
                if (next == 2) {
                    if ("policy-list".equals(name)) {
                        i5 = XmlUtils.readIntAttribute(resolvePullParser, "version");
                        this.mLoadedRestrictBackground = (i5 < 3 || !XmlUtils.readBooleanAttribute(resolvePullParser, "restrictBackground")) ? 0 : i4;
                        i = i4;
                        str = str3;
                    } else {
                        if ("network-policy".equals(name)) {
                            int readIntAttribute = XmlUtils.readIntAttribute(resolvePullParser, "networkTemplate");
                            String attributeValue = resolvePullParser.getAttributeValue(str3, "subscriberId");
                            String attributeValue2 = i5 >= 9 ? resolvePullParser.getAttributeValue(str3, "networkId") : str3;
                            if (i5 >= 13) {
                                i3 = XmlUtils.readIntAttribute(resolvePullParser, "subscriberIdMatchRule");
                                i2 = XmlUtils.readIntAttribute(resolvePullParser, "templateMetered");
                            } else {
                                if (readIntAttribute == i4) {
                                    Log.d("NetworkPolicy", "Update template match rule from mobile to carrier and force to metered");
                                    readIntAttribute = 10;
                                    i2 = i4;
                                } else {
                                    i2 = -1;
                                }
                                i3 = 0;
                            }
                            if (i5 >= 11) {
                                buildRule = new RecurrenceRule(RecurrenceRule.convertZonedDateTime(XmlUtils.readStringAttribute(resolvePullParser, "cycleStart")), RecurrenceRule.convertZonedDateTime(XmlUtils.readStringAttribute(resolvePullParser, "cycleEnd")), RecurrenceRule.convertPeriod(XmlUtils.readStringAttribute(resolvePullParser, "cyclePeriod")));
                                obj2 = null;
                            } else {
                                int readIntAttribute2 = XmlUtils.readIntAttribute(resolvePullParser, "cycleDay");
                                if (i5 >= 6) {
                                    obj = null;
                                    str2 = resolvePullParser.getAttributeValue((String) null, "cycleTimezone");
                                } else {
                                    obj = null;
                                    str2 = "UTC";
                                }
                                buildRule = NetworkPolicy.buildRule(readIntAttribute2, ZoneId.of(str2));
                                obj2 = obj;
                            }
                            long readLongAttribute2 = XmlUtils.readLongAttribute(resolvePullParser, "warningBytes");
                            long readLongAttribute3 = XmlUtils.readLongAttribute(resolvePullParser, "limitBytes");
                            if (i5 >= 5) {
                                readLongAttribute = XmlUtils.readLongAttribute(resolvePullParser, "lastLimitSnooze");
                            } else if (i5 >= 2) {
                                readLongAttribute = XmlUtils.readLongAttribute(resolvePullParser, "lastSnooze");
                            } else {
                                j = -1;
                                if (i5 < 4) {
                                    z = XmlUtils.readBooleanAttribute(resolvePullParser, "metered");
                                } else {
                                    z = readIntAttribute == 1;
                                }
                                long readLongAttribute4 = i5 < 5 ? XmlUtils.readLongAttribute(resolvePullParser, "lastWarningSnooze") : -1L;
                                boolean readBooleanAttribute = i5 < 7 ? XmlUtils.readBooleanAttribute(resolvePullParser, "inferred") : false;
                                NetworkTemplate.Builder meteredness = new NetworkTemplate.Builder(readIntAttribute).setMeteredness(i2);
                                if (i3 == 0) {
                                    ArraySet arraySet = new ArraySet();
                                    arraySet.add(attributeValue);
                                    meteredness.setSubscriberIds(arraySet);
                                }
                                if (attributeValue2 != null) {
                                    meteredness.setWifiNetworkKeys(Set.of(attributeValue2));
                                }
                                build = meteredness.build();
                                str = obj2;
                                if (NetworkPolicy.isTemplatePersistable(build)) {
                                    this.mNetworkPolicy.put(build, new NetworkPolicy(build, buildRule, readLongAttribute2, readLongAttribute3, readLongAttribute4, j, z, readBooleanAttribute));
                                    str = obj2;
                                }
                            }
                            j = readLongAttribute;
                            if (i5 < 4) {
                            }
                            if (i5 < 5) {
                            }
                            if (i5 < 7) {
                            }
                            NetworkTemplate.Builder meteredness2 = new NetworkTemplate.Builder(readIntAttribute).setMeteredness(i2);
                            if (i3 == 0) {
                            }
                            if (attributeValue2 != null) {
                            }
                            build = meteredness2.build();
                            str = obj2;
                            if (NetworkPolicy.isTemplatePersistable(build)) {
                            }
                        } else {
                            str = str3;
                            if ("uid-policy".equals(name)) {
                                int readIntAttribute3 = XmlUtils.readIntAttribute(resolvePullParser, "uid");
                                int readIntAttribute4 = XmlUtils.readIntAttribute(resolvePullParser, "policy");
                                if (UserHandle.isApp(readIntAttribute3)) {
                                    setUidPolicyUncheckedUL(readIntAttribute3, readIntAttribute4, false);
                                    str = str;
                                } else {
                                    Slog.w("NetworkPolicy", "unable to apply policy to UID " + readIntAttribute3 + "; ignoring");
                                    str = str;
                                }
                            } else if ("app-policy".equals(name)) {
                                int readIntAttribute5 = XmlUtils.readIntAttribute(resolvePullParser, "appId");
                                int readIntAttribute6 = XmlUtils.readIntAttribute(resolvePullParser, "policy");
                                int uid = UserHandle.getUid(0, readIntAttribute5);
                                if (UserHandle.isApp(uid)) {
                                    setUidPolicyUncheckedUL(uid, readIntAttribute6, false);
                                    str = str;
                                } else {
                                    Slog.w("NetworkPolicy", "unable to apply policy to UID " + uid + "; ignoring");
                                    str = str;
                                }
                            } else if ("whitelist".equals(name)) {
                                z2 = true;
                                str = str;
                            } else if ("restrict-background".equals(name) && z2) {
                                sparseBooleanArray.append(XmlUtils.readIntAttribute(resolvePullParser, "uid"), true);
                                str = str;
                            } else {
                                str = str;
                                str = str;
                                if ("revoked-restrict-background".equals(name) && z2) {
                                    i = 1;
                                    this.mRestrictBackgroundAllowlistRevokedUids.put(XmlUtils.readIntAttribute(resolvePullParser, "uid"), true);
                                }
                            }
                        }
                        i = 1;
                    }
                } else {
                    i = i4;
                    str = str3;
                    if (next == 3 && "whitelist".equals(name)) {
                        z2 = false;
                    }
                }
                str3 = str;
                i4 = i;
            }
            int size = sparseBooleanArray.size();
            for (int i6 = 0; i6 < size; i6++) {
                int keyAt = sparseBooleanArray.keyAt(i6);
                int i7 = this.mUidPolicy.get(keyAt, 0);
                if ((i7 & 1) != 0) {
                    Slog.w("NetworkPolicy", "ignoring restrict-background-allowlist for " + keyAt + " because its policy is " + NetworkPolicyManager.uidPoliciesToString(i7));
                } else if (UserHandle.isApp(keyAt)) {
                    int i8 = i7 | 4;
                    if (LOGV) {
                        Log.v("NetworkPolicy", "new policy for " + keyAt + ": " + NetworkPolicyManager.uidPoliciesToString(i8));
                    }
                    setUidPolicyUncheckedUL(keyAt, i8, false);
                } else {
                    Slog.w("NetworkPolicy", "unable to update policy on UID " + keyAt);
                }
            }
            IoUtils.closeQuietly(openRead);
        } catch (FileNotFoundException unused2) {
            fileInputStream = openRead;
            upgradeDefaultBackgroundDataUL();
            IoUtils.closeQuietly(fileInputStream);
        } catch (Exception e2) {
            e = e2;
            fileInputStream = openRead;
            Log.wtf("NetworkPolicy", "problem reading network policy", e);
            IoUtils.closeQuietly(fileInputStream);
        } catch (Throwable th3) {
            th = th3;
            fileInputStream = openRead;
            IoUtils.closeQuietly(fileInputStream);
            throw th;
        }
    }

    public final void upgradeDefaultBackgroundDataUL() {
        this.mLoadedRestrictBackground = Settings.Global.getInt(this.mContext.getContentResolver(), "default_restrict_background_data", 0) == 1;
    }

    public final void upgradeWifiMeteredOverride() {
        int i;
        ArrayMap arrayMap = new ArrayMap();
        synchronized (this.mNetworkPoliciesSecondLock) {
            int i2 = 0;
            while (i2 < this.mNetworkPolicy.size()) {
                NetworkPolicy valueAt = this.mNetworkPolicy.valueAt(i2);
                if (valueAt.template.getMatchRule() != 4 || valueAt.inferred) {
                    i2++;
                } else {
                    this.mNetworkPolicy.removeAt(i2);
                    Set wifiNetworkKeys = valueAt.template.getWifiNetworkKeys();
                    arrayMap.put(wifiNetworkKeys.isEmpty() ? null : (String) wifiNetworkKeys.iterator().next(), Boolean.valueOf(valueAt.metered));
                }
            }
        }
        if (arrayMap.isEmpty()) {
            return;
        }
        WifiManager wifiManager = (WifiManager) this.mContext.getSystemService(WifiManager.class);
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        for (i = 0; i < configuredNetworks.size(); i++) {
            WifiConfiguration wifiConfiguration = configuredNetworks.get(i);
            Iterator it = wifiConfiguration.getAllNetworkKeys().iterator();
            while (true) {
                if (it.hasNext()) {
                    String str = (String) it.next();
                    Boolean bool = (Boolean) arrayMap.get(str);
                    if (bool != null) {
                        Slog.d("NetworkPolicy", "Found network " + str + "; upgrading metered hint");
                        wifiConfiguration.meteredOverride = bool.booleanValue() ? 1 : 2;
                        wifiManager.updateNetwork(wifiConfiguration);
                    }
                }
            }
        }
        synchronized (this.mUidRulesFirstLock) {
            synchronized (this.mNetworkPoliciesSecondLock) {
                writePolicyAL();
            }
        }
    }

    @GuardedBy({"mUidRulesFirstLock", "mNetworkPoliciesSecondLock"})
    public void writePolicyAL() {
        if (LOGV) {
            Slog.v("NetworkPolicy", "writePolicyAL()");
        }
        FileOutputStream fileOutputStream = null;
        try {
            FileOutputStream startWrite = this.mPolicyFile.startWrite();
            try {
                TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(startWrite);
                resolveSerializer.startDocument((String) null, Boolean.TRUE);
                resolveSerializer.startTag((String) null, "policy-list");
                XmlUtils.writeIntAttribute(resolveSerializer, "version", 14);
                XmlUtils.writeBooleanAttribute(resolveSerializer, "restrictBackground", this.mRestrictBackground);
                for (int i = 0; i < this.mNetworkPolicy.size(); i++) {
                    NetworkPolicy valueAt = this.mNetworkPolicy.valueAt(i);
                    NetworkTemplate networkTemplate = valueAt.template;
                    if (NetworkPolicy.isTemplatePersistable(networkTemplate)) {
                        resolveSerializer.startTag((String) null, "network-policy");
                        XmlUtils.writeIntAttribute(resolveSerializer, "networkTemplate", networkTemplate.getMatchRule());
                        String str = networkTemplate.getSubscriberIds().isEmpty() ? null : (String) networkTemplate.getSubscriberIds().iterator().next();
                        if (str != null) {
                            resolveSerializer.attribute((String) null, "subscriberId", str);
                        }
                        XmlUtils.writeIntAttribute(resolveSerializer, "subscriberIdMatchRule", networkTemplate.getSubscriberIds().isEmpty() ? 1 : 0);
                        if (!networkTemplate.getWifiNetworkKeys().isEmpty()) {
                            resolveSerializer.attribute((String) null, "networkId", (String) networkTemplate.getWifiNetworkKeys().iterator().next());
                        }
                        XmlUtils.writeIntAttribute(resolveSerializer, "templateMetered", networkTemplate.getMeteredness());
                        XmlUtils.writeStringAttribute(resolveSerializer, "cycleStart", RecurrenceRule.convertZonedDateTime(valueAt.cycleRule.start));
                        XmlUtils.writeStringAttribute(resolveSerializer, "cycleEnd", RecurrenceRule.convertZonedDateTime(valueAt.cycleRule.end));
                        XmlUtils.writeStringAttribute(resolveSerializer, "cyclePeriod", RecurrenceRule.convertPeriod(valueAt.cycleRule.period));
                        XmlUtils.writeLongAttribute(resolveSerializer, "warningBytes", valueAt.warningBytes);
                        XmlUtils.writeLongAttribute(resolveSerializer, "limitBytes", valueAt.limitBytes);
                        XmlUtils.writeLongAttribute(resolveSerializer, "lastWarningSnooze", valueAt.lastWarningSnooze);
                        XmlUtils.writeLongAttribute(resolveSerializer, "lastLimitSnooze", valueAt.lastLimitSnooze);
                        XmlUtils.writeBooleanAttribute(resolveSerializer, "metered", valueAt.metered);
                        XmlUtils.writeBooleanAttribute(resolveSerializer, "inferred", valueAt.inferred);
                        resolveSerializer.endTag((String) null, "network-policy");
                    }
                }
                for (int i2 = 0; i2 < this.mUidPolicy.size(); i2++) {
                    int keyAt = this.mUidPolicy.keyAt(i2);
                    int valueAt2 = this.mUidPolicy.valueAt(i2);
                    if (valueAt2 != 0) {
                        resolveSerializer.startTag((String) null, "uid-policy");
                        XmlUtils.writeIntAttribute(resolveSerializer, "uid", keyAt);
                        XmlUtils.writeIntAttribute(resolveSerializer, "policy", valueAt2);
                        resolveSerializer.endTag((String) null, "uid-policy");
                    }
                }
                resolveSerializer.endTag((String) null, "policy-list");
                resolveSerializer.startTag((String) null, "whitelist");
                int size = this.mRestrictBackgroundAllowlistRevokedUids.size();
                for (int i3 = 0; i3 < size; i3++) {
                    int keyAt2 = this.mRestrictBackgroundAllowlistRevokedUids.keyAt(i3);
                    resolveSerializer.startTag((String) null, "revoked-restrict-background");
                    XmlUtils.writeIntAttribute(resolveSerializer, "uid", keyAt2);
                    resolveSerializer.endTag((String) null, "revoked-restrict-background");
                }
                resolveSerializer.endTag((String) null, "whitelist");
                resolveSerializer.endDocument();
                this.mPolicyFile.finishWrite(startWrite);
            } catch (IOException unused) {
                fileOutputStream = startWrite;
                if (fileOutputStream != null) {
                    this.mPolicyFile.failWrite(fileOutputStream);
                }
            }
        } catch (IOException unused2) {
        }
    }

    public void setUidPolicy(int i, int i2) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
        if (!UserHandle.isApp(i)) {
            throw new IllegalArgumentException("cannot apply policy to UID " + i);
        }
        synchronized (this.mUidRulesFirstLock) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            int i3 = this.mUidPolicy.get(i, 0);
            if (i3 != i2) {
                setUidPolicyUncheckedUL(i, i3, i2, true);
                this.mLogger.uidPolicyChanged(i, i3, i2);
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void addUidPolicy(int i, int i2) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
        if (!UserHandle.isApp(i)) {
            throw new IllegalArgumentException("cannot apply policy to UID " + i);
        }
        synchronized (this.mUidRulesFirstLock) {
            int i3 = this.mUidPolicy.get(i, 0);
            int i4 = i2 | i3;
            if (i3 != i4) {
                setUidPolicyUncheckedUL(i, i3, i4, true);
                this.mLogger.uidPolicyChanged(i, i3, i4);
            }
        }
    }

    public void removeUidPolicy(int i, int i2) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
        if (!UserHandle.isApp(i)) {
            throw new IllegalArgumentException("cannot apply policy to UID " + i);
        }
        synchronized (this.mUidRulesFirstLock) {
            int i3 = this.mUidPolicy.get(i, 0);
            int i4 = (~i2) & i3;
            if (i3 != i4) {
                setUidPolicyUncheckedUL(i, i3, i4, true);
                this.mLogger.uidPolicyChanged(i, i3, i4);
            }
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final void setUidPolicyUncheckedUL(int i, int i2, int i3, boolean z) {
        boolean z2 = false;
        setUidPolicyUncheckedUL(i, i3, false);
        if (isUidValidForAllowlistRulesUL(i)) {
            boolean z3 = i2 == 1;
            boolean z4 = i3 == 1;
            boolean z5 = i2 == 4;
            boolean z6 = i3 == 4;
            boolean z7 = z3 || (this.mRestrictBackground && !z5);
            boolean z8 = z4 || (this.mRestrictBackground && !z6);
            if (z5 && ((!z6 || z4) && this.mDefaultRestrictBackgroundAllowlistUids.get(i) && !this.mRestrictBackgroundAllowlistRevokedUids.get(i))) {
                if (LOGD) {
                    Slog.d("NetworkPolicy", "Adding uid " + i + " to revoked restrict background allowlist");
                }
                this.mRestrictBackgroundAllowlistRevokedUids.append(i, true);
            }
            if (z7 != z8) {
                z2 = true;
            }
        }
        this.mHandler.obtainMessage(13, i, i3, Boolean.valueOf(z2)).sendToTarget();
        if (z) {
            synchronized (this.mNetworkPoliciesSecondLock) {
                writePolicyAL();
            }
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final void setUidPolicyUncheckedUL(int i, int i2, boolean z) {
        if (i2 == 0) {
            this.mUidPolicy.delete(i);
        } else {
            this.mUidPolicy.put(i, i2);
        }
        lambda$updateRulesForRestrictBackgroundUL$6(i);
        if (z) {
            synchronized (this.mNetworkPoliciesSecondLock) {
                writePolicyAL();
            }
        }
    }

    public int getUidPolicy(int i) {
        int i2;
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
        synchronized (this.mUidRulesFirstLock) {
            i2 = this.mUidPolicy.get(i, 0);
        }
        return i2;
    }

    public int[] getUidsWithPolicy(int i) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
        int[] iArr = new int[0];
        synchronized (this.mUidRulesFirstLock) {
            for (int i2 = 0; i2 < this.mUidPolicy.size(); i2++) {
                int keyAt = this.mUidPolicy.keyAt(i2);
                int valueAt = this.mUidPolicy.valueAt(i2);
                if ((i == 0 && valueAt == 0) || (valueAt & i) != 0) {
                    iArr = ArrayUtils.appendInt(iArr, keyAt);
                }
            }
        }
        return iArr;
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public boolean removeUserStateUL(int i, boolean z, boolean z2) {
        this.mLogger.removingUserState(i);
        boolean z3 = false;
        for (int size = this.mRestrictBackgroundAllowlistRevokedUids.size() - 1; size >= 0; size--) {
            if (UserHandle.getUserId(this.mRestrictBackgroundAllowlistRevokedUids.keyAt(size)) == i) {
                this.mRestrictBackgroundAllowlistRevokedUids.removeAt(size);
                z3 = true;
            }
        }
        int[] iArr = new int[0];
        for (int i2 = 0; i2 < this.mUidPolicy.size(); i2++) {
            int keyAt = this.mUidPolicy.keyAt(i2);
            if (UserHandle.getUserId(keyAt) == i) {
                iArr = ArrayUtils.appendInt(iArr, keyAt);
            }
        }
        if (iArr.length > 0) {
            for (int i3 : iArr) {
                this.mUidPolicy.delete(i3);
            }
            z3 = true;
        }
        synchronized (this.mNetworkPoliciesSecondLock) {
            if (z2) {
                try {
                    updateRulesForGlobalChangeAL(true);
                } catch (Throwable th) {
                    throw th;
                }
            }
            if (z && z3) {
                writePolicyAL();
            }
        }
        return z3;
    }

    public final boolean checkAnyPermissionOf(String... strArr) {
        for (String str : strArr) {
            if (this.mContext.checkCallingOrSelfPermission(str) == 0) {
                return true;
            }
        }
        return false;
    }

    public final void enforceAnyPermissionOf(String... strArr) {
        if (checkAnyPermissionOf(strArr)) {
            return;
        }
        throw new SecurityException("Requires one of the following permissions: " + String.join(", ", strArr) + ".");
    }

    public void registerListener(INetworkPolicyListener iNetworkPolicyListener) {
        Objects.requireNonNull(iNetworkPolicyListener);
        enforceAnyPermissionOf("android.permission.CONNECTIVITY_INTERNAL", "android.permission.OBSERVE_NETWORK_POLICY");
        this.mListeners.register(iNetworkPolicyListener);
    }

    public void unregisterListener(INetworkPolicyListener iNetworkPolicyListener) {
        Objects.requireNonNull(iNetworkPolicyListener);
        enforceAnyPermissionOf("android.permission.CONNECTIVITY_INTERNAL", "android.permission.OBSERVE_NETWORK_POLICY");
        this.mListeners.unregister(iNetworkPolicyListener);
    }

    public void setNetworkPolicies(NetworkPolicy[] networkPolicyArr) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mUidRulesFirstLock) {
                synchronized (this.mNetworkPoliciesSecondLock) {
                    normalizePoliciesNL(networkPolicyArr);
                    handleNetworkPoliciesUpdateAL(false);
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void addNetworkPolicyAL(NetworkPolicy networkPolicy) {
        setNetworkPolicies((NetworkPolicy[]) ArrayUtils.appendElement(NetworkPolicy.class, getNetworkPolicies(this.mContext.getOpPackageName()), networkPolicy));
    }

    public NetworkPolicy[] getNetworkPolicies(String str) {
        NetworkPolicy[] networkPolicyArr;
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
        try {
            this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PRIVILEGED_PHONE_STATE", "NetworkPolicy");
        } catch (SecurityException unused) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.READ_PHONE_STATE", "NetworkPolicy");
            if (this.mAppOps.noteOp(51, Binder.getCallingUid(), str) != 0) {
                return new NetworkPolicy[0];
            }
        }
        synchronized (this.mNetworkPoliciesSecondLock) {
            int size = this.mNetworkPolicy.size();
            networkPolicyArr = new NetworkPolicy[size];
            for (int i = 0; i < size; i++) {
                networkPolicyArr[i] = this.mNetworkPolicy.valueAt(i);
            }
        }
        return networkPolicyArr;
    }

    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final void normalizePoliciesNL() {
        normalizePoliciesNL(getNetworkPolicies(this.mContext.getOpPackageName()));
    }

    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final void normalizePoliciesNL(NetworkPolicy[] networkPolicyArr) {
        this.mNetworkPolicy.clear();
        for (NetworkPolicy networkPolicy : networkPolicyArr) {
            if (networkPolicy != null) {
                NetworkTemplate normalizeTemplate = normalizeTemplate(networkPolicy.template, this.mMergedSubscriberIds);
                networkPolicy.template = normalizeTemplate;
                NetworkPolicy networkPolicy2 = this.mNetworkPolicy.get(normalizeTemplate);
                if (networkPolicy2 == null || networkPolicy2.compareTo(networkPolicy) > 0) {
                    if (networkPolicy2 != null) {
                        Slog.d("NetworkPolicy", "Normalization replaced " + networkPolicy2 + " with " + networkPolicy);
                    }
                    this.mNetworkPolicy.put(networkPolicy.template, networkPolicy);
                }
            }
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PRIVATE)
    public static NetworkTemplate normalizeTemplate(NetworkTemplate networkTemplate, List<String[]> list) {
        if (networkTemplate.getSubscriberIds().isEmpty()) {
            return networkTemplate;
        }
        for (String[] strArr : list) {
            ArraySet arraySet = new ArraySet(strArr);
            if (arraySet.size() != strArr.length) {
                Log.wtf("NetworkPolicy", "Duplicated merged list detected: " + Arrays.toString(strArr));
            }
            for (String str : networkTemplate.getSubscriberIds()) {
                if (com.android.net.module.util.CollectionUtils.contains(strArr, str)) {
                    return new NetworkTemplate.Builder(networkTemplate.getMatchRule()).setWifiNetworkKeys(networkTemplate.getWifiNetworkKeys()).setSubscriberIds(arraySet).setMeteredness(networkTemplate.getMeteredness()).build();
                }
            }
        }
        return networkTemplate;
    }

    public void snoozeLimit(NetworkTemplate networkTemplate) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            performSnooze(networkTemplate, 35);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void performSnooze(NetworkTemplate networkTemplate, int i) {
        long millis = this.mClock.millis();
        synchronized (this.mUidRulesFirstLock) {
            synchronized (this.mNetworkPoliciesSecondLock) {
                NetworkPolicy networkPolicy = this.mNetworkPolicy.get(networkTemplate);
                if (networkPolicy == null) {
                    throw new IllegalArgumentException("unable to find policy for " + networkTemplate);
                }
                if (i == 34) {
                    networkPolicy.lastWarningSnooze = millis;
                } else if (i == 35) {
                    networkPolicy.lastLimitSnooze = millis;
                } else if (i == 45) {
                    networkPolicy.lastRapidSnooze = millis;
                } else {
                    throw new IllegalArgumentException("unexpected type");
                }
                handleNetworkPoliciesUpdateAL(true);
            }
        }
    }

    public void setRestrictBackground(boolean z) {
        Trace.traceBegin(2097152L, "setRestrictBackground");
        try {
            this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
            int callingUid = Binder.getCallingUid();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            synchronized (this.mUidRulesFirstLock) {
                setRestrictBackgroundUL(z, "uid:" + callingUid);
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
        } finally {
            Trace.traceEnd(2097152L);
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final void setRestrictBackgroundUL(boolean z, String str) {
        Trace.traceBegin(2097152L, "setRestrictBackgroundUL");
        try {
            if (z == this.mRestrictBackground) {
                Slog.w("NetworkPolicy", "setRestrictBackgroundUL: already " + z);
                return;
            }
            Slog.d("NetworkPolicy", "setRestrictBackgroundUL(): " + z + "; reason: " + str);
            boolean z2 = this.mRestrictBackground;
            this.mRestrictBackground = z;
            updateRulesForRestrictBackgroundUL();
            try {
                if (!this.mNetworkManager.setDataSaverModeEnabled(this.mRestrictBackground)) {
                    Slog.e("NetworkPolicy", "Could not change Data Saver Mode on NMS to " + this.mRestrictBackground);
                    this.mRestrictBackground = z2;
                    return;
                }
            } catch (RemoteException unused) {
            }
            sendRestrictBackgroundChangedMsg();
            this.mLogger.restrictBackgroundChanged(z2, this.mRestrictBackground);
            if (this.mRestrictBackgroundLowPowerMode) {
                this.mRestrictBackgroundChangedInBsm = true;
            }
            synchronized (this.mNetworkPoliciesSecondLock) {
                updateNotificationsNL();
                writePolicyAL();
            }
        } finally {
            Trace.traceEnd(2097152L);
        }
    }

    public final void sendRestrictBackgroundChangedMsg() {
        this.mHandler.removeMessages(6);
        this.mHandler.obtainMessage(6, this.mRestrictBackground ? 1 : 0, 0).sendToTarget();
    }

    public int getRestrictBackgroundByCaller() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE", "NetworkPolicy");
        return getRestrictBackgroundStatusInternal(Binder.getCallingUid());
    }

    public int getRestrictBackgroundStatus(int i) {
        PermissionUtils.enforceNetworkStackPermission(this.mContext);
        return getRestrictBackgroundStatusInternal(i);
    }

    public final int getRestrictBackgroundStatusInternal(int i) {
        synchronized (this.mUidRulesFirstLock) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            int uidPolicy = getUidPolicy(i);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            if (uidPolicy == 1) {
                return 3;
            }
            if (this.mRestrictBackground) {
                return (this.mUidPolicy.get(i) & 4) != 0 ? 2 : 3;
            }
            return 1;
        }
    }

    public boolean getRestrictBackground() {
        boolean z;
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
        synchronized (this.mUidRulesFirstLock) {
            z = this.mRestrictBackground;
        }
        return z;
    }

    public void setDeviceIdleMode(boolean z) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
        Trace.traceBegin(2097152L, "setDeviceIdleMode");
        try {
            synchronized (this.mUidRulesFirstLock) {
                if (this.mDeviceIdleMode == z) {
                    return;
                }
                this.mDeviceIdleMode = z;
                this.mLogger.deviceIdleModeEnabled(z);
                if (this.mSystemReady) {
                    handleDeviceIdleModeChangedUL(z);
                }
                if (z) {
                    EventLogTags.writeDeviceIdleOnPhase("net");
                } else {
                    EventLogTags.writeDeviceIdleOffPhase("net");
                }
            }
        } finally {
            Trace.traceEnd(2097152L);
        }
    }

    public void setWifiMeteredOverride(String str, int i) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            WifiManager wifiManager = (WifiManager) this.mContext.getSystemService(WifiManager.class);
            for (WifiConfiguration wifiConfiguration : wifiManager.getConfiguredNetworks()) {
                if (Objects.equals(NetworkPolicyManager.resolveNetworkId(wifiConfiguration), str)) {
                    wifiConfiguration.meteredOverride = i;
                    wifiManager.updateNetwork(wifiConfiguration);
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void enforceSubscriptionPlanAccess(int i, int i2, String str) {
        this.mAppOps.checkPackage(i2, str);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(i);
            TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
            if (telephonyManager == null || !telephonyManager.hasCarrierPrivileges(i)) {
                if (configForSubId != null) {
                    String string = configForSubId.getString("config_plans_package_override_string", null);
                    if (!TextUtils.isEmpty(string) && Objects.equals(string, str)) {
                        return;
                    }
                }
                String defaultCarrierServicePackageName = this.mCarrierConfigManager.getDefaultCarrierServicePackageName();
                if (TextUtils.isEmpty(defaultCarrierServicePackageName) || !Objects.equals(defaultCarrierServicePackageName, str)) {
                    String str2 = SystemProperties.get("persist.sys.sub_plan_owner." + i, (String) null);
                    if (TextUtils.isEmpty(str2) || !Objects.equals(str2, str)) {
                        String str3 = SystemProperties.get("fw.sub_plan_owner." + i, (String) null);
                        if (TextUtils.isEmpty(str3) || !Objects.equals(str3, str)) {
                            this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_SUBSCRIPTION_PLANS", "NetworkPolicy");
                        }
                    }
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void enforceSubscriptionPlanValidity(SubscriptionPlan[] subscriptionPlanArr) {
        if (subscriptionPlanArr.length == 0) {
            Log.d("NetworkPolicy", "Received empty plans list. Clearing existing SubscriptionPlans.");
            return;
        }
        int[] allNetworkTypes = TelephonyManager.getAllNetworkTypes();
        ArraySet arraySet = new ArraySet();
        addAll(arraySet, allNetworkTypes);
        ArraySet arraySet2 = new ArraySet();
        boolean z = false;
        for (SubscriptionPlan subscriptionPlan : subscriptionPlanArr) {
            int[] networkTypes = subscriptionPlan.getNetworkTypes();
            ArraySet arraySet3 = new ArraySet();
            for (int i = 0; i < networkTypes.length; i++) {
                if (arraySet.contains(Integer.valueOf(networkTypes[i]))) {
                    if (!arraySet3.add(Integer.valueOf(networkTypes[i]))) {
                        throw new IllegalArgumentException("Subscription plan contains duplicate network types.");
                    }
                } else {
                    throw new IllegalArgumentException("Invalid network type: " + networkTypes[i]);
                }
            }
            if (networkTypes.length == allNetworkTypes.length) {
                z = true;
            } else if (!addAll(arraySet2, networkTypes)) {
                throw new IllegalArgumentException("Multiple subscription plans defined for a single network type.");
            }
        }
        if (!z) {
            throw new IllegalArgumentException("No generic subscription plan that applies to all network types.");
        }
    }

    public static boolean addAll(ArraySet<Integer> arraySet, int... iArr) {
        boolean z = true;
        for (int i : iArr) {
            z &= arraySet.add(Integer.valueOf(i));
        }
        return z;
    }

    public SubscriptionPlan getSubscriptionPlan(NetworkTemplate networkTemplate) {
        SubscriptionPlan primarySubscriptionPlanLocked;
        enforceAnyPermissionOf("android.permission.MAINLINE_NETWORK_STACK");
        synchronized (this.mNetworkPoliciesSecondLock) {
            primarySubscriptionPlanLocked = getPrimarySubscriptionPlanLocked(findRelevantSubIdNL(networkTemplate));
        }
        return primarySubscriptionPlanLocked;
    }

    public void notifyStatsProviderWarningOrLimitReached() {
        enforceAnyPermissionOf("android.permission.MAINLINE_NETWORK_STACK");
        synchronized (this.mNetworkPoliciesSecondLock) {
            if (this.mSystemReady) {
                this.mHandler.obtainMessage(20).sendToTarget();
            }
        }
    }

    public SubscriptionPlan[] getSubscriptionPlans(int i, String str) {
        enforceSubscriptionPlanAccess(i, Binder.getCallingUid(), str);
        String str2 = SystemProperties.get("fw.fake_plan");
        if (!TextUtils.isEmpty(str2)) {
            ArrayList arrayList = new ArrayList();
            if ("month_hard".equals(str2)) {
                arrayList.add(SubscriptionPlan.Builder.createRecurringMonthly(ZonedDateTime.parse("2007-03-14T00:00:00.000Z")).setTitle("G-Mobile").setDataLimit(DataUnit.GIBIBYTES.toBytes(5L), 1).setDataUsage(DataUnit.GIBIBYTES.toBytes(1L), ZonedDateTime.now().minusHours(36L).toInstant().toEpochMilli()).build());
                arrayList.add(SubscriptionPlan.Builder.createRecurringMonthly(ZonedDateTime.parse("2017-03-14T00:00:00.000Z")).setTitle("G-Mobile Happy").setDataLimit(Long.MAX_VALUE, 1).setDataUsage(DataUnit.GIBIBYTES.toBytes(5L), ZonedDateTime.now().minusHours(36L).toInstant().toEpochMilli()).build());
                arrayList.add(SubscriptionPlan.Builder.createRecurringMonthly(ZonedDateTime.parse("2017-03-14T00:00:00.000Z")).setTitle("G-Mobile, Charged after limit").setDataLimit(DataUnit.GIBIBYTES.toBytes(5L), 1).setDataUsage(DataUnit.GIBIBYTES.toBytes(5L), ZonedDateTime.now().minusHours(36L).toInstant().toEpochMilli()).build());
            } else if ("month_soft".equals(str2)) {
                arrayList.add(SubscriptionPlan.Builder.createRecurringMonthly(ZonedDateTime.parse("2007-03-14T00:00:00.000Z")).setTitle("G-Mobile is the carriers name who this plan belongs to").setSummary("Crazy unlimited bandwidth plan with incredibly long title that should be cut off to prevent UI from looking terrible").setDataLimit(DataUnit.GIBIBYTES.toBytes(5L), 2).setDataUsage(DataUnit.GIBIBYTES.toBytes(1L), ZonedDateTime.now().minusHours(1L).toInstant().toEpochMilli()).build());
                arrayList.add(SubscriptionPlan.Builder.createRecurringMonthly(ZonedDateTime.parse("2017-03-14T00:00:00.000Z")).setTitle("G-Mobile, Throttled after limit").setDataLimit(DataUnit.GIBIBYTES.toBytes(5L), 2).setDataUsage(DataUnit.GIBIBYTES.toBytes(5L), ZonedDateTime.now().minusHours(1L).toInstant().toEpochMilli()).build());
                arrayList.add(SubscriptionPlan.Builder.createRecurringMonthly(ZonedDateTime.parse("2017-03-14T00:00:00.000Z")).setTitle("G-Mobile, No data connection after limit").setDataLimit(DataUnit.GIBIBYTES.toBytes(5L), 0).setDataUsage(DataUnit.GIBIBYTES.toBytes(5L), ZonedDateTime.now().minusHours(1L).toInstant().toEpochMilli()).build());
            } else if ("month_over".equals(str2)) {
                arrayList.add(SubscriptionPlan.Builder.createRecurringMonthly(ZonedDateTime.parse("2007-03-14T00:00:00.000Z")).setTitle("G-Mobile is the carriers name who this plan belongs to").setDataLimit(DataUnit.GIBIBYTES.toBytes(5L), 2).setDataUsage(DataUnit.GIBIBYTES.toBytes(6L), ZonedDateTime.now().minusHours(1L).toInstant().toEpochMilli()).build());
                arrayList.add(SubscriptionPlan.Builder.createRecurringMonthly(ZonedDateTime.parse("2017-03-14T00:00:00.000Z")).setTitle("G-Mobile, Throttled after limit").setDataLimit(DataUnit.GIBIBYTES.toBytes(5L), 2).setDataUsage(DataUnit.GIBIBYTES.toBytes(5L), ZonedDateTime.now().minusHours(1L).toInstant().toEpochMilli()).build());
                arrayList.add(SubscriptionPlan.Builder.createRecurringMonthly(ZonedDateTime.parse("2017-03-14T00:00:00.000Z")).setTitle("G-Mobile, No data connection after limit").setDataLimit(DataUnit.GIBIBYTES.toBytes(5L), 0).setDataUsage(DataUnit.GIBIBYTES.toBytes(5L), ZonedDateTime.now().minusHours(1L).toInstant().toEpochMilli()).build());
            } else if ("month_none".equals(str2)) {
                arrayList.add(SubscriptionPlan.Builder.createRecurringMonthly(ZonedDateTime.parse("2007-03-14T00:00:00.000Z")).setTitle("G-Mobile").build());
            } else if ("prepaid".equals(str2)) {
                arrayList.add(SubscriptionPlan.Builder.createNonrecurring(ZonedDateTime.now().minusDays(20L), ZonedDateTime.now().plusDays(10L)).setTitle("G-Mobile").setDataLimit(DataUnit.MEBIBYTES.toBytes(512L), 0).setDataUsage(DataUnit.MEBIBYTES.toBytes(100L), ZonedDateTime.now().minusHours(3L).toInstant().toEpochMilli()).build());
            } else if ("prepaid_crazy".equals(str2)) {
                arrayList.add(SubscriptionPlan.Builder.createNonrecurring(ZonedDateTime.now().minusDays(20L), ZonedDateTime.now().plusDays(10L)).setTitle("G-Mobile Anytime").setDataLimit(DataUnit.MEBIBYTES.toBytes(512L), 0).setDataUsage(DataUnit.MEBIBYTES.toBytes(100L), ZonedDateTime.now().minusHours(3L).toInstant().toEpochMilli()).build());
                arrayList.add(SubscriptionPlan.Builder.createNonrecurring(ZonedDateTime.now().minusDays(10L), ZonedDateTime.now().plusDays(20L)).setTitle("G-Mobile Nickel Nights").setSummary("5¢/GB between 1-5AM").setDataLimit(DataUnit.GIBIBYTES.toBytes(5L), 2).setDataUsage(DataUnit.MEBIBYTES.toBytes(15L), ZonedDateTime.now().minusHours(30L).toInstant().toEpochMilli()).build());
                arrayList.add(SubscriptionPlan.Builder.createNonrecurring(ZonedDateTime.now().minusDays(10L), ZonedDateTime.now().plusDays(20L)).setTitle("G-Mobile Bonus 3G").setSummary("Unlimited 3G data").setDataLimit(DataUnit.GIBIBYTES.toBytes(1L), 2).setDataUsage(DataUnit.MEBIBYTES.toBytes(300L), ZonedDateTime.now().minusHours(1L).toInstant().toEpochMilli()).build());
            } else if ("unlimited".equals(str2)) {
                arrayList.add(SubscriptionPlan.Builder.createNonrecurring(ZonedDateTime.now().minusDays(20L), ZonedDateTime.now().plusDays(10L)).setTitle("G-Mobile Awesome").setDataLimit(Long.MAX_VALUE, 2).setDataUsage(DataUnit.MEBIBYTES.toBytes(50L), ZonedDateTime.now().minusHours(3L).toInstant().toEpochMilli()).build());
            }
            return (SubscriptionPlan[]) arrayList.toArray(new SubscriptionPlan[arrayList.size()]);
        }
        synchronized (this.mNetworkPoliciesSecondLock) {
            String str3 = this.mSubscriptionPlansOwner.get(i);
            if (!Objects.equals(str3, str) && UserHandle.getCallingAppId() != 1000 && UserHandle.getCallingAppId() != 1001) {
                Log.w("NetworkPolicy", "Not returning plans because caller " + str + " doesn't match owner " + str3);
                return null;
            }
            return this.mSubscriptionPlans.get(i);
        }
    }

    public void setSubscriptionPlans(int i, SubscriptionPlan[] subscriptionPlanArr, long j, String str) {
        enforceSubscriptionPlanAccess(i, Binder.getCallingUid(), str);
        enforceSubscriptionPlanValidity(subscriptionPlanArr);
        for (SubscriptionPlan subscriptionPlan : subscriptionPlanArr) {
            Objects.requireNonNull(subscriptionPlan);
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            setSubscriptionPlansInternal(i, subscriptionPlanArr, j, str);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void setSubscriptionPlansInternal(int i, SubscriptionPlan[] subscriptionPlanArr, long j, String str) {
        synchronized (this.mUidRulesFirstLock) {
            synchronized (this.mNetworkPoliciesSecondLock) {
                this.mSubscriptionPlans.put(i, subscriptionPlanArr);
                this.mSubscriptionPlansOwner.put(i, str);
                String str2 = this.mSubIdToSubscriberId.get(i, null);
                if (str2 != null) {
                    ensureActiveCarrierPolicyAL(i, str2);
                    maybeUpdateCarrierPolicyCycleAL(i, str2);
                } else {
                    Slog.wtf("NetworkPolicy", "Missing subscriberId for subId " + i);
                }
                handleNetworkPoliciesUpdateAL(true);
                Intent intent = new Intent("android.telephony.action.SUBSCRIPTION_PLANS_CHANGED");
                intent.addFlags(1073741824);
                intent.putExtra("android.telephony.extra.SUBSCRIPTION_INDEX", i);
                this.mContext.sendBroadcast(intent, "android.permission.MANAGE_SUBSCRIPTION_PLANS");
                Handler handler = this.mHandler;
                handler.sendMessage(handler.obtainMessage(19, i, 0, subscriptionPlanArr));
                int i2 = this.mSetSubscriptionPlansIdCounter;
                this.mSetSubscriptionPlansIdCounter = i2 + 1;
                this.mSetSubscriptionPlansIds.put(i, i2);
                if (j > 0) {
                    Handler handler2 = this.mHandler;
                    handler2.sendMessageDelayed(handler2.obtainMessage(22, i, i2, str), j);
                }
            }
        }
    }

    public void setSubscriptionPlansOwner(int i, String str) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.NETWORK_SETTINGS", "NetworkPolicy");
        SystemProperties.set("persist.sys.sub_plan_owner." + i, str);
    }

    public String getSubscriptionPlansOwner(int i) {
        String str;
        if (UserHandle.getCallingAppId() != 1000) {
            throw new SecurityException();
        }
        synchronized (this.mNetworkPoliciesSecondLock) {
            str = this.mSubscriptionPlansOwner.get(i);
        }
        return str;
    }

    public void setSubscriptionOverride(int i, int i2, int i3, int[] iArr, long j, String str) {
        enforceSubscriptionPlanAccess(i, Binder.getCallingUid(), str);
        ArraySet arraySet = new ArraySet();
        addAll(arraySet, TelephonyManager.getAllNetworkTypes());
        IntArray intArray = new IntArray();
        for (int i4 : iArr) {
            if (arraySet.contains(Integer.valueOf(i4))) {
                intArray.add(i4);
            } else {
                Log.d("NetworkPolicy", "setSubscriptionOverride removing invalid network type: " + i4);
            }
        }
        synchronized (this.mNetworkPoliciesSecondLock) {
            SubscriptionPlan primarySubscriptionPlanLocked = getPrimarySubscriptionPlanLocked(i);
            if ((i2 != 1 && primarySubscriptionPlanLocked == null) || primarySubscriptionPlanLocked.getDataLimitBehavior() == -1) {
                throw new IllegalStateException("Must provide valid SubscriptionPlan to enable overriding");
            }
        }
        if ((Settings.Global.getInt(this.mContext.getContentResolver(), "netpolicy_override_enabled", 1) != 0) || i3 == 0) {
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = Integer.valueOf(i);
            obtain.arg2 = Integer.valueOf(i2);
            obtain.arg3 = Integer.valueOf(i3);
            obtain.arg4 = intArray.toArray();
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(16, obtain));
            if (j > 0) {
                obtain.arg3 = 0;
                Handler handler2 = this.mHandler;
                handler2.sendMessageDelayed(handler2.obtainMessage(16, obtain), j);
            }
        }
    }

    public int getMultipathPreference(Network network) {
        PermissionUtils.enforceNetworkStackPermission(this.mContext);
        Integer multipathPreference = this.mMultipathPolicyTracker.getMultipathPreference(network);
        if (multipathPreference != null) {
            return multipathPreference.intValue();
        }
        return 0;
    }

    @NeverCompile
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        int keyAt;
        if (DumpUtils.checkDumpPermission(this.mContext, "NetworkPolicy", printWriter)) {
            IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
            ArraySet arraySet = new ArraySet(strArr.length);
            for (String str : strArr) {
                arraySet.add(str);
            }
            synchronized (this.mUidRulesFirstLock) {
                synchronized (this.mNetworkPoliciesSecondLock) {
                    if (arraySet.contains("--unsnooze")) {
                        for (int size = this.mNetworkPolicy.size() - 1; size >= 0; size--) {
                            this.mNetworkPolicy.valueAt(size).clearSnooze();
                        }
                        handleNetworkPoliciesUpdateAL(true);
                        indentingPrintWriter.println("Cleared snooze timestamps");
                        return;
                    }
                    indentingPrintWriter.print("System ready: ");
                    indentingPrintWriter.println(this.mSystemReady);
                    indentingPrintWriter.print("Restrict background: ");
                    indentingPrintWriter.println(this.mRestrictBackground);
                    indentingPrintWriter.print("Restrict power: ");
                    indentingPrintWriter.println(this.mRestrictPower);
                    indentingPrintWriter.print("Device idle: ");
                    indentingPrintWriter.println(this.mDeviceIdleMode);
                    indentingPrintWriter.print("Restricted networking mode: ");
                    indentingPrintWriter.println(this.mRestrictedNetworkingMode);
                    indentingPrintWriter.print("Low Power Standby mode: ");
                    indentingPrintWriter.println(this.mLowPowerStandbyActive);
                    synchronized (this.mMeteredIfacesLock) {
                        indentingPrintWriter.print("Metered ifaces: ");
                        indentingPrintWriter.println(this.mMeteredIfaces);
                    }
                    indentingPrintWriter.println();
                    indentingPrintWriter.println("mRestrictBackgroundLowPowerMode: " + this.mRestrictBackgroundLowPowerMode);
                    indentingPrintWriter.println("mRestrictBackgroundBeforeBsm: " + this.mRestrictBackgroundBeforeBsm);
                    indentingPrintWriter.println("mLoadedRestrictBackground: " + this.mLoadedRestrictBackground);
                    indentingPrintWriter.println("mRestrictBackgroundChangedInBsm: " + this.mRestrictBackgroundChangedInBsm);
                    indentingPrintWriter.println();
                    indentingPrintWriter.println("Network policies:");
                    indentingPrintWriter.increaseIndent();
                    for (int i = 0; i < this.mNetworkPolicy.size(); i++) {
                        indentingPrintWriter.println(this.mNetworkPolicy.valueAt(i).toString());
                    }
                    indentingPrintWriter.decreaseIndent();
                    indentingPrintWriter.println();
                    indentingPrintWriter.println("Subscription plans:");
                    indentingPrintWriter.increaseIndent();
                    for (int i2 = 0; i2 < this.mSubscriptionPlans.size(); i2++) {
                        indentingPrintWriter.println("Subscriber ID " + this.mSubscriptionPlans.keyAt(i2) + com.android.internal.util.jobs.XmlUtils.STRING_ARRAY_SEPARATOR);
                        indentingPrintWriter.increaseIndent();
                        SubscriptionPlan[] valueAt = this.mSubscriptionPlans.valueAt(i2);
                        if (!ArrayUtils.isEmpty(valueAt)) {
                            for (SubscriptionPlan subscriptionPlan : valueAt) {
                                indentingPrintWriter.println(subscriptionPlan);
                            }
                        }
                        indentingPrintWriter.decreaseIndent();
                    }
                    indentingPrintWriter.decreaseIndent();
                    indentingPrintWriter.println();
                    indentingPrintWriter.println("Active subscriptions:");
                    indentingPrintWriter.increaseIndent();
                    for (int i3 = 0; i3 < this.mSubIdToSubscriberId.size(); i3++) {
                        indentingPrintWriter.println(this.mSubIdToSubscriberId.keyAt(i3) + "=" + NetworkIdentityUtils.scrubSubscriberId(this.mSubIdToSubscriberId.valueAt(i3)));
                    }
                    indentingPrintWriter.decreaseIndent();
                    indentingPrintWriter.println();
                    Iterator<String[]> it = this.mMergedSubscriberIds.iterator();
                    while (it.hasNext()) {
                        indentingPrintWriter.println("Merged subscriptions: " + Arrays.toString(NetworkIdentityUtils.scrubSubscriberIds(it.next())));
                    }
                    indentingPrintWriter.println();
                    indentingPrintWriter.println("Policy for UIDs:");
                    indentingPrintWriter.increaseIndent();
                    int size2 = this.mUidPolicy.size();
                    for (int i4 = 0; i4 < size2; i4++) {
                        int keyAt2 = this.mUidPolicy.keyAt(i4);
                        int valueAt2 = this.mUidPolicy.valueAt(i4);
                        indentingPrintWriter.print("UID=");
                        indentingPrintWriter.print(keyAt2);
                        indentingPrintWriter.print(" policy=");
                        indentingPrintWriter.print(NetworkPolicyManager.uidPoliciesToString(valueAt2));
                        indentingPrintWriter.println();
                    }
                    indentingPrintWriter.decreaseIndent();
                    int size3 = this.mPowerSaveWhitelistExceptIdleAppIds.size();
                    if (size3 > 0) {
                        indentingPrintWriter.println("Power save whitelist (except idle) app ids:");
                        indentingPrintWriter.increaseIndent();
                        for (int i5 = 0; i5 < size3; i5++) {
                            indentingPrintWriter.print("UID=");
                            indentingPrintWriter.print(this.mPowerSaveWhitelistExceptIdleAppIds.keyAt(i5));
                            indentingPrintWriter.print(": ");
                            indentingPrintWriter.print(this.mPowerSaveWhitelistExceptIdleAppIds.valueAt(i5));
                            indentingPrintWriter.println();
                        }
                        indentingPrintWriter.decreaseIndent();
                    }
                    int size4 = this.mPowerSaveWhitelistAppIds.size();
                    if (size4 > 0) {
                        indentingPrintWriter.println("Power save whitelist app ids:");
                        indentingPrintWriter.increaseIndent();
                        for (int i6 = 0; i6 < size4; i6++) {
                            indentingPrintWriter.print("UID=");
                            indentingPrintWriter.print(this.mPowerSaveWhitelistAppIds.keyAt(i6));
                            indentingPrintWriter.print(": ");
                            indentingPrintWriter.print(this.mPowerSaveWhitelistAppIds.valueAt(i6));
                            indentingPrintWriter.println();
                        }
                        indentingPrintWriter.decreaseIndent();
                    }
                    int size5 = this.mAppIdleTempWhitelistAppIds.size();
                    if (size5 > 0) {
                        indentingPrintWriter.println("App idle whitelist app ids:");
                        indentingPrintWriter.increaseIndent();
                        for (int i7 = 0; i7 < size5; i7++) {
                            indentingPrintWriter.print("UID=");
                            indentingPrintWriter.print(this.mAppIdleTempWhitelistAppIds.keyAt(i7));
                            indentingPrintWriter.print(": ");
                            indentingPrintWriter.print(this.mAppIdleTempWhitelistAppIds.valueAt(i7));
                            indentingPrintWriter.println();
                        }
                        indentingPrintWriter.decreaseIndent();
                    }
                    int size6 = this.mDefaultRestrictBackgroundAllowlistUids.size();
                    if (size6 > 0) {
                        indentingPrintWriter.println("Default restrict background allowlist uids:");
                        indentingPrintWriter.increaseIndent();
                        for (int i8 = 0; i8 < size6; i8++) {
                            indentingPrintWriter.print("UID=");
                            indentingPrintWriter.print(this.mDefaultRestrictBackgroundAllowlistUids.keyAt(i8));
                            indentingPrintWriter.println();
                        }
                        indentingPrintWriter.decreaseIndent();
                    }
                    int size7 = this.mRestrictBackgroundAllowlistRevokedUids.size();
                    if (size7 > 0) {
                        indentingPrintWriter.println("Default restrict background allowlist uids revoked by users:");
                        indentingPrintWriter.increaseIndent();
                        for (int i9 = 0; i9 < size7; i9++) {
                            indentingPrintWriter.print("UID=");
                            indentingPrintWriter.print(this.mRestrictBackgroundAllowlistRevokedUids.keyAt(i9));
                            indentingPrintWriter.println();
                        }
                        indentingPrintWriter.decreaseIndent();
                    }
                    int size8 = this.mLowPowerStandbyAllowlistUids.size();
                    if (size8 > 0) {
                        indentingPrintWriter.println("Low Power Standby allowlist uids:");
                        indentingPrintWriter.increaseIndent();
                        for (int i10 = 0; i10 < size8; i10++) {
                            indentingPrintWriter.print("UID=");
                            indentingPrintWriter.print(this.mLowPowerStandbyAllowlistUids.keyAt(i10));
                            indentingPrintWriter.println();
                        }
                        indentingPrintWriter.decreaseIndent();
                    }
                    SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
                    collectKeys(this.mUidState, sparseBooleanArray);
                    synchronized (this.mUidBlockedState) {
                        collectKeys(this.mUidBlockedState, sparseBooleanArray);
                    }
                    indentingPrintWriter.println("Status for all known UIDs:");
                    indentingPrintWriter.increaseIndent();
                    int size9 = sparseBooleanArray.size();
                    for (int i11 = 0; i11 < size9; i11++) {
                        int keyAt3 = sparseBooleanArray.keyAt(i11);
                        indentingPrintWriter.print("UID=");
                        indentingPrintWriter.print(keyAt3);
                        NetworkPolicyManager.UidState uidState = this.mUidState.get(keyAt3);
                        if (uidState == null) {
                            indentingPrintWriter.print(" state={null}");
                        } else {
                            indentingPrintWriter.print(" state=");
                            indentingPrintWriter.print(uidState.toString());
                        }
                        synchronized (this.mUidBlockedState) {
                            UidBlockedState uidBlockedState = this.mUidBlockedState.get(keyAt3);
                            if (uidBlockedState == null) {
                                indentingPrintWriter.print(" blocked_state={null}");
                            } else {
                                indentingPrintWriter.print(" blocked_state=");
                                indentingPrintWriter.print(uidBlockedState);
                            }
                        }
                        indentingPrintWriter.println();
                    }
                    indentingPrintWriter.decreaseIndent();
                    indentingPrintWriter.println("Admin restricted uids for metered data:");
                    indentingPrintWriter.increaseIndent();
                    int size10 = this.mMeteredRestrictedUids.size();
                    for (int i12 = 0; i12 < size10; i12++) {
                        indentingPrintWriter.print("u" + this.mMeteredRestrictedUids.keyAt(i12) + ": ");
                        indentingPrintWriter.println(this.mMeteredRestrictedUids.valueAt(i12));
                    }
                    indentingPrintWriter.decreaseIndent();
                    indentingPrintWriter.println("Network to interfaces:");
                    indentingPrintWriter.increaseIndent();
                    for (int i13 = 0; i13 < this.mNetworkToIfaces.size(); i13++) {
                        indentingPrintWriter.println(this.mNetworkToIfaces.keyAt(i13) + ": " + this.mNetworkToIfaces.get(keyAt));
                    }
                    indentingPrintWriter.decreaseIndent();
                    indentingPrintWriter.println();
                    this.mStatLogger.dump(indentingPrintWriter);
                    this.mLogger.dumpLogs(indentingPrintWriter);
                    indentingPrintWriter.println();
                    this.mMultipathPolicyTracker.dump(indentingPrintWriter);
                }
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public int handleShellCommand(ParcelFileDescriptor parcelFileDescriptor, ParcelFileDescriptor parcelFileDescriptor2, ParcelFileDescriptor parcelFileDescriptor3, String[] strArr) {
        return new NetworkPolicyManagerShellCommand(this.mContext, this).exec(this, parcelFileDescriptor.getFileDescriptor(), parcelFileDescriptor2.getFileDescriptor(), parcelFileDescriptor3.getFileDescriptor(), strArr);
    }

    public void setDebugUid(int i) {
        this.mLogger.setDebugUid(i);
    }

    @VisibleForTesting
    public boolean isUidForeground(int i) {
        boolean isProcStateAllowedWhileIdleOrPowerSaveMode;
        synchronized (this.mUidRulesFirstLock) {
            isProcStateAllowedWhileIdleOrPowerSaveMode = NetworkPolicyManager.isProcStateAllowedWhileIdleOrPowerSaveMode(this.mUidState.get(i));
        }
        return isProcStateAllowedWhileIdleOrPowerSaveMode;
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final boolean isUidForegroundOnRestrictBackgroundUL(int i) {
        return NetworkPolicyManager.isProcStateAllowedWhileOnRestrictBackground(this.mUidState.get(i));
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final boolean isUidForegroundOnRestrictPowerUL(int i) {
        return NetworkPolicyManager.isProcStateAllowedWhileIdleOrPowerSaveMode(this.mUidState.get(i));
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final boolean isUidTop(int i) {
        return NetworkPolicyManager.isProcStateAllowedWhileInLowPowerStandby(this.mUidState.get(i));
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final boolean updateUidStateUL(int i, int i2, long j, int i3) {
        Trace.traceBegin(2097152L, "updateUidStateUL");
        try {
            NetworkPolicyManager.UidState uidState = this.mUidState.get(i);
            if (uidState != null && j < uidState.procStateSeq) {
                if (LOGV) {
                    Slog.v("NetworkPolicy", "Ignoring older uid state updates; uid=" + i + ",procState=" + ActivityManager.procStateToString(i2) + ",seq=" + j + ",cap=" + i3 + ",oldUidState=" + uidState);
                }
                return false;
            }
            if (uidState != null && uidState.procState == i2 && uidState.capability == i3) {
                return false;
            }
            NetworkPolicyManager.UidState uidState2 = new NetworkPolicyManager.UidState(i, i2, j, i3);
            this.mUidState.put(i, uidState2);
            updateRestrictBackgroundRulesOnUidStatusChangedUL(i, uidState, uidState2);
            boolean z = NetworkPolicyManager.isProcStateAllowedWhileIdleOrPowerSaveMode(uidState) != NetworkPolicyManager.isProcStateAllowedWhileIdleOrPowerSaveMode(uidState2);
            if (z) {
                updateRuleForAppIdleUL(i, i2);
                if (this.mDeviceIdleMode) {
                    updateRuleForDeviceIdleUL(i);
                }
                if (this.mRestrictPower) {
                    updateRuleForRestrictPowerUL(i);
                }
                updateRulesForPowerRestrictionsUL(i, i2);
            }
            if (this.mLowPowerStandbyActive) {
                if (NetworkPolicyManager.isProcStateAllowedWhileInLowPowerStandby(uidState) != NetworkPolicyManager.isProcStateAllowedWhileInLowPowerStandby(uidState2)) {
                    if (!z) {
                        updateRulesForPowerRestrictionsUL(i, i2);
                    }
                    updateRuleForLowPowerStandbyUL(i);
                }
            }
            return true;
        } finally {
            Trace.traceEnd(2097152L);
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final boolean removeUidStateUL(int i) {
        int indexOfKey = this.mUidState.indexOfKey(i);
        if (indexOfKey >= 0) {
            NetworkPolicyManager.UidState valueAt = this.mUidState.valueAt(indexOfKey);
            this.mUidState.removeAt(indexOfKey);
            if (valueAt != null) {
                updateRestrictBackgroundRulesOnUidStatusChangedUL(i, valueAt, null);
                if (this.mDeviceIdleMode) {
                    updateRuleForDeviceIdleUL(i);
                }
                if (this.mRestrictPower) {
                    updateRuleForRestrictPowerUL(i);
                }
                lambda$updateRulesForRestrictPowerUL$5(i);
                if (this.mLowPowerStandbyActive) {
                    updateRuleForLowPowerStandbyUL(i);
                    return true;
                }
                return true;
            }
            return false;
        }
        return false;
    }

    public final void updateNetworkStats(int i, boolean z) {
        if (Trace.isTagEnabled(2097152L)) {
            StringBuilder sb = new StringBuilder();
            sb.append("updateNetworkStats: ");
            sb.append(i);
            sb.append("/");
            sb.append(z ? "F" : "B");
            Trace.traceBegin(2097152L, sb.toString());
        }
        try {
            this.mNetworkStats.noteUidForeground(i, z);
        } finally {
            Trace.traceEnd(2097152L);
        }
    }

    public final void updateRestrictBackgroundRulesOnUidStatusChangedUL(int i, NetworkPolicyManager.UidState uidState, NetworkPolicyManager.UidState uidState2) {
        if (NetworkPolicyManager.isProcStateAllowedWhileOnRestrictBackground(uidState) != NetworkPolicyManager.isProcStateAllowedWhileOnRestrictBackground(uidState2)) {
            lambda$updateRulesForRestrictBackgroundUL$6(i);
        }
    }

    @VisibleForTesting
    public boolean isRestrictedModeEnabled() {
        boolean z;
        synchronized (this.mUidRulesFirstLock) {
            z = this.mRestrictedNetworkingMode;
        }
        return z;
    }

    @GuardedBy({"mUidRulesFirstLock"})
    @VisibleForTesting
    public void updateRestrictedModeAllowlistUL() {
        this.mUidFirewallRestrictedModeRules.clear();
        forEachUid("updateRestrictedModeAllowlist", new IntConsumer() { // from class: com.android.server.net.NetworkPolicyManagerService$$ExternalSyntheticLambda0
            @Override // java.util.function.IntConsumer
            public final void accept(int i) {
                NetworkPolicyManagerService.this.lambda$updateRestrictedModeAllowlistUL$3(i);
            }
        });
        if (this.mRestrictedNetworkingMode) {
            setUidFirewallRulesUL(4, this.mUidFirewallRestrictedModeRules);
        }
        enableFirewallChainUL(4, this.mRestrictedNetworkingMode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateRestrictedModeAllowlistUL$3(int i) {
        synchronized (this.mUidRulesFirstLock) {
            int restrictedModeFirewallRule = getRestrictedModeFirewallRule(updateBlockedReasonsForRestrictedModeUL(i));
            if (restrictedModeFirewallRule != 0) {
                this.mUidFirewallRestrictedModeRules.append(i, restrictedModeFirewallRule);
            }
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    @VisibleForTesting
    public void updateRestrictedModeForUidUL(int i) {
        int updateBlockedReasonsForRestrictedModeUL = updateBlockedReasonsForRestrictedModeUL(i);
        if (this.mRestrictedNetworkingMode) {
            setUidFirewallRuleUL(4, i, getRestrictedModeFirewallRule(updateBlockedReasonsForRestrictedModeUL));
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final int updateBlockedReasonsForRestrictedModeUL(int i) {
        int i2;
        int i3;
        int deriveUidRules;
        boolean hasRestrictedModeAccess = hasRestrictedModeAccess(i);
        synchronized (this.mUidBlockedState) {
            UidBlockedState orCreateUidBlockedStateForUid = getOrCreateUidBlockedStateForUid(this.mUidBlockedState, i);
            i2 = orCreateUidBlockedStateForUid.effectiveBlockedReasons;
            if (this.mRestrictedNetworkingMode) {
                orCreateUidBlockedStateForUid.blockedReasons |= 8;
            } else {
                orCreateUidBlockedStateForUid.blockedReasons &= -9;
            }
            if (hasRestrictedModeAccess) {
                orCreateUidBlockedStateForUid.allowedReasons |= 16;
            } else {
                orCreateUidBlockedStateForUid.allowedReasons &= -17;
            }
            orCreateUidBlockedStateForUid.updateEffectiveBlockedReasons();
            i3 = orCreateUidBlockedStateForUid.effectiveBlockedReasons;
            deriveUidRules = i2 == i3 ? 0 : orCreateUidBlockedStateForUid.deriveUidRules();
        }
        if (i2 != i3) {
            handleBlockedReasonsChanged(i, i3, i2);
            postUidRulesChangedMsg(i, deriveUidRules);
        }
        return i3;
    }

    public final boolean hasRestrictedModeAccess(int i) {
        try {
            if (this.mIPm.checkUidPermission("android.permission.CONNECTIVITY_USE_RESTRICTED_NETWORKS", i) != 0 && this.mIPm.checkUidPermission("android.permission.NETWORK_STACK", i) != 0) {
                if (this.mIPm.checkUidPermission("android.permission.MAINLINE_NETWORK_STACK", i) != 0) {
                    return false;
                }
            }
            return true;
        } catch (RemoteException unused) {
            return false;
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public void updateRulesForPowerSaveUL() {
        Trace.traceBegin(2097152L, "updateRulesForPowerSaveUL");
        try {
            updateRulesForWhitelistedPowerSaveUL(this.mRestrictPower, 3, this.mUidFirewallPowerSaveRules);
        } finally {
            Trace.traceEnd(2097152L);
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public void updateRuleForRestrictPowerUL(int i) {
        updateRulesForWhitelistedPowerSaveUL(i, this.mRestrictPower, 3);
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public void updateRulesForDeviceIdleUL() {
        Trace.traceBegin(2097152L, "updateRulesForDeviceIdleUL");
        try {
            updateRulesForWhitelistedPowerSaveUL(this.mDeviceIdleMode, 1, this.mUidFirewallDozableRules);
        } finally {
            Trace.traceEnd(2097152L);
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public void updateRuleForDeviceIdleUL(int i) {
        updateRulesForWhitelistedPowerSaveUL(i, this.mDeviceIdleMode, 1);
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final void updateRulesForWhitelistedPowerSaveUL(boolean z, int i, SparseIntArray sparseIntArray) {
        if (z) {
            sparseIntArray.clear();
            List users = this.mUserManager.getUsers();
            for (int size = users.size() - 1; size >= 0; size--) {
                UserInfo userInfo = (UserInfo) users.get(size);
                updateRulesForWhitelistedAppIds(sparseIntArray, this.mPowerSaveTempWhitelistAppIds, userInfo.id);
                updateRulesForWhitelistedAppIds(sparseIntArray, this.mPowerSaveWhitelistAppIds, userInfo.id);
                if (i == 3) {
                    updateRulesForWhitelistedAppIds(sparseIntArray, this.mPowerSaveWhitelistExceptIdleAppIds, userInfo.id);
                }
            }
            for (int size2 = this.mUidState.size() - 1; size2 >= 0; size2--) {
                if (NetworkPolicyManager.isProcStateAllowedWhileIdleOrPowerSaveMode(this.mUidState.valueAt(size2))) {
                    sparseIntArray.put(this.mUidState.keyAt(size2), 1);
                }
            }
            setUidFirewallRulesUL(i, sparseIntArray, 1);
            return;
        }
        setUidFirewallRulesUL(i, null, 2);
    }

    public final void updateRulesForWhitelistedAppIds(SparseIntArray sparseIntArray, SparseBooleanArray sparseBooleanArray, int i) {
        for (int size = sparseBooleanArray.size() - 1; size >= 0; size--) {
            if (sparseBooleanArray.valueAt(size)) {
                sparseIntArray.put(UserHandle.getUid(i, sparseBooleanArray.keyAt(size)), 1);
            }
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public void updateRulesForLowPowerStandbyUL() {
        Trace.traceBegin(2097152L, "updateRulesForLowPowerStandbyUL");
        try {
            if (this.mLowPowerStandbyActive) {
                this.mUidFirewallLowPowerStandbyModeRules.clear();
                for (int size = this.mUidState.size() - 1; size >= 0; size--) {
                    int keyAt = this.mUidState.keyAt(size);
                    int effectiveBlockedReasons = getEffectiveBlockedReasons(keyAt);
                    if (hasInternetPermissionUL(keyAt) && (effectiveBlockedReasons & 32) == 0) {
                        this.mUidFirewallLowPowerStandbyModeRules.put(keyAt, 1);
                    }
                }
                setUidFirewallRulesUL(5, this.mUidFirewallLowPowerStandbyModeRules, 1);
            } else {
                setUidFirewallRulesUL(5, null, 2);
            }
        } finally {
            Trace.traceEnd(2097152L);
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public void updateRuleForLowPowerStandbyUL(int i) {
        if (hasInternetPermissionUL(i)) {
            int effectiveBlockedReasons = getEffectiveBlockedReasons(i);
            if (this.mUidState.contains(i) && (effectiveBlockedReasons & 32) == 0) {
                this.mUidFirewallLowPowerStandbyModeRules.put(i, 1);
                setUidFirewallRuleUL(5, i, 1);
                return;
            }
            this.mUidFirewallLowPowerStandbyModeRules.delete(i);
            setUidFirewallRuleUL(5, i, 0);
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final boolean isWhitelistedFromPowerSaveUL(int i, boolean z) {
        int appId = UserHandle.getAppId(i);
        boolean z2 = false;
        boolean z3 = this.mPowerSaveTempWhitelistAppIds.get(appId) || this.mPowerSaveWhitelistAppIds.get(appId);
        if (z) {
            return z3;
        }
        if (z3 || isWhitelistedFromPowerSaveExceptIdleUL(i)) {
            z2 = true;
        }
        return z2;
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final boolean isWhitelistedFromPowerSaveExceptIdleUL(int i) {
        return this.mPowerSaveWhitelistExceptIdleAppIds.get(UserHandle.getAppId(i));
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final boolean isAllowlistedFromLowPowerStandbyUL(int i) {
        return this.mLowPowerStandbyAllowlistUids.get(i);
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final void updateRulesForWhitelistedPowerSaveUL(int i, boolean z, int i2) {
        if (z) {
            if (isWhitelistedFromPowerSaveUL(i, i2 == 1) || isUidForegroundOnRestrictPowerUL(i)) {
                setUidFirewallRuleUL(i2, i, 1);
            } else {
                setUidFirewallRuleUL(i2, i, 0);
            }
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public void updateRulesForAppIdleUL() {
        int[] idleUidsForUser;
        Trace.traceBegin(2097152L, "updateRulesForAppIdleUL");
        try {
            SparseIntArray sparseIntArray = this.mUidFirewallStandbyRules;
            sparseIntArray.clear();
            List users = this.mUserManager.getUsers();
            for (int size = users.size() - 1; size >= 0; size--) {
                for (int i : this.mUsageStats.getIdleUidsForUser(((UserInfo) users.get(size)).id)) {
                    if (!this.mPowerSaveTempWhitelistAppIds.get(UserHandle.getAppId(i), false) && hasInternetPermissionUL(i) && !isUidForegroundOnRestrictPowerUL(i)) {
                        sparseIntArray.put(i, 2);
                    }
                }
            }
            setUidFirewallRulesUL(2, sparseIntArray, 0);
        } finally {
            Trace.traceEnd(2097152L);
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public void updateRuleForAppIdleUL(int i, int i2) {
        if (isUidValidForDenylistRulesUL(i)) {
            if (Trace.isTagEnabled(2097152L)) {
                Trace.traceBegin(2097152L, "updateRuleForAppIdleUL: " + i);
            }
            try {
                if (!this.mPowerSaveTempWhitelistAppIds.get(UserHandle.getAppId(i)) && isUidIdle(i, i2) && !isUidForegroundOnRestrictPowerUL(i)) {
                    setUidFirewallRuleUL(2, i, 2);
                    if (LOGD) {
                        Log.d("NetworkPolicy", "updateRuleForAppIdleUL DENY " + i);
                    }
                } else {
                    setUidFirewallRuleUL(2, i, 0);
                    if (LOGD) {
                        Log.d("NetworkPolicy", "updateRuleForAppIdleUL " + i + " to DEFAULT");
                    }
                }
            } finally {
                Trace.traceEnd(2097152L);
            }
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final void updateRulesForAppIdleParoleUL() {
        boolean isInParole = this.mAppStandby.isInParole();
        boolean z = !isInParole;
        int size = this.mUidFirewallStandbyRules.size();
        SparseIntArray sparseIntArray = new SparseIntArray();
        int i = 0;
        while (true) {
            boolean z2 = true;
            if (i >= size) {
                break;
            }
            int keyAt = this.mUidFirewallStandbyRules.keyAt(i);
            if (isUidValidForDenylistRulesUL(keyAt)) {
                int blockedReasons = getBlockedReasons(keyAt);
                if (z || (blockedReasons & GnssNative.GNSS_AIDING_TYPE_ALL) != 0) {
                    if (isInParole || !isUidIdle(keyAt)) {
                        z2 = false;
                    }
                    if (z2 && !this.mPowerSaveTempWhitelistAppIds.get(UserHandle.getAppId(keyAt)) && !isUidForegroundOnRestrictPowerUL(keyAt)) {
                        this.mUidFirewallStandbyRules.put(keyAt, 2);
                        sparseIntArray.put(keyAt, 2);
                    } else {
                        this.mUidFirewallStandbyRules.put(keyAt, 0);
                    }
                    updateRulesForPowerRestrictionsUL(keyAt, z2);
                }
            }
            i++;
        }
        setUidFirewallRulesUL(2, sparseIntArray, z ? 1 : 2);
    }

    @GuardedBy({"mUidRulesFirstLock", "mNetworkPoliciesSecondLock"})
    public final void updateRulesForGlobalChangeAL(boolean z) {
        if (Trace.isTagEnabled(2097152L)) {
            StringBuilder sb = new StringBuilder();
            sb.append("updateRulesForGlobalChangeAL: ");
            sb.append(z ? "R" : PackageManagerShellCommandDataLoader.STDIN_PATH);
            Trace.traceBegin(2097152L, sb.toString());
        }
        try {
            updateRulesForAppIdleUL();
            updateRulesForRestrictPowerUL();
            updateRulesForRestrictBackgroundUL();
            updateRestrictedModeAllowlistUL();
            if (z) {
                normalizePoliciesNL();
                updateNetworkRulesNL();
            }
        } finally {
            Trace.traceEnd(2097152L);
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final void handleDeviceIdleModeChangedUL(boolean z) {
        Trace.traceBegin(2097152L, "updateRulesForRestrictPowerUL");
        try {
            updateRulesForDeviceIdleUL();
            if (z) {
                forEachUid("updateRulesForRestrictPower", new IntConsumer() { // from class: com.android.server.net.NetworkPolicyManagerService$$ExternalSyntheticLambda3
                    @Override // java.util.function.IntConsumer
                    public final void accept(int i) {
                        NetworkPolicyManagerService.this.lambda$handleDeviceIdleModeChangedUL$4(i);
                    }
                });
            } else {
                handleDeviceIdleModeDisabledUL();
            }
        } finally {
            Trace.traceEnd(2097152L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleDeviceIdleModeChangedUL$4(int i) {
        synchronized (this.mUidRulesFirstLock) {
            lambda$updateRulesForRestrictPowerUL$5(i);
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final void handleDeviceIdleModeDisabledUL() {
        Trace.traceBegin(2097152L, "handleDeviceIdleModeDisabledUL");
        try {
            SparseArray sparseArray = new SparseArray();
            synchronized (this.mUidBlockedState) {
                int size = this.mUidBlockedState.size();
                for (int i = 0; i < size; i++) {
                    int keyAt = this.mUidBlockedState.keyAt(i);
                    UidBlockedState valueAt = this.mUidBlockedState.valueAt(i);
                    int i2 = valueAt.blockedReasons;
                    if ((i2 & 2) != 0) {
                        valueAt.blockedReasons = i2 & (-3);
                        int i3 = valueAt.effectiveBlockedReasons;
                        valueAt.updateEffectiveBlockedReasons();
                        if (LOGV) {
                            Log.v("NetworkPolicy", "handleDeviceIdleModeDisabled(" + keyAt + "); newUidBlockedState=" + valueAt + ", oldEffectiveBlockedReasons=" + i3);
                        }
                        if (i3 != valueAt.effectiveBlockedReasons) {
                            SomeArgs obtain = SomeArgs.obtain();
                            obtain.argi1 = i3;
                            obtain.argi2 = valueAt.effectiveBlockedReasons;
                            obtain.argi3 = valueAt.deriveUidRules();
                            sparseArray.append(keyAt, obtain);
                            this.mActivityManagerInternal.onUidBlockedReasonsChanged(keyAt, valueAt.effectiveBlockedReasons);
                        }
                    }
                }
            }
            if (sparseArray.size() != 0) {
                this.mHandler.obtainMessage(23, sparseArray).sendToTarget();
            }
        } finally {
            Trace.traceEnd(2097152L);
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final void updateRulesForRestrictPowerUL() {
        Trace.traceBegin(2097152L, "updateRulesForRestrictPowerUL");
        try {
            updateRulesForDeviceIdleUL();
            updateRulesForPowerSaveUL();
            forEachUid("updateRulesForRestrictPower", new IntConsumer() { // from class: com.android.server.net.NetworkPolicyManagerService$$ExternalSyntheticLambda4
                @Override // java.util.function.IntConsumer
                public final void accept(int i) {
                    NetworkPolicyManagerService.this.lambda$updateRulesForRestrictPowerUL$5(i);
                }
            });
        } finally {
            Trace.traceEnd(2097152L);
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final void updateRulesForRestrictBackgroundUL() {
        Trace.traceBegin(2097152L, "updateRulesForRestrictBackgroundUL");
        try {
            forEachUid("updateRulesForRestrictBackground", new IntConsumer() { // from class: com.android.server.net.NetworkPolicyManagerService$$ExternalSyntheticLambda2
                @Override // java.util.function.IntConsumer
                public final void accept(int i) {
                    NetworkPolicyManagerService.this.lambda$updateRulesForRestrictBackgroundUL$6(i);
                }
            });
        } finally {
            Trace.traceEnd(2097152L);
        }
    }

    public final void forEachUid(String str, final IntConsumer intConsumer) {
        if (Trace.isTagEnabled(2097152L)) {
            Trace.traceBegin(2097152L, "forEachUid-" + str);
        }
        try {
            Trace.traceBegin(2097152L, "list-users");
            List users = this.mUserManager.getUsers();
            Trace.traceEnd(2097152L);
            Trace.traceBegin(2097152L, "iterate-uids");
            PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
            int size = users.size();
            for (int i = 0; i < size; i++) {
                final int i2 = ((UserInfo) users.get(i)).id;
                final SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
                packageManagerInternal.forEachInstalledPackage(new Consumer() { // from class: com.android.server.net.NetworkPolicyManagerService$$ExternalSyntheticLambda5
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        NetworkPolicyManagerService.lambda$forEachUid$7(sparseBooleanArray, i2, intConsumer, (AndroidPackage) obj);
                    }
                }, i2);
            }
            Trace.traceEnd(2097152L);
        } finally {
            Trace.traceEnd(2097152L);
        }
    }

    public static /* synthetic */ void lambda$forEachUid$7(SparseBooleanArray sparseBooleanArray, int i, IntConsumer intConsumer, AndroidPackage androidPackage) {
        int uid = androidPackage.getUid();
        if (androidPackage.getSharedUserId() != null) {
            if (sparseBooleanArray.indexOfKey(uid) >= 0) {
                return;
            }
            sparseBooleanArray.put(uid, true);
        }
        intConsumer.accept(UserHandle.getUid(i, uid));
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final void updateRulesForTempWhitelistChangeUL(int i) {
        List users = this.mUserManager.getUsers();
        int size = users.size();
        for (int i2 = 0; i2 < size; i2++) {
            int uid = UserHandle.getUid(((UserInfo) users.get(i2)).id, i);
            updateRuleForAppIdleUL(uid, -1);
            updateRuleForDeviceIdleUL(uid);
            updateRuleForRestrictPowerUL(uid);
            lambda$updateRulesForRestrictPowerUL$5(uid);
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final boolean isUidValidForDenylistRulesUL(int i) {
        return i == 1013 || i == 1019 || isUidValidForAllowlistRulesUL(i);
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final boolean isUidValidForAllowlistRulesUL(int i) {
        return UserHandle.isApp(i) && hasInternetPermissionUL(i);
    }

    @VisibleForTesting
    public void setAppIdleWhitelist(int i, boolean z) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
        synchronized (this.mUidRulesFirstLock) {
            if (this.mAppIdleTempWhitelistAppIds.get(i) == z) {
                return;
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            this.mLogger.appIdleWlChanged(i, z);
            if (z) {
                this.mAppIdleTempWhitelistAppIds.put(i, true);
            } else {
                this.mAppIdleTempWhitelistAppIds.delete(i);
            }
            updateRuleForAppIdleUL(i, -1);
            lambda$updateRulesForRestrictPowerUL$5(i);
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @VisibleForTesting
    public int[] getAppIdleWhitelist() {
        int[] iArr;
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_NETWORK_POLICY", "NetworkPolicy");
        synchronized (this.mUidRulesFirstLock) {
            int size = this.mAppIdleTempWhitelistAppIds.size();
            iArr = new int[size];
            for (int i = 0; i < size; i++) {
                iArr[i] = this.mAppIdleTempWhitelistAppIds.keyAt(i);
            }
        }
        return iArr;
    }

    @VisibleForTesting
    public boolean isUidIdle(int i) {
        return isUidIdle(i, -1);
    }

    public final boolean isUidIdle(int i, int i2) {
        synchronized (this.mUidRulesFirstLock) {
            if (i2 != -1) {
                if (ActivityManager.isProcStateConsideredInteraction(i2)) {
                    return false;
                }
            }
            if (this.mAppIdleTempWhitelistAppIds.get(i)) {
                return false;
            }
            String[] packagesForUid = this.mContext.getPackageManager().getPackagesForUid(i);
            int userId = UserHandle.getUserId(i);
            if (packagesForUid != null) {
                for (String str : packagesForUid) {
                    if (!this.mUsageStats.isAppIdle(str, i, userId)) {
                        return false;
                    }
                }
                return true;
            }
            return true;
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final boolean hasInternetPermissionUL(int i) {
        try {
            if (this.mInternetPermissionMap.get(i)) {
                return true;
            }
            boolean z = this.mIPm.checkUidPermission("android.permission.INTERNET", i) == 0;
            this.mInternetPermissionMap.put(i, z);
            return z;
        } catch (RemoteException unused) {
            return true;
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final void onUidDeletedUL(int i) {
        synchronized (this.mUidBlockedState) {
            this.mUidBlockedState.delete(i);
        }
        this.mUidState.delete(i);
        this.mActivityManagerInternal.onUidBlockedReasonsChanged(i, 0);
        this.mUidPolicy.delete(i);
        this.mUidFirewallStandbyRules.delete(i);
        this.mUidFirewallDozableRules.delete(i);
        this.mUidFirewallPowerSaveRules.delete(i);
        this.mPowerSaveWhitelistExceptIdleAppIds.delete(i);
        this.mPowerSaveWhitelistAppIds.delete(i);
        this.mPowerSaveTempWhitelistAppIds.delete(i);
        this.mAppIdleTempWhitelistAppIds.delete(i);
        this.mUidFirewallRestrictedModeRules.delete(i);
        this.mUidFirewallLowPowerStandbyModeRules.delete(i);
        synchronized (this.mUidStateCallbackInfos) {
            this.mUidStateCallbackInfos.remove(i);
        }
        this.mHandler.obtainMessage(15, i, 0).sendToTarget();
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final void updateRestrictionRulesForUidUL(int i) {
        updateRuleForDeviceIdleUL(i);
        updateRuleForAppIdleUL(i, -1);
        updateRuleForRestrictPowerUL(i);
        updateRestrictedModeForUidUL(i);
        lambda$updateRulesForRestrictPowerUL$5(i);
        lambda$updateRulesForRestrictBackgroundUL$6(i);
    }

    /* renamed from: updateRulesForDataUsageRestrictionsUL */
    public final void lambda$updateRulesForRestrictBackgroundUL$6(int i) {
        if (Trace.isTagEnabled(2097152L)) {
            Trace.traceBegin(2097152L, "updateRulesForDataUsageRestrictionsUL: " + i);
        }
        try {
            updateRulesForDataUsageRestrictionsULInner(i);
        } finally {
            Trace.traceEnd(2097152L);
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final void updateRulesForDataUsageRestrictionsULInner(int i) {
        int i2;
        int i3;
        int i4;
        int i5;
        if (!isUidValidForAllowlistRulesUL(i)) {
            if (LOGD) {
                Slog.d("NetworkPolicy", "no need to update restrict data rules for uid " + i);
                return;
            }
            return;
        }
        int i6 = 0;
        int i7 = this.mUidPolicy.get(i, 0);
        boolean isUidForegroundOnRestrictBackgroundUL = isUidForegroundOnRestrictBackgroundUL(i);
        boolean isRestrictedByAdminUL = isRestrictedByAdminUL(i);
        boolean z = (i7 & 1) != 0;
        boolean z2 = (i7 & 4) != 0;
        int i8 = (isRestrictedByAdminUL ? 262144 : 0) | 0 | (this.mRestrictBackground ? 65536 : 0);
        int i9 = IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES;
        int i10 = i8 | (z ? 131072 : 0);
        if (!isSystem(i)) {
            i9 = 0;
        }
        int i11 = (isUidForegroundOnRestrictBackgroundUL ? 262144 : 0) | i9 | 0 | (z2 ? 65536 : 0);
        synchronized (this.mUidBlockedState) {
            UidBlockedState orCreateUidBlockedStateForUid = getOrCreateUidBlockedStateForUid(this.mUidBlockedState, i);
            UidBlockedState orCreateUidBlockedStateForUid2 = getOrCreateUidBlockedStateForUid(this.mTmpUidBlockedState, i);
            orCreateUidBlockedStateForUid2.copyFrom(orCreateUidBlockedStateForUid);
            orCreateUidBlockedStateForUid.blockedReasons = (orCreateUidBlockedStateForUid.blockedReasons & GnssNative.GNSS_AIDING_TYPE_ALL) | i10;
            orCreateUidBlockedStateForUid.allowedReasons = (orCreateUidBlockedStateForUid.allowedReasons & GnssNative.GNSS_AIDING_TYPE_ALL) | i11;
            orCreateUidBlockedStateForUid.updateEffectiveBlockedReasons();
            i2 = orCreateUidBlockedStateForUid2.effectiveBlockedReasons;
            i3 = orCreateUidBlockedStateForUid.effectiveBlockedReasons;
            int i12 = orCreateUidBlockedStateForUid2.allowedReasons;
            if (i2 != i3) {
                i6 = orCreateUidBlockedStateForUid.deriveUidRules();
            }
            if (LOGV) {
                i5 = i12;
                StringBuilder sb = new StringBuilder();
                i4 = i6;
                sb.append("updateRuleForRestrictBackgroundUL(");
                sb.append(i);
                sb.append("): isForeground=");
                sb.append(isUidForegroundOnRestrictBackgroundUL);
                sb.append(", isDenied=");
                sb.append(z);
                sb.append(", isAllowed=");
                sb.append(z2);
                sb.append(", isRestrictedByAdmin=");
                sb.append(isRestrictedByAdminUL);
                sb.append(", oldBlockedState=");
                sb.append(orCreateUidBlockedStateForUid2);
                sb.append(", newBlockedState=");
                sb.append(orCreateUidBlockedStateForUid);
                sb.append(", newBlockedMeteredReasons=");
                sb.append(NetworkPolicyManager.blockedReasonsToString(i10));
                sb.append(", newAllowedMeteredReasons=");
                sb.append(NetworkPolicyManager.allowedReasonsToString(i11));
                Log.v("NetworkPolicy", sb.toString());
            } else {
                i4 = i6;
                i5 = i12;
            }
        }
        if (i2 != i3) {
            handleBlockedReasonsChanged(i, i3, i2);
            postUidRulesChangedMsg(i, i4);
        }
        if ((i2 & 393216) != 0 || (i3 & 393216) != 0) {
            setMeteredNetworkDenylist(i, (393216 & i3) != 0);
        }
        if ((i5 & 327680) == 0 && (i11 & 327680) == 0) {
            return;
        }
        setMeteredNetworkAllowlist(i, (327680 & i11) != 0);
    }

    @GuardedBy({"mUidRulesFirstLock"})
    /* renamed from: updateRulesForPowerRestrictionsUL */
    public final void lambda$updateRulesForRestrictPowerUL$5(int i) {
        updateRulesForPowerRestrictionsUL(i, -1);
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final void updateRulesForPowerRestrictionsUL(int i, int i2) {
        updateRulesForPowerRestrictionsUL(i, isUidIdle(i, i2));
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final void updateRulesForPowerRestrictionsUL(int i, boolean z) {
        if (Trace.isTagEnabled(2097152L)) {
            StringBuilder sb = new StringBuilder();
            sb.append("updateRulesForPowerRestrictionsUL: ");
            sb.append(i);
            sb.append("/");
            sb.append(z ? "I" : PackageManagerShellCommandDataLoader.STDIN_PATH);
            Trace.traceBegin(2097152L, sb.toString());
        }
        try {
            updateRulesForPowerRestrictionsULInner(i, z);
        } finally {
            Trace.traceEnd(2097152L);
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final void updateRulesForPowerRestrictionsULInner(int i, boolean z) {
        int i2;
        int i3;
        int i4;
        if (!isUidValidForDenylistRulesUL(i)) {
            if (LOGD) {
                Slog.d("NetworkPolicy", "no need to update restrict power rules for uid " + i);
                return;
            }
            return;
        }
        boolean isUidForegroundOnRestrictPowerUL = isUidForegroundOnRestrictPowerUL(i);
        boolean isUidTop = isUidTop(i);
        boolean isWhitelistedFromPowerSaveUL = isWhitelistedFromPowerSaveUL(i, this.mDeviceIdleMode);
        synchronized (this.mUidBlockedState) {
            UidBlockedState orCreateUidBlockedStateForUid = getOrCreateUidBlockedStateForUid(this.mUidBlockedState, i);
            UidBlockedState orCreateUidBlockedStateForUid2 = getOrCreateUidBlockedStateForUid(this.mTmpUidBlockedState, i);
            orCreateUidBlockedStateForUid2.copyFrom(orCreateUidBlockedStateForUid);
            i2 = 0;
            int i5 = 2;
            int i6 = 32;
            int i7 = 4;
            int i8 = 8;
            int i9 = (this.mRestrictPower ? 1 : 0) | 0 | (this.mDeviceIdleMode ? 2 : 0) | (this.mLowPowerStandbyActive ? 32 : 0) | (z ? 4 : 0) | (orCreateUidBlockedStateForUid.blockedReasons & 8);
            int i10 = (isSystem(i) ? 1 : 0) | 0;
            if (!isUidForegroundOnRestrictPowerUL) {
                i5 = 0;
            }
            int i11 = i5 | i10;
            if (!isUidTop) {
                i6 = 0;
            }
            int i12 = i11 | i6;
            if (!isWhitelistedFromPowerSaveUL(i, true)) {
                i7 = 0;
            }
            int i13 = i12 | i7;
            if (!isWhitelistedFromPowerSaveExceptIdleUL(i)) {
                i8 = 0;
            }
            int i14 = i13 | i8 | (orCreateUidBlockedStateForUid.allowedReasons & 16) | (isAllowlistedFromLowPowerStandbyUL(i) ? 64 : 0);
            orCreateUidBlockedStateForUid.blockedReasons = i9 | (orCreateUidBlockedStateForUid.blockedReasons & (-65536));
            orCreateUidBlockedStateForUid.allowedReasons = (orCreateUidBlockedStateForUid.allowedReasons & (-65536)) | i14;
            orCreateUidBlockedStateForUid.updateEffectiveBlockedReasons();
            if (LOGV) {
                Log.v("NetworkPolicy", "updateRulesForPowerRestrictionsUL(" + i + "), isIdle: " + z + ", mRestrictPower: " + this.mRestrictPower + ", mDeviceIdleMode: " + this.mDeviceIdleMode + ", isForeground=" + isUidForegroundOnRestrictPowerUL + ", isTop=" + isUidTop + ", isWhitelisted=" + isWhitelistedFromPowerSaveUL + ", oldUidBlockedState=" + orCreateUidBlockedStateForUid2 + ", newUidBlockedState=" + orCreateUidBlockedStateForUid);
            }
            i3 = orCreateUidBlockedStateForUid2.effectiveBlockedReasons;
            i4 = orCreateUidBlockedStateForUid.effectiveBlockedReasons;
            if (i3 != i4) {
                i2 = orCreateUidBlockedStateForUid.deriveUidRules();
            }
        }
        if (i3 != i4) {
            handleBlockedReasonsChanged(i, i4, i3);
            postUidRulesChangedMsg(i, i2);
        }
    }

    /* loaded from: classes2.dex */
    public class NetPolicyAppIdleStateChangeListener extends AppStandbyInternal.AppIdleStateChangeListener {
        public NetPolicyAppIdleStateChangeListener() {
        }

        public void onAppIdleStateChanged(String str, int i, boolean z, int i2, int i3) {
            try {
                int packageUidAsUser = NetworkPolicyManagerService.this.mContext.getPackageManager().getPackageUidAsUser(str, IInstalld.FLAG_FORCE, i);
                synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                    NetworkPolicyManagerService.this.mLogger.appIdleStateChanged(packageUidAsUser, z);
                    NetworkPolicyManagerService.this.updateRuleForAppIdleUL(packageUidAsUser, -1);
                    NetworkPolicyManagerService.this.lambda$updateRulesForRestrictPowerUL$5(packageUidAsUser);
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }

        public void onParoleStateChanged(boolean z) {
            synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                NetworkPolicyManagerService.this.mLogger.paroleStateChanged(z);
                NetworkPolicyManagerService.this.updateRulesForAppIdleParoleUL();
            }
        }
    }

    public final void handleBlockedReasonsChanged(int i, int i2, int i3) {
        this.mActivityManagerInternal.onUidBlockedReasonsChanged(i, i2);
        postBlockedReasonsChangedMsg(i, i2, i3);
    }

    public final void postBlockedReasonsChangedMsg(int i, int i2, int i3) {
        this.mHandler.obtainMessage(21, i, i2, Integer.valueOf(i3)).sendToTarget();
    }

    public final void postUidRulesChangedMsg(int i, int i2) {
        this.mHandler.obtainMessage(1, i, i2).sendToTarget();
    }

    public final void dispatchUidRulesChanged(INetworkPolicyListener iNetworkPolicyListener, int i, int i2) {
        try {
            iNetworkPolicyListener.onUidRulesChanged(i, i2);
        } catch (RemoteException unused) {
        }
    }

    public final void dispatchMeteredIfacesChanged(INetworkPolicyListener iNetworkPolicyListener, String[] strArr) {
        try {
            iNetworkPolicyListener.onMeteredIfacesChanged(strArr);
        } catch (RemoteException unused) {
        }
    }

    public final void dispatchRestrictBackgroundChanged(INetworkPolicyListener iNetworkPolicyListener, boolean z) {
        try {
            iNetworkPolicyListener.onRestrictBackgroundChanged(z);
        } catch (RemoteException unused) {
        }
    }

    public final void dispatchUidPoliciesChanged(INetworkPolicyListener iNetworkPolicyListener, int i, int i2) {
        try {
            iNetworkPolicyListener.onUidPoliciesChanged(i, i2);
        } catch (RemoteException unused) {
        }
    }

    public final void dispatchSubscriptionOverride(INetworkPolicyListener iNetworkPolicyListener, int i, int i2, int i3, int[] iArr) {
        try {
            iNetworkPolicyListener.onSubscriptionOverride(i, i2, i3, iArr);
        } catch (RemoteException unused) {
        }
    }

    public final void dispatchSubscriptionPlansChanged(INetworkPolicyListener iNetworkPolicyListener, int i, SubscriptionPlan[] subscriptionPlanArr) {
        try {
            iNetworkPolicyListener.onSubscriptionPlansChanged(i, subscriptionPlanArr);
        } catch (RemoteException unused) {
        }
    }

    public final void dispatchBlockedReasonChanged(INetworkPolicyListener iNetworkPolicyListener, int i, int i2, int i3) {
        try {
            iNetworkPolicyListener.onBlockedReasonChanged(i, i2, i3);
        } catch (RemoteException unused) {
        }
    }

    public void handleUidChanged(UidStateCallbackInfo uidStateCallbackInfo) {
        int i;
        int i2;
        long j;
        int i3;
        boolean updateUidStateUL;
        Trace.traceBegin(2097152L, "onUidStateChanged");
        try {
            synchronized (this.mUidRulesFirstLock) {
                synchronized (this.mUidStateCallbackInfos) {
                    i = uidStateCallbackInfo.uid;
                    i2 = uidStateCallbackInfo.procState;
                    j = uidStateCallbackInfo.procStateSeq;
                    i3 = uidStateCallbackInfo.capability;
                    uidStateCallbackInfo.isPending = false;
                }
                this.mLogger.uidStateChanged(i, i2, j, i3);
                updateUidStateUL = updateUidStateUL(i, i2, j, i3);
                this.mActivityManagerInternal.notifyNetworkPolicyRulesUpdated(i, j);
            }
            if (updateUidStateUL) {
                updateNetworkStats(i, NetworkPolicyManager.isProcStateAllowedWhileOnRestrictBackground(i2));
            }
        } finally {
            Trace.traceEnd(2097152L);
        }
    }

    public void handleUidGone(int i) {
        boolean removeUidStateUL;
        Trace.traceBegin(2097152L, "onUidGone");
        try {
            synchronized (this.mUidRulesFirstLock) {
                removeUidStateUL = removeUidStateUL(i);
            }
            if (removeUidStateUL) {
                updateNetworkStats(i, false);
            }
        } finally {
            Trace.traceEnd(2097152L);
        }
    }

    public final void broadcastRestrictBackgroundChanged(int i, Boolean bool) {
        String[] packagesForUid = this.mContext.getPackageManager().getPackagesForUid(i);
        if (packagesForUid != null) {
            int userId = UserHandle.getUserId(i);
            for (String str : packagesForUid) {
                Intent intent = new Intent("android.net.conn.RESTRICT_BACKGROUND_CHANGED");
                intent.setPackage(str);
                intent.setFlags(1073741824);
                this.mContext.sendBroadcastAsUser(intent, UserHandle.of(userId));
            }
        }
    }

    /* loaded from: classes2.dex */
    public static final class IfaceQuotas {
        public final String iface;
        public final long limit;
        public final long warning;

        public IfaceQuotas(String str, long j, long j2) {
            this.iface = str;
            this.warning = j;
            this.limit = j2;
        }
    }

    public final void setInterfaceQuotasAsync(String str, long j, long j2) {
        this.mHandler.obtainMessage(10, new IfaceQuotas(str, j, j2)).sendToTarget();
    }

    public final void setInterfaceLimit(String str, long j) {
        try {
            this.mNetworkManager.setInterfaceQuota(str, j);
        } catch (RemoteException unused) {
        } catch (IllegalStateException e) {
            Log.wtf("NetworkPolicy", "problem setting interface quota", e);
        }
    }

    public final void removeInterfaceQuotasAsync(String str) {
        this.mHandler.obtainMessage(11, str).sendToTarget();
    }

    public final void removeInterfaceLimit(String str) {
        try {
            this.mNetworkManager.removeInterfaceQuota(str);
        } catch (RemoteException unused) {
        } catch (IllegalStateException e) {
            Log.wtf("NetworkPolicy", "problem removing interface quota", e);
        }
    }

    public final void setMeteredNetworkDenylist(int i, boolean z) {
        if (LOGV) {
            Slog.v("NetworkPolicy", "setMeteredNetworkDenylist " + i + ": " + z);
        }
        try {
            this.mNetworkManager.setUidOnMeteredNetworkDenylist(i, z);
            this.mLogger.meteredDenylistChanged(i, z);
            if (Process.isApplicationUid(i)) {
                int sdkSandboxUid = Process.toSdkSandboxUid(i);
                this.mNetworkManager.setUidOnMeteredNetworkDenylist(sdkSandboxUid, z);
                this.mLogger.meteredDenylistChanged(sdkSandboxUid, z);
            }
        } catch (RemoteException unused) {
        } catch (IllegalStateException e) {
            Log.wtf("NetworkPolicy", "problem setting denylist (" + z + ") rules for " + i, e);
        }
    }

    public final void setMeteredNetworkAllowlist(int i, boolean z) {
        if (LOGV) {
            Slog.v("NetworkPolicy", "setMeteredNetworkAllowlist " + i + ": " + z);
        }
        try {
            this.mNetworkManager.setUidOnMeteredNetworkAllowlist(i, z);
            this.mLogger.meteredAllowlistChanged(i, z);
            if (Process.isApplicationUid(i)) {
                int sdkSandboxUid = Process.toSdkSandboxUid(i);
                this.mNetworkManager.setUidOnMeteredNetworkAllowlist(sdkSandboxUid, z);
                this.mLogger.meteredAllowlistChanged(sdkSandboxUid, z);
            }
        } catch (RemoteException unused) {
        } catch (IllegalStateException e) {
            Log.wtf("NetworkPolicy", "problem setting allowlist (" + z + ") rules for " + i, e);
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final void setUidFirewallRulesUL(int i, SparseIntArray sparseIntArray, int i2) {
        if (sparseIntArray != null) {
            setUidFirewallRulesUL(i, sparseIntArray);
        }
        if (i2 != 0) {
            enableFirewallChainUL(i, i2 == 1);
        }
    }

    public final void addSdkSandboxUidsIfNeeded(SparseIntArray sparseIntArray) {
        int size = sparseIntArray.size();
        SparseIntArray sparseIntArray2 = new SparseIntArray();
        for (int i = 0; i < size; i++) {
            int keyAt = sparseIntArray.keyAt(i);
            int valueAt = sparseIntArray.valueAt(i);
            if (Process.isApplicationUid(keyAt)) {
                sparseIntArray2.put(Process.toSdkSandboxUid(keyAt), valueAt);
            }
        }
        for (int i2 = 0; i2 < sparseIntArray2.size(); i2++) {
            sparseIntArray.put(sparseIntArray2.keyAt(i2), sparseIntArray2.valueAt(i2));
        }
    }

    public final void setUidFirewallRulesUL(int i, SparseIntArray sparseIntArray) {
        addSdkSandboxUidsIfNeeded(sparseIntArray);
        try {
            int size = sparseIntArray.size();
            int[] iArr = new int[size];
            int[] iArr2 = new int[size];
            for (int i2 = size - 1; i2 >= 0; i2--) {
                iArr[i2] = sparseIntArray.keyAt(i2);
                iArr2[i2] = sparseIntArray.valueAt(i2);
            }
            this.mNetworkManager.setFirewallUidRules(i, iArr, iArr2);
            this.mLogger.firewallRulesChanged(i, iArr, iArr2);
        } catch (RemoteException unused) {
        } catch (IllegalStateException e) {
            Log.wtf("NetworkPolicy", "problem setting firewall uid rules", e);
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final void setUidFirewallRuleUL(int i, int i2, int i3) {
        if (Trace.isTagEnabled(2097152L)) {
            Trace.traceBegin(2097152L, "setUidFirewallRuleUL: " + i + "/" + i2 + "/" + i3);
        }
        try {
            if (i == 1) {
                this.mUidFirewallDozableRules.put(i2, i3);
            } else if (i == 2) {
                this.mUidFirewallStandbyRules.put(i2, i3);
            } else if (i == 3) {
                this.mUidFirewallPowerSaveRules.put(i2, i3);
            } else if (i == 4) {
                this.mUidFirewallRestrictedModeRules.put(i2, i3);
            } else if (i == 5) {
                this.mUidFirewallLowPowerStandbyModeRules.put(i2, i3);
            }
            try {
                this.mNetworkManager.setFirewallUidRule(i, i2, i3);
                this.mLogger.uidFirewallRuleChanged(i, i2, i3);
                if (Process.isApplicationUid(i2)) {
                    int sdkSandboxUid = Process.toSdkSandboxUid(i2);
                    this.mNetworkManager.setFirewallUidRule(i, sdkSandboxUid, i3);
                    this.mLogger.uidFirewallRuleChanged(i, sdkSandboxUid, i3);
                }
            } catch (RemoteException unused) {
            } catch (IllegalStateException e) {
                Log.wtf("NetworkPolicy", "problem setting firewall uid rules", e);
            }
            Trace.traceEnd(2097152L);
        } catch (Throwable th) {
            Trace.traceEnd(2097152L);
            throw th;
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final void enableFirewallChainUL(int i, boolean z) {
        if (this.mFirewallChainStates.indexOfKey(i) < 0 || this.mFirewallChainStates.get(i) != z) {
            this.mFirewallChainStates.put(i, z);
            try {
                this.mNetworkManager.setFirewallChainEnabled(i, z);
                this.mLogger.firewallChainEnabled(i, z);
            } catch (RemoteException unused) {
            } catch (IllegalStateException e) {
                Log.wtf("NetworkPolicy", "problem enable firewall chain", e);
            }
        }
    }

    public final void resetUidFirewallRules(int i) {
        try {
            this.mNetworkManager.setFirewallUidRule(1, i, 0);
            this.mNetworkManager.setFirewallUidRule(2, i, 0);
            this.mNetworkManager.setFirewallUidRule(3, i, 0);
            this.mNetworkManager.setFirewallUidRule(4, i, 0);
            this.mNetworkManager.setFirewallUidRule(5, i, 0);
            this.mNetworkManager.setUidOnMeteredNetworkAllowlist(i, false);
            this.mLogger.meteredAllowlistChanged(i, false);
            this.mNetworkManager.setUidOnMeteredNetworkDenylist(i, false);
            this.mLogger.meteredDenylistChanged(i, false);
        } catch (RemoteException unused) {
        } catch (IllegalStateException e) {
            Log.wtf("NetworkPolicy", "problem resetting firewall uid rules for " + i, e);
        }
        if (Process.isApplicationUid(i)) {
            resetUidFirewallRules(Process.toSdkSandboxUid(i));
        }
    }

    @Deprecated
    public final long getTotalBytes(NetworkTemplate networkTemplate, long j, long j2) {
        if (this.mStatsCallback.isAnyCallbackReceived()) {
            return this.mDeps.getNetworkTotalBytes(networkTemplate, j, j2);
        }
        return 0L;
    }

    public final boolean isBandwidthControlEnabled() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            boolean isBandwidthControlEnabled = this.mNetworkManager.isBandwidthControlEnabled();
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return isBandwidthControlEnabled;
        } catch (RemoteException unused) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return false;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public static Intent buildSnoozeWarningIntent(NetworkTemplate networkTemplate, String str) {
        Intent intent = new Intent("com.android.server.net.action.SNOOZE_WARNING");
        intent.addFlags(268435456);
        intent.putExtra("android.net.NETWORK_TEMPLATE", (Parcelable) networkTemplate);
        intent.setPackage(str);
        return intent;
    }

    public static Intent buildSnoozeRapidIntent(NetworkTemplate networkTemplate, String str) {
        Intent intent = new Intent("com.android.server.net.action.SNOOZE_RAPID");
        intent.addFlags(268435456);
        intent.putExtra("android.net.NETWORK_TEMPLATE", (Parcelable) networkTemplate);
        intent.setPackage(str);
        return intent;
    }

    public static Intent buildNetworkOverLimitIntent(Resources resources, NetworkTemplate networkTemplate) {
        Intent intent = new Intent();
        intent.setComponent(ComponentName.unflattenFromString(resources.getString(17039967)));
        intent.addFlags(268435456);
        intent.putExtra("android.net.NETWORK_TEMPLATE", (Parcelable) networkTemplate);
        return intent;
    }

    public static Intent buildViewDataUsageIntent(Resources resources, NetworkTemplate networkTemplate) {
        Intent intent = new Intent();
        intent.setComponent(ComponentName.unflattenFromString(resources.getString(17039865)));
        intent.addFlags(268435456);
        intent.putExtra("android.net.NETWORK_TEMPLATE", (Parcelable) networkTemplate);
        return intent;
    }

    @VisibleForTesting
    public void addIdleHandler(MessageQueue.IdleHandler idleHandler) {
        this.mHandler.getLooper().getQueue().addIdleHandler(idleHandler);
    }

    @GuardedBy({"mUidRulesFirstLock"})
    @VisibleForTesting
    public void updateRestrictBackgroundByLowPowerModeUL(PowerSaveState powerSaveState) {
        boolean z;
        boolean z2;
        boolean z3 = this.mRestrictBackgroundLowPowerMode;
        boolean z4 = powerSaveState.batterySaverEnabled;
        if (z3 == z4) {
            return;
        }
        this.mRestrictBackgroundLowPowerMode = z4;
        boolean z5 = this.mRestrictBackgroundChangedInBsm;
        if (this.mRestrictBackgroundLowPowerMode) {
            z = !this.mRestrictBackground;
            this.mRestrictBackgroundBeforeBsm = this.mRestrictBackground;
            z2 = false;
        } else {
            z = !this.mRestrictBackgroundChangedInBsm;
            z4 = this.mRestrictBackgroundBeforeBsm;
            z2 = z5;
        }
        if (z) {
            setRestrictBackgroundUL(z4, "low_power");
        }
        this.mRestrictBackgroundChangedInBsm = z2;
    }

    public static <T> void collectKeys(SparseArray<T> sparseArray, SparseBooleanArray sparseBooleanArray) {
        int size = sparseArray.size();
        for (int i = 0; i < size; i++) {
            sparseBooleanArray.put(sparseArray.keyAt(i), true);
        }
    }

    public void factoryReset(String str) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.NETWORK_SETTINGS", "NetworkPolicy");
        if (this.mUserManager.hasUserRestriction("no_network_reset")) {
            return;
        }
        NetworkPolicy[] networkPolicies = getNetworkPolicies(this.mContext.getOpPackageName());
        NetworkTemplate buildTemplateCarrierMetered = str != null ? buildTemplateCarrierMetered(str) : null;
        NetworkTemplate build = str != null ? new NetworkTemplate.Builder(1).setSubscriberIds(Set.of(str)).setMeteredness(1).build() : null;
        for (NetworkPolicy networkPolicy : networkPolicies) {
            if (networkPolicy.template.equals(buildTemplateCarrierMetered) || networkPolicy.template.equals(build)) {
                networkPolicy.limitBytes = -1L;
                networkPolicy.inferred = false;
                networkPolicy.clearSnooze();
            }
        }
        setNetworkPolicies(networkPolicies);
        setRestrictBackground(false);
        if (this.mUserManager.hasUserRestriction("no_control_apps")) {
            return;
        }
        for (int i : getUidsWithPolicy(1)) {
            setUidPolicy(i, 0);
        }
    }

    public boolean isUidNetworkingBlocked(int i, boolean z) {
        int i2;
        long time = this.mStatLogger.getTime();
        this.mContext.enforceCallingOrSelfPermission("android.permission.OBSERVE_NETWORK_POLICY", "NetworkPolicy");
        synchronized (this.mUidBlockedState) {
            UidBlockedState uidBlockedState = this.mUidBlockedState.get(i);
            i2 = uidBlockedState == null ? 0 : uidBlockedState.effectiveBlockedReasons;
            if (!z) {
                i2 &= GnssNative.GNSS_AIDING_TYPE_ALL;
            }
            this.mLogger.networkBlocked(i, uidBlockedState);
        }
        this.mStatLogger.logDurationStat(1, time);
        return i2 != 0;
    }

    public boolean isUidRestrictedOnMeteredNetworks(int i) {
        boolean z;
        this.mContext.enforceCallingOrSelfPermission("android.permission.OBSERVE_NETWORK_POLICY", "NetworkPolicy");
        synchronized (this.mUidBlockedState) {
            UidBlockedState uidBlockedState = this.mUidBlockedState.get(i);
            z = ((uidBlockedState == null ? 0 : uidBlockedState.effectiveBlockedReasons) & (-65536)) != 0;
        }
        return z;
    }

    /* loaded from: classes2.dex */
    public class NetworkPolicyManagerInternalImpl extends NetworkPolicyManagerInternal {
        public NetworkPolicyManagerInternalImpl() {
        }

        /* JADX WARN: Code restructure failed: missing block: B:10:0x001b, code lost:
            r6 = r5.this$0.mNetworkPoliciesSecondLock;
         */
        /* JADX WARN: Code restructure failed: missing block: B:11:0x001f, code lost:
            monitor-enter(r6);
         */
        /* JADX WARN: Code restructure failed: missing block: B:12:0x0020, code lost:
            r5.this$0.writePolicyAL();
         */
        /* JADX WARN: Code restructure failed: missing block: B:13:0x0025, code lost:
            monitor-exit(r6);
         */
        @Override // com.android.server.net.NetworkPolicyManagerInternal
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void resetUserState(int i) {
            synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                boolean z = true;
                boolean removeUserStateUL = NetworkPolicyManagerService.this.removeUserStateUL(i, false, true);
                if (!NetworkPolicyManagerService.this.addDefaultRestrictBackgroundAllowlistUidsUL(i) && !removeUserStateUL) {
                    z = false;
                }
            }
        }

        @Override // com.android.server.net.NetworkPolicyManagerInternal
        public void onTempPowerSaveWhitelistChange(int i, boolean z, int i2, String str) {
            synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                if (NetworkPolicyManagerService.this.mSystemReady) {
                    NetworkPolicyManagerService.this.mLogger.tempPowerSaveWlChanged(i, z, i2, str);
                    if (z) {
                        NetworkPolicyManagerService.this.mPowerSaveTempWhitelistAppIds.put(i, true);
                    } else {
                        NetworkPolicyManagerService.this.mPowerSaveTempWhitelistAppIds.delete(i);
                    }
                    NetworkPolicyManagerService.this.updateRulesForTempWhitelistChangeUL(i);
                }
            }
        }

        @Override // com.android.server.net.NetworkPolicyManagerInternal
        public long getSubscriptionOpportunisticQuota(Network network, int i) {
            long j;
            float f;
            float f2;
            synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                NetworkPolicyManagerService networkPolicyManagerService = NetworkPolicyManagerService.this;
                j = networkPolicyManagerService.mSubscriptionOpportunisticQuota.get(networkPolicyManagerService.getSubIdLocked(network), -1L);
            }
            if (j == -1) {
                return -1L;
            }
            if (i == 1) {
                f = (float) j;
                f2 = Settings.Global.getFloat(NetworkPolicyManagerService.this.mContext.getContentResolver(), "netpolicy_quota_frac_jobs", 0.5f);
            } else if (i != 2) {
                return -1L;
            } else {
                f = (float) j;
                f2 = Settings.Global.getFloat(NetworkPolicyManagerService.this.mContext.getContentResolver(), "netpolicy_quota_frac_multipath", 0.5f);
            }
            return f * f2;
        }

        @Override // com.android.server.net.NetworkPolicyManagerInternal
        public void onAdminDataAvailable() {
            NetworkPolicyManagerService.this.mAdminDataAvailableLatch.countDown();
        }

        @Override // com.android.server.net.NetworkPolicyManagerInternal
        public void setAppIdleWhitelist(int i, boolean z) {
            NetworkPolicyManagerService.this.setAppIdleWhitelist(i, z);
        }

        @Override // com.android.server.net.NetworkPolicyManagerInternal
        public void setMeteredRestrictedPackages(Set<String> set, int i) {
            NetworkPolicyManagerService.this.setMeteredRestrictedPackagesInternal(set, i);
        }

        @Override // com.android.server.net.NetworkPolicyManagerInternal
        public void setMeteredRestrictedPackagesAsync(Set<String> set, int i) {
            NetworkPolicyManagerService.this.mHandler.obtainMessage(17, i, 0, set).sendToTarget();
        }

        @Override // com.android.server.net.NetworkPolicyManagerInternal
        public void setLowPowerStandbyActive(boolean z) {
            Trace.traceBegin(2097152L, "setLowPowerStandbyActive");
            try {
                synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                    if (NetworkPolicyManagerService.this.mLowPowerStandbyActive == z) {
                        return;
                    }
                    NetworkPolicyManagerService.this.mLowPowerStandbyActive = z;
                    synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                        if (NetworkPolicyManagerService.this.mSystemReady) {
                            NetworkPolicyManagerService.this.forEachUid("updateRulesForRestrictPower", new IntConsumer() { // from class: com.android.server.net.NetworkPolicyManagerService$NetworkPolicyManagerInternalImpl$$ExternalSyntheticLambda0
                                @Override // java.util.function.IntConsumer
                                public final void accept(int i) {
                                    NetworkPolicyManagerService.NetworkPolicyManagerInternalImpl.this.lambda$setLowPowerStandbyActive$0(i);
                                }
                            });
                            NetworkPolicyManagerService.this.updateRulesForLowPowerStandbyUL();
                        }
                    }
                }
            } finally {
                Trace.traceEnd(2097152L);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setLowPowerStandbyActive$0(int i) {
            NetworkPolicyManagerService.this.lambda$updateRulesForRestrictPowerUL$5(i);
        }

        @Override // com.android.server.net.NetworkPolicyManagerInternal
        public void setLowPowerStandbyAllowlist(int[] iArr) {
            synchronized (NetworkPolicyManagerService.this.mUidRulesFirstLock) {
                SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
                for (int i = 0; i < NetworkPolicyManagerService.this.mLowPowerStandbyAllowlistUids.size(); i++) {
                    int keyAt = NetworkPolicyManagerService.this.mLowPowerStandbyAllowlistUids.keyAt(i);
                    if (!ArrayUtils.contains(iArr, keyAt)) {
                        sparseBooleanArray.put(keyAt, true);
                    }
                }
                for (int i2 = 0; i2 < sparseBooleanArray.size(); i2++) {
                    NetworkPolicyManagerService.this.mLowPowerStandbyAllowlistUids.delete(sparseBooleanArray.keyAt(i2));
                }
                for (int i3 : iArr) {
                    if (NetworkPolicyManagerService.this.mLowPowerStandbyAllowlistUids.indexOfKey(i3) < 0) {
                        sparseBooleanArray.append(i3, true);
                        NetworkPolicyManagerService.this.mLowPowerStandbyAllowlistUids.append(i3, true);
                    }
                }
                if (NetworkPolicyManagerService.this.mLowPowerStandbyActive) {
                    synchronized (NetworkPolicyManagerService.this.mNetworkPoliciesSecondLock) {
                        if (NetworkPolicyManagerService.this.mSystemReady) {
                            for (int i4 = 0; i4 < sparseBooleanArray.size(); i4++) {
                                int keyAt2 = sparseBooleanArray.keyAt(i4);
                                NetworkPolicyManagerService.this.lambda$updateRulesForRestrictPowerUL$5(keyAt2);
                                NetworkPolicyManagerService.this.updateRuleForLowPowerStandbyUL(keyAt2);
                            }
                        }
                    }
                }
            }
        }
    }

    public final void setMeteredRestrictedPackagesInternal(Set<String> set, int i) {
        synchronized (this.mUidRulesFirstLock) {
            ArraySet arraySet = new ArraySet();
            for (String str : set) {
                int uidForPackage = getUidForPackage(str, i);
                if (uidForPackage >= 0) {
                    arraySet.add(Integer.valueOf(uidForPackage));
                }
            }
            this.mMeteredRestrictedUids.put(i, arraySet);
            handleRestrictedPackagesChangeUL(this.mMeteredRestrictedUids.get(i), arraySet);
            this.mLogger.meteredRestrictedPkgsChanged(arraySet);
        }
    }

    public final int getUidForPackage(String str, int i) {
        try {
            return this.mContext.getPackageManager().getPackageUidAsUser(str, 4202496, i);
        } catch (PackageManager.NameNotFoundException unused) {
            return -1;
        }
    }

    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final int getSubIdLocked(Network network) {
        return this.mNetIdToSubId.get(network.getNetId(), -1);
    }

    @GuardedBy({"mNetworkPoliciesSecondLock"})
    public final SubscriptionPlan getPrimarySubscriptionPlanLocked(int i) {
        SubscriptionPlan[] subscriptionPlanArr = this.mSubscriptionPlans.get(i);
        if (ArrayUtils.isEmpty(subscriptionPlanArr)) {
            return null;
        }
        int length = subscriptionPlanArr.length;
        for (int i2 = 0; i2 < length; i2++) {
            SubscriptionPlan subscriptionPlan = subscriptionPlanArr[i2];
            if (subscriptionPlan.getCycleRule().isRecurring() || subscriptionPlan.cycleIterator().next().contains((Range<ZonedDateTime>) ZonedDateTime.now(this.mClock))) {
                return subscriptionPlan;
            }
        }
        return null;
    }

    public final void waitForAdminData() {
        if (this.mContext.getPackageManager().hasSystemFeature("android.software.device_admin")) {
            ConcurrentUtils.waitForCountDownNoInterrupt(this.mAdminDataAvailableLatch, 10000L, "Wait for admin data");
        }
    }

    public final void handleRestrictedPackagesChangeUL(Set<Integer> set, Set<Integer> set2) {
        if (this.mNetworkManagerReady) {
            if (set == null) {
                for (Integer num : set2) {
                    lambda$updateRulesForRestrictBackgroundUL$6(num.intValue());
                }
                return;
            }
            for (Integer num2 : set) {
                int intValue = num2.intValue();
                if (!set2.contains(Integer.valueOf(intValue))) {
                    lambda$updateRulesForRestrictBackgroundUL$6(intValue);
                }
            }
            for (Integer num3 : set2) {
                int intValue2 = num3.intValue();
                if (!set.contains(Integer.valueOf(intValue2))) {
                    lambda$updateRulesForRestrictBackgroundUL$6(intValue2);
                }
            }
        }
    }

    @GuardedBy({"mUidRulesFirstLock"})
    public final boolean isRestrictedByAdminUL(int i) {
        Set<Integer> set = this.mMeteredRestrictedUids.get(UserHandle.getUserId(i));
        return set != null && set.contains(Integer.valueOf(i));
    }

    public static boolean getBooleanDefeatingNullable(PersistableBundle persistableBundle, String str, boolean z) {
        return persistableBundle != null ? persistableBundle.getBoolean(str, z) : z;
    }

    public static UidBlockedState getOrCreateUidBlockedStateForUid(SparseArray<UidBlockedState> sparseArray, int i) {
        UidBlockedState uidBlockedState = sparseArray.get(i);
        if (uidBlockedState == null) {
            UidBlockedState uidBlockedState2 = new UidBlockedState();
            sparseArray.put(i, uidBlockedState2);
            return uidBlockedState2;
        }
        return uidBlockedState;
    }

    public final int getEffectiveBlockedReasons(int i) {
        int i2;
        synchronized (this.mUidBlockedState) {
            UidBlockedState uidBlockedState = this.mUidBlockedState.get(i);
            i2 = uidBlockedState == null ? 0 : uidBlockedState.effectiveBlockedReasons;
        }
        return i2;
    }

    public final int getBlockedReasons(int i) {
        int i2;
        synchronized (this.mUidBlockedState) {
            UidBlockedState uidBlockedState = this.mUidBlockedState.get(i);
            i2 = uidBlockedState == null ? 0 : uidBlockedState.blockedReasons;
        }
        return i2;
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static final class UidBlockedState {
        public int allowedReasons;
        public int blockedReasons;
        public int effectiveBlockedReasons;
        public static final int[] BLOCKED_REASONS = {1, 2, 4, 8, 32, 65536, IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES, 262144};
        public static final int[] ALLOWED_REASONS = {1, 2, 32, 4, 8, 16, 64, 65536, IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES, 262144};

        public static int getAllowedReasonsForProcState(int i) {
            if (i > 5) {
                return 0;
            }
            return i <= 3 ? 262178 : 262146;
        }

        @VisibleForTesting
        public static int getEffectiveBlockedReasons(int i, int i2) {
            if (i == 0) {
                return i;
            }
            if ((i2 & 1) != 0) {
                i &= -65536;
            }
            if ((131072 & i2) != 0) {
                i &= GnssNative.GNSS_AIDING_TYPE_ALL;
            }
            if ((i2 & 2) != 0) {
                i = i & (-2) & (-3) & (-5);
            }
            if ((262144 & i2) != 0) {
                i = i & (-65537) & (-131073);
            }
            if ((i2 & 32) != 0) {
                i &= -33;
            }
            if ((i2 & 4) != 0) {
                i = i & (-2) & (-3) & (-5);
            }
            if ((i2 & 8) != 0) {
                i = i & (-2) & (-5);
            }
            if ((i2 & 16) != 0) {
                i &= -9;
            }
            if ((65536 & i2) != 0) {
                i &= -65537;
            }
            return (i2 & 64) != 0 ? i & (-33) : i;
        }

        public UidBlockedState(int i, int i2, int i3) {
            this.blockedReasons = i;
            this.allowedReasons = i2;
            this.effectiveBlockedReasons = i3;
        }

        public UidBlockedState() {
            this(0, 0, 0);
        }

        public void updateEffectiveBlockedReasons() {
            if (NetworkPolicyManagerService.LOGV && this.blockedReasons == 0) {
                Log.v("NetworkPolicy", "updateEffectiveBlockedReasons(): no blocked reasons");
            }
            this.effectiveBlockedReasons = getEffectiveBlockedReasons(this.blockedReasons, this.allowedReasons);
            if (NetworkPolicyManagerService.LOGV) {
                Log.v("NetworkPolicy", "updateEffectiveBlockedReasons(): blockedReasons=" + Integer.toBinaryString(this.blockedReasons) + ", effectiveReasons=" + Integer.toBinaryString(this.effectiveBlockedReasons));
            }
        }

        public String toString() {
            return toString(this.blockedReasons, this.allowedReasons, this.effectiveBlockedReasons);
        }

        public static String toString(int i, int i2, int i3) {
            return "{blocked=" + blockedReasonsToString(i) + ",allowed=" + allowedReasonsToString(i2) + ",effective=" + blockedReasonsToString(i3) + "}";
        }

        public static String blockedReasonToString(int i) {
            if (i != 0) {
                if (i != 1) {
                    if (i != 2) {
                        if (i != 4) {
                            if (i != 8) {
                                if (i != 32) {
                                    if (i != 65536) {
                                        if (i != 131072) {
                                            if (i != 262144) {
                                                Slog.wtfStack("NetworkPolicy", "Unknown blockedReason: " + i);
                                                return String.valueOf(i);
                                            }
                                            return "METERED_ADMIN_DISABLED";
                                        }
                                        return "METERED_USER_RESTRICTED";
                                    }
                                    return "DATA_SAVER";
                                }
                                return "LOW_POWER_STANDBY";
                            }
                            return "RESTRICTED_MODE";
                        }
                        return "APP_STANDBY";
                    }
                    return "DOZE";
                }
                return "BATTERY_SAVER";
            }
            return "NONE";
        }

        public static String allowedReasonToString(int i) {
            if (i != 0) {
                if (i != 1) {
                    if (i != 2) {
                        if (i != 4) {
                            if (i != 8) {
                                if (i != 16) {
                                    if (i != 32) {
                                        if (i != 64) {
                                            if (i != 65536) {
                                                if (i != 131072) {
                                                    if (i != 262144) {
                                                        Slog.wtfStack("NetworkPolicy", "Unknown allowedReason: " + i);
                                                        return String.valueOf(i);
                                                    }
                                                    return "METERED_FOREGROUND";
                                                }
                                                return "METERED_SYSTEM";
                                            }
                                            return "METERED_USER_EXEMPTED";
                                        }
                                        return "LOW_POWER_STANDBY_ALLOWLIST";
                                    }
                                    return "TOP";
                                }
                                return "RESTRICTED_MODE_PERMISSIONS";
                            }
                            return "POWER_SAVE_EXCEPT_IDLE_ALLOWLIST";
                        }
                        return "POWER_SAVE_ALLOWLIST";
                    }
                    return "FOREGROUND";
                }
                return "SYSTEM";
            }
            return "NONE";
        }

        public static String blockedReasonsToString(int i) {
            int i2 = 0;
            if (i == 0) {
                return blockedReasonToString(0);
            }
            StringBuilder sb = new StringBuilder();
            int[] iArr = BLOCKED_REASONS;
            int length = iArr.length;
            while (true) {
                if (i2 >= length) {
                    break;
                }
                int i3 = iArr[i2];
                if ((i & i3) != 0) {
                    sb.append(sb.length() != 0 ? "|" : "");
                    sb.append(blockedReasonToString(i3));
                    i &= ~i3;
                }
                i2++;
            }
            if (i != 0) {
                sb.append(sb.length() != 0 ? "|" : "");
                sb.append(String.valueOf(i));
                Slog.wtfStack("NetworkPolicy", "Unknown blockedReasons: " + i);
            }
            return sb.toString();
        }

        public static String allowedReasonsToString(int i) {
            int i2 = 0;
            if (i == 0) {
                return allowedReasonToString(0);
            }
            StringBuilder sb = new StringBuilder();
            int[] iArr = ALLOWED_REASONS;
            int length = iArr.length;
            while (true) {
                if (i2 >= length) {
                    break;
                }
                int i3 = iArr[i2];
                if ((i & i3) != 0) {
                    sb.append(sb.length() != 0 ? "|" : "");
                    sb.append(allowedReasonToString(i3));
                    i &= ~i3;
                }
                i2++;
            }
            if (i != 0) {
                sb.append(sb.length() != 0 ? "|" : "");
                sb.append(String.valueOf(i));
                Slog.wtfStack("NetworkPolicy", "Unknown allowedReasons: " + i);
            }
            return sb.toString();
        }

        public void copyFrom(UidBlockedState uidBlockedState) {
            this.blockedReasons = uidBlockedState.blockedReasons;
            this.allowedReasons = uidBlockedState.allowedReasons;
            this.effectiveBlockedReasons = uidBlockedState.effectiveBlockedReasons;
        }

        /* JADX WARN: Code restructure failed: missing block: B:26:0x0040, code lost:
            if ((r0 & 262144) != 0) goto L19;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public int deriveUidRules() {
            int i = this.effectiveBlockedReasons;
            int i2 = (i & 8) != 0 ? 1024 : 0;
            if ((i & 39) != 0) {
                i2 |= 64;
            } else if ((this.blockedReasons & 39) != 0) {
                i2 |= 32;
            }
            if ((i & 393216) != 0) {
                i2 |= 4;
            } else {
                int i3 = this.blockedReasons;
                if ((131072 & i3) == 0 || (this.allowedReasons & 262144) == 0) {
                    if ((i3 & 65536) != 0) {
                        int i4 = this.allowedReasons;
                        if ((65536 & i4) != 0) {
                            i2 |= 32;
                        }
                    }
                }
                i2 |= 2;
            }
            if (NetworkPolicyManagerService.LOGV) {
                Slog.v("NetworkPolicy", "uidBlockedState=" + this + " -> uidRule=" + NetworkPolicyManager.uidRulesToString(i2));
            }
            return i2;
        }
    }

    /* loaded from: classes2.dex */
    public static class NotificationId {
        public final int mId;
        public final String mTag;

        public NotificationId(NetworkPolicy networkPolicy, int i) {
            this.mTag = buildNotificationTag(networkPolicy, i);
            this.mId = i;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof NotificationId) {
                return Objects.equals(this.mTag, ((NotificationId) obj).mTag);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.mTag);
        }

        public final String buildNotificationTag(NetworkPolicy networkPolicy, int i) {
            return "NetworkPolicy:" + networkPolicy.template.hashCode() + com.android.internal.util.jobs.XmlUtils.STRING_ARRAY_SEPARATOR + i;
        }

        public String getTag() {
            return this.mTag;
        }

        public int getId() {
            return this.mId;
        }
    }
}
