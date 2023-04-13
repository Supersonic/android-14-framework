package com.android.server.connectivity;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkIdentity;
import android.net.NetworkPolicy;
import android.net.NetworkPolicyManager;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.NetworkTemplate;
import android.net.TelephonyNetworkSpecifier;
import android.net.Uri;
import android.os.BestClock;
import android.os.Handler;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DebugUtils;
import android.util.Log;
import android.util.Range;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.LocalServices;
import com.android.server.connectivity.MultipathPolicyTracker;
import com.android.server.net.NetworkPolicyManagerInternal;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class MultipathPolicyTracker {
    public static String TAG = "MultipathPolicyTracker";
    public ConnectivityManager mCM;
    public final Clock mClock;
    public final ConfigChangeReceiver mConfigChangeReceiver;
    public final Context mContext;
    public final Dependencies mDeps;
    public final Handler mHandler;
    public ConnectivityManager.NetworkCallback mMobileNetworkCallback;
    public final ConcurrentHashMap<Network, MultipathTracker> mMultipathTrackers;
    public NetworkPolicyManager mNPM;
    public NetworkPolicyManager.Listener mPolicyListener;
    public final ContentResolver mResolver;
    @VisibleForTesting
    final ContentObserver mSettingsObserver;
    public NetworkStatsManager mStatsManager;
    public final Context mUserAllContext;

    /* loaded from: classes.dex */
    public static class Dependencies {
        public Clock getClock() {
            return new BestClock(ZoneOffset.UTC, new Clock[]{SystemClock.currentNetworkTimeClock(), Clock.systemUTC()});
        }
    }

    public MultipathPolicyTracker(Context context, Handler handler) {
        this(context, handler, new Dependencies());
    }

    public MultipathPolicyTracker(Context context, Handler handler, Dependencies dependencies) {
        this.mMultipathTrackers = new ConcurrentHashMap<>();
        this.mContext = context;
        this.mUserAllContext = context.createContextAsUser(UserHandle.ALL, 0);
        this.mHandler = handler;
        this.mClock = dependencies.getClock();
        this.mDeps = dependencies;
        this.mResolver = context.getContentResolver();
        this.mSettingsObserver = new SettingsObserver(handler);
        this.mConfigChangeReceiver = new ConfigChangeReceiver();
    }

    public void start() {
        this.mCM = (ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class);
        this.mNPM = (NetworkPolicyManager) this.mContext.getSystemService(NetworkPolicyManager.class);
        this.mStatsManager = (NetworkStatsManager) this.mContext.getSystemService(NetworkStatsManager.class);
        registerTrackMobileCallback();
        registerNetworkPolicyListener();
        this.mResolver.registerContentObserver(Settings.Global.getUriFor("network_default_daily_multipath_quota_bytes"), false, this.mSettingsObserver);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
        this.mUserAllContext.registerReceiver(this.mConfigChangeReceiver, intentFilter, null, this.mHandler);
    }

    public Integer getMultipathPreference(Network network) {
        MultipathTracker multipathTracker;
        if (network == null || (multipathTracker = this.mMultipathTrackers.get(network)) == null) {
            return null;
        }
        return Integer.valueOf(multipathTracker.getMultipathPreference());
    }

    /* loaded from: classes.dex */
    public class MultipathTracker {
        public volatile long mMultipathBudget;
        public NetworkCapabilities mNetworkCapabilities;
        public final NetworkTemplate mNetworkTemplate;
        public long mQuota;
        public final NetworkStatsManager mStatsManager;
        public final int mSubId;
        public final NetworkStatsManager.UsageCallback mUsageCallback;
        public boolean mUsageCallbackRegistered = false;
        public final Network network;
        public final String subscriberId;

        public MultipathTracker(final Network network, NetworkCapabilities networkCapabilities) {
            this.network = network;
            this.mNetworkCapabilities = new NetworkCapabilities(networkCapabilities);
            NetworkSpecifier networkSpecifier = networkCapabilities.getNetworkSpecifier();
            if (networkSpecifier instanceof TelephonyNetworkSpecifier) {
                int subscriptionId = ((TelephonyNetworkSpecifier) networkSpecifier).getSubscriptionId();
                this.mSubId = subscriptionId;
                TelephonyManager telephonyManager = (TelephonyManager) MultipathPolicyTracker.this.mContext.getSystemService(TelephonyManager.class);
                if (telephonyManager == null) {
                    throw new IllegalStateException(String.format("Missing TelephonyManager", new Object[0]));
                }
                TelephonyManager createForSubscriptionId = telephonyManager.createForSubscriptionId(subscriptionId);
                if (createForSubscriptionId == null) {
                    throw new IllegalStateException(String.format("Can't get TelephonyManager for subId %d", Integer.valueOf(subscriptionId)));
                }
                String subscriberId = createForSubscriptionId.getSubscriberId();
                this.subscriberId = subscriberId;
                if (subscriberId == null) {
                    throw new IllegalStateException("Null subscriber Id for subId " + subscriptionId);
                }
                this.mNetworkTemplate = new NetworkTemplate.Builder(1).setSubscriberIds(Set.of(subscriberId)).setMeteredness(1).setDefaultNetworkStatus(0).build();
                this.mUsageCallback = new NetworkStatsManager.UsageCallback() { // from class: com.android.server.connectivity.MultipathPolicyTracker.MultipathTracker.1
                    @Override // android.app.usage.NetworkStatsManager.UsageCallback
                    public void onThresholdReached(int i, String str) {
                        MultipathTracker.this.updateMultipathBudget();
                    }
                };
                NetworkStatsManager networkStatsManager = (NetworkStatsManager) MultipathPolicyTracker.this.mContext.getSystemService(NetworkStatsManager.class);
                this.mStatsManager = networkStatsManager;
                networkStatsManager.setPollOnOpen(false);
                updateMultipathBudget();
                return;
            }
            throw new IllegalStateException(String.format("Can't get subId from mobile network %s (%s)", network, networkCapabilities));
        }

        public void setNetworkCapabilities(NetworkCapabilities networkCapabilities) {
            this.mNetworkCapabilities = new NetworkCapabilities(networkCapabilities);
        }

        public final long getDailyNonDefaultDataUsage() {
            ZonedDateTime ofInstant = ZonedDateTime.ofInstant(MultipathPolicyTracker.this.mClock.instant(), ZoneId.systemDefault());
            return getNetworkTotalBytes(ofInstant.truncatedTo(ChronoUnit.DAYS).toInstant().toEpochMilli(), ofInstant.toInstant().toEpochMilli());
        }

        public final long getNetworkTotalBytes(long j, long j2) {
            try {
                NetworkStats.Bucket querySummaryForDevice = this.mStatsManager.querySummaryForDevice(this.mNetworkTemplate, j, j2);
                return querySummaryForDevice.getRxBytes() + querySummaryForDevice.getTxBytes();
            } catch (RuntimeException e) {
                String str = MultipathPolicyTracker.TAG;
                Log.w(str, "Failed to get data usage: " + e);
                return -1L;
            }
        }

        public final NetworkIdentity getTemplateMatchingNetworkIdentity(NetworkCapabilities networkCapabilities) {
            return new NetworkIdentity.Builder().setType(0).setSubscriberId(this.subscriberId).setRoaming(!networkCapabilities.hasCapability(18)).setMetered(!networkCapabilities.hasCapability(11)).setSubId(this.mSubId).build();
        }

        public final long getRemainingDailyBudget(long j, Range<ZonedDateTime> range) {
            long epochMilli = range.getLower().toInstant().toEpochMilli();
            long epochMilli2 = range.getUpper().toInstant().toEpochMilli();
            long networkTotalBytes = getNetworkTotalBytes(epochMilli, epochMilli2);
            return (networkTotalBytes != -1 ? Math.max(0L, j - networkTotalBytes) : 0L) / Math.max(1L, (((epochMilli2 - MultipathPolicyTracker.this.mClock.millis()) - 1) / TimeUnit.DAYS.toMillis(1L)) + 1);
        }

        public final long getUserPolicyOpportunisticQuotaBytes() {
            NetworkPolicy[] networkPolicies;
            NetworkIdentity templateMatchingNetworkIdentity = getTemplateMatchingNetworkIdentity(this.mNetworkCapabilities);
            long j = Long.MAX_VALUE;
            for (NetworkPolicy networkPolicy : MultipathPolicyTracker.this.mNPM.getNetworkPolicies()) {
                if (networkPolicy.hasCycle() && networkPolicy.template.matches(templateMatchingNetworkIdentity)) {
                    long epochMilli = ((ZonedDateTime) ((Range) networkPolicy.cycleIterator().next()).getLower()).toInstant().toEpochMilli();
                    long activeWarning = MultipathPolicyTracker.getActiveWarning(networkPolicy, epochMilli);
                    if (activeWarning == -1) {
                        activeWarning = MultipathPolicyTracker.getActiveLimit(networkPolicy, epochMilli);
                    }
                    int i = (activeWarning > (-1L) ? 1 : (activeWarning == (-1L) ? 0 : -1));
                    if (i != 0 && i != 0) {
                        j = Math.min(j, getRemainingDailyBudget(activeWarning, (Range) networkPolicy.cycleIterator().next()));
                    }
                }
            }
            if (j == Long.MAX_VALUE) {
                return -1L;
            }
            return j / 20;
        }

        public void updateMultipathBudget() {
            long subscriptionOpportunisticQuota = ((NetworkPolicyManagerInternal) LocalServices.getService(NetworkPolicyManagerInternal.class)).getSubscriptionOpportunisticQuota(this.network, 2);
            if (subscriptionOpportunisticQuota == -1) {
                subscriptionOpportunisticQuota = getUserPolicyOpportunisticQuotaBytes();
            }
            if (subscriptionOpportunisticQuota == -1) {
                subscriptionOpportunisticQuota = MultipathPolicyTracker.this.getDefaultDailyMultipathQuotaBytes();
            }
            if (haveMultipathBudget() && subscriptionOpportunisticQuota == this.mQuota) {
                return;
            }
            this.mQuota = subscriptionOpportunisticQuota;
            long dailyNonDefaultDataUsage = getDailyNonDefaultDataUsage();
            long max = dailyNonDefaultDataUsage != -1 ? Math.max(0L, subscriptionOpportunisticQuota - dailyNonDefaultDataUsage) : 0L;
            if (max > 2097152) {
                setMultipathBudget(max);
            } else {
                clearMultipathBudget();
            }
        }

        public int getMultipathPreference() {
            return haveMultipathBudget() ? 3 : 0;
        }

        public long getQuota() {
            return this.mQuota;
        }

        public long getMultipathBudget() {
            return this.mMultipathBudget;
        }

        public final boolean haveMultipathBudget() {
            return this.mMultipathBudget > 0;
        }

        public final void setMultipathBudget(long j) {
            maybeUnregisterUsageCallback();
            this.mStatsManager.registerUsageCallback(this.mNetworkTemplate, j, new Executor() { // from class: com.android.server.connectivity.MultipathPolicyTracker$MultipathTracker$$ExternalSyntheticLambda0
                @Override // java.util.concurrent.Executor
                public final void execute(Runnable runnable) {
                    MultipathPolicyTracker.MultipathTracker.this.lambda$setMultipathBudget$0(runnable);
                }
            }, this.mUsageCallback);
            this.mUsageCallbackRegistered = true;
            this.mMultipathBudget = j;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setMultipathBudget$0(Runnable runnable) {
            MultipathPolicyTracker.this.mHandler.post(runnable);
        }

        public final void maybeUnregisterUsageCallback() {
            if (this.mUsageCallbackRegistered) {
                this.mStatsManager.unregisterUsageCallback(this.mUsageCallback);
                this.mUsageCallbackRegistered = false;
            }
        }

        public final void clearMultipathBudget() {
            maybeUnregisterUsageCallback();
            this.mMultipathBudget = 0L;
        }

        public void shutdown() {
            clearMultipathBudget();
        }
    }

    public static long getActiveWarning(NetworkPolicy networkPolicy, long j) {
        if (networkPolicy.lastWarningSnooze < j) {
            return networkPolicy.warningBytes;
        }
        return -1L;
    }

    public static long getActiveLimit(NetworkPolicy networkPolicy, long j) {
        if (networkPolicy.lastLimitSnooze < j) {
            return networkPolicy.limitBytes;
        }
        return -1L;
    }

    public final long getDefaultDailyMultipathQuotaBytes() {
        String string = Settings.Global.getString(this.mContext.getContentResolver(), "network_default_daily_multipath_quota_bytes");
        if (string != null) {
            try {
                return Long.parseLong(string);
            } catch (NumberFormatException unused) {
            }
        }
        return this.mContext.getResources().getInteger(17694902);
    }

    public final void registerTrackMobileCallback() {
        NetworkRequest build = new NetworkRequest.Builder().addCapability(12).addTransportType(0).build();
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() { // from class: com.android.server.connectivity.MultipathPolicyTracker.1
            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                MultipathTracker multipathTracker = (MultipathTracker) MultipathPolicyTracker.this.mMultipathTrackers.get(network);
                if (multipathTracker != null) {
                    multipathTracker.setNetworkCapabilities(networkCapabilities);
                    multipathTracker.updateMultipathBudget();
                    return;
                }
                try {
                    MultipathPolicyTracker.this.mMultipathTrackers.put(network, new MultipathTracker(network, networkCapabilities));
                } catch (IllegalStateException e) {
                    String str = MultipathPolicyTracker.TAG;
                    Log.e(str, "Can't track mobile network " + network + ": " + e.getMessage());
                }
            }

            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onLost(Network network) {
                MultipathTracker multipathTracker = (MultipathTracker) MultipathPolicyTracker.this.mMultipathTrackers.get(network);
                if (multipathTracker != null) {
                    multipathTracker.shutdown();
                    MultipathPolicyTracker.this.mMultipathTrackers.remove(network);
                }
            }
        };
        this.mMobileNetworkCallback = networkCallback;
        this.mCM.registerNetworkCallback(build, networkCallback, this.mHandler);
    }

    public final void updateAllMultipathBudgets() {
        for (MultipathTracker multipathTracker : this.mMultipathTrackers.values()) {
            multipathTracker.updateMultipathBudget();
        }
    }

    /* renamed from: com.android.server.connectivity.MultipathPolicyTracker$2 */
    /* loaded from: classes.dex */
    public class C06772 extends NetworkPolicyManager.Listener {
        public C06772() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onMeteredIfacesChanged$0() {
            MultipathPolicyTracker.this.updateAllMultipathBudgets();
        }

        public void onMeteredIfacesChanged(String[] strArr) {
            MultipathPolicyTracker.this.mHandler.post(new Runnable() { // from class: com.android.server.connectivity.MultipathPolicyTracker$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    MultipathPolicyTracker.C06772.this.lambda$onMeteredIfacesChanged$0();
                }
            });
        }
    }

    public final void registerNetworkPolicyListener() {
        C06772 c06772 = new C06772();
        this.mPolicyListener = c06772;
        this.mNPM.registerListener(c06772);
    }

    /* loaded from: classes.dex */
    public final class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            Log.wtf(MultipathPolicyTracker.TAG, "Should never be reached.");
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            if (!Settings.Global.getUriFor("network_default_daily_multipath_quota_bytes").equals(uri)) {
                String str = MultipathPolicyTracker.TAG;
                Log.wtf(str, "Unexpected settings observation: " + uri);
            }
            MultipathPolicyTracker.this.updateAllMultipathBudgets();
        }
    }

    /* loaded from: classes.dex */
    public final class ConfigChangeReceiver extends BroadcastReceiver {
        public ConfigChangeReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            MultipathPolicyTracker.this.updateAllMultipathBudgets();
        }
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.println("MultipathPolicyTracker:");
        indentingPrintWriter.increaseIndent();
        for (MultipathTracker multipathTracker : this.mMultipathTrackers.values()) {
            indentingPrintWriter.println(String.format("Network %s: quota %d, budget %d. Preference: %s", multipathTracker.network, Long.valueOf(multipathTracker.getQuota()), Long.valueOf(multipathTracker.getMultipathBudget()), DebugUtils.flagsToString(ConnectivityManager.class, "MULTIPATH_PREFERENCE_", multipathTracker.getMultipathPreference())));
        }
        indentingPrintWriter.decreaseIndent();
    }
}
