package com.android.server.net;

import android.annotation.EnforcePermission;
import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.INetd;
import android.net.INetdUnsolicitedEventListener;
import android.net.INetworkManagementEventObserver;
import android.net.ITetheringStatsProvider;
import android.net.InetAddresses;
import android.net.InterfaceConfiguration;
import android.net.InterfaceConfigurationParcel;
import android.net.IpPrefix;
import android.net.LinkAddress;
import android.net.NetworkStack;
import android.net.NetworkStats;
import android.net.RouteInfo;
import android.net.UidRangeParcel;
import android.net.util.NetdService;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.PermissionEnforcer;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.ServiceSpecificException;
import android.os.SystemClock;
import android.os.Trace;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.IBatteryStats;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.HexDump;
import com.android.internal.util.Preconditions;
import com.android.internal.util.jobs.XmlUtils;
import com.android.net.module.util.NetdUtils;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.net.NetworkManagementService;
import com.google.android.collect.Maps;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes2.dex */
public class NetworkManagementService extends INetworkManagementService.Stub {
    public static final boolean DBG = Log.isLoggable("NetworkManagement", 3);
    @GuardedBy({"mQuotaLock"})
    public HashMap<String, Long> mActiveAlerts;
    @GuardedBy({"mQuotaLock"})
    public HashMap<String, Long> mActiveQuotas;
    public IBatteryStats mBatteryStats;
    public final Context mContext;
    public final Handler mDaemonHandler;
    @GuardedBy({"mQuotaLock"})
    public volatile boolean mDataSaverMode;
    public final Dependencies mDeps;
    @GuardedBy({"mRulesLock"})
    public final SparseBooleanArray mFirewallChainStates;
    public volatile boolean mFirewallEnabled;
    public INetd mNetdService;
    public final NetdUnsolicitedEventListener mNetdUnsolicitedEventListener;
    public final RemoteCallbackList<INetworkManagementEventObserver> mObservers;
    public final Object mQuotaLock;
    public final Object mRulesLock;
    public volatile boolean mStrictEnabled;
    @GuardedBy({"mTetheringStatsProviders"})
    public final HashMap<ITetheringStatsProvider, String> mTetheringStatsProviders;
    @GuardedBy({"mRulesLock"})
    public SparseBooleanArray mUidAllowOnMetered;
    @GuardedBy({"mQuotaLock"})
    public SparseIntArray mUidCleartextPolicy;
    @GuardedBy({"mRulesLock"})
    public SparseIntArray mUidFirewallDozableRules;
    @GuardedBy({"mRulesLock"})
    public SparseIntArray mUidFirewallLowPowerStandbyRules;
    @GuardedBy({"mRulesLock"})
    public SparseIntArray mUidFirewallPowerSaveRules;
    @GuardedBy({"mRulesLock"})
    public SparseIntArray mUidFirewallRestrictedRules;
    @GuardedBy({"mRulesLock"})
    public SparseIntArray mUidFirewallRules;
    @GuardedBy({"mRulesLock"})
    public SparseIntArray mUidFirewallStandbyRules;
    @GuardedBy({"mRulesLock"})
    public SparseBooleanArray mUidRejectOnMetered;

    @FunctionalInterface
    /* loaded from: classes2.dex */
    public interface NetworkManagementEventCallback {
        void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) throws RemoteException;
    }

    public boolean isBandwidthControlEnabled() {
        return true;
    }

    /* loaded from: classes2.dex */
    public static class Dependencies {
        public IBinder getService(String str) {
            return ServiceManager.getService(str);
        }

        public void registerLocalService(NetworkManagementInternal networkManagementInternal) {
            LocalServices.addService(NetworkManagementInternal.class, networkManagementInternal);
        }

        public INetd getNetd() {
            return NetdService.get();
        }

        public int getCallingUid() {
            return Binder.getCallingUid();
        }
    }

    public NetworkManagementService(Context context, Dependencies dependencies) {
        super(PermissionEnforcer.fromContext(context));
        this.mObservers = new RemoteCallbackList<>();
        HashMap<ITetheringStatsProvider, String> newHashMap = Maps.newHashMap();
        this.mTetheringStatsProviders = newHashMap;
        this.mQuotaLock = new Object();
        this.mRulesLock = new Object();
        this.mActiveQuotas = Maps.newHashMap();
        this.mActiveAlerts = Maps.newHashMap();
        this.mUidRejectOnMetered = new SparseBooleanArray();
        this.mUidAllowOnMetered = new SparseBooleanArray();
        this.mUidCleartextPolicy = new SparseIntArray();
        this.mUidFirewallRules = new SparseIntArray();
        this.mUidFirewallStandbyRules = new SparseIntArray();
        this.mUidFirewallDozableRules = new SparseIntArray();
        this.mUidFirewallPowerSaveRules = new SparseIntArray();
        this.mUidFirewallRestrictedRules = new SparseIntArray();
        this.mUidFirewallLowPowerStandbyRules = new SparseIntArray();
        this.mFirewallChainStates = new SparseBooleanArray();
        this.mContext = context;
        this.mDeps = dependencies;
        this.mDaemonHandler = new Handler(FgThread.get().getLooper());
        this.mNetdUnsolicitedEventListener = new NetdUnsolicitedEventListener();
        dependencies.registerLocalService(new LocalService());
        synchronized (newHashMap) {
            newHashMap.put(new NetdTetheringStatsProvider(), "netd");
        }
    }

    public NetworkManagementService() {
        this.mObservers = new RemoteCallbackList<>();
        this.mTetheringStatsProviders = Maps.newHashMap();
        this.mQuotaLock = new Object();
        this.mRulesLock = new Object();
        this.mActiveQuotas = Maps.newHashMap();
        this.mActiveAlerts = Maps.newHashMap();
        this.mUidRejectOnMetered = new SparseBooleanArray();
        this.mUidAllowOnMetered = new SparseBooleanArray();
        this.mUidCleartextPolicy = new SparseIntArray();
        this.mUidFirewallRules = new SparseIntArray();
        this.mUidFirewallStandbyRules = new SparseIntArray();
        this.mUidFirewallDozableRules = new SparseIntArray();
        this.mUidFirewallPowerSaveRules = new SparseIntArray();
        this.mUidFirewallRestrictedRules = new SparseIntArray();
        this.mUidFirewallLowPowerStandbyRules = new SparseIntArray();
        this.mFirewallChainStates = new SparseBooleanArray();
        this.mContext = null;
        this.mDaemonHandler = null;
        this.mDeps = null;
        this.mNetdUnsolicitedEventListener = null;
    }

    public static NetworkManagementService create(Context context, Dependencies dependencies) throws InterruptedException {
        NetworkManagementService networkManagementService = new NetworkManagementService(context, dependencies);
        boolean z = DBG;
        if (z) {
            Slog.d("NetworkManagement", "Creating NetworkManagementService");
        }
        if (z) {
            Slog.d("NetworkManagement", "Connecting native netd service");
        }
        networkManagementService.connectNativeNetdService();
        if (z) {
            Slog.d("NetworkManagement", "Connected");
        }
        return networkManagementService;
    }

    public static NetworkManagementService create(Context context) throws InterruptedException {
        return create(context, new Dependencies());
    }

    public void systemReady() {
        if (DBG) {
            long currentTimeMillis = System.currentTimeMillis();
            prepareNativeDaemon();
            Slog.d("NetworkManagement", "Prepared in " + (System.currentTimeMillis() - currentTimeMillis) + "ms");
            return;
        }
        prepareNativeDaemon();
    }

    public final IBatteryStats getBatteryStats() {
        synchronized (this) {
            IBatteryStats iBatteryStats = this.mBatteryStats;
            if (iBatteryStats != null) {
                return iBatteryStats;
            }
            IBatteryStats asInterface = IBatteryStats.Stub.asInterface(this.mDeps.getService("batterystats"));
            this.mBatteryStats = asInterface;
            return asInterface;
        }
    }

    public void registerObserver(INetworkManagementEventObserver iNetworkManagementEventObserver) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        this.mObservers.register(iNetworkManagementEventObserver);
    }

    public void unregisterObserver(INetworkManagementEventObserver iNetworkManagementEventObserver) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        this.mObservers.unregister(iNetworkManagementEventObserver);
    }

    public final void invokeForAllObservers(NetworkManagementEventCallback networkManagementEventCallback) {
        int beginBroadcast = this.mObservers.beginBroadcast();
        for (int i = 0; i < beginBroadcast; i++) {
            try {
                networkManagementEventCallback.sendCallback(this.mObservers.getBroadcastItem(i));
            } catch (RemoteException | RuntimeException unused) {
            } catch (Throwable th) {
                this.mObservers.finishBroadcast();
                throw th;
            }
        }
        this.mObservers.finishBroadcast();
    }

    public final void notifyInterfaceStatusChanged(final String str, final boolean z) {
        invokeForAllObservers(new NetworkManagementEventCallback() { // from class: com.android.server.net.NetworkManagementService$$ExternalSyntheticLambda9
            @Override // com.android.server.net.NetworkManagementService.NetworkManagementEventCallback
            public final void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) {
                iNetworkManagementEventObserver.interfaceStatusChanged(str, z);
            }
        });
    }

    public final void notifyInterfaceLinkStateChanged(final String str, final boolean z) {
        invokeForAllObservers(new NetworkManagementEventCallback() { // from class: com.android.server.net.NetworkManagementService$$ExternalSyntheticLambda11
            @Override // com.android.server.net.NetworkManagementService.NetworkManagementEventCallback
            public final void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) {
                iNetworkManagementEventObserver.interfaceLinkStateChanged(str, z);
            }
        });
    }

    public final void notifyInterfaceAdded(final String str) {
        invokeForAllObservers(new NetworkManagementEventCallback() { // from class: com.android.server.net.NetworkManagementService$$ExternalSyntheticLambda4
            @Override // com.android.server.net.NetworkManagementService.NetworkManagementEventCallback
            public final void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) {
                iNetworkManagementEventObserver.interfaceAdded(str);
            }
        });
    }

    public final void notifyInterfaceRemoved(final String str) {
        this.mActiveAlerts.remove(str);
        this.mActiveQuotas.remove(str);
        invokeForAllObservers(new NetworkManagementEventCallback() { // from class: com.android.server.net.NetworkManagementService$$ExternalSyntheticLambda10
            @Override // com.android.server.net.NetworkManagementService.NetworkManagementEventCallback
            public final void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) {
                iNetworkManagementEventObserver.interfaceRemoved(str);
            }
        });
    }

    public final void notifyLimitReached(final String str, final String str2) {
        invokeForAllObservers(new NetworkManagementEventCallback() { // from class: com.android.server.net.NetworkManagementService$$ExternalSyntheticLambda1
            @Override // com.android.server.net.NetworkManagementService.NetworkManagementEventCallback
            public final void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) {
                iNetworkManagementEventObserver.limitReached(str, str2);
            }
        });
    }

    public final void notifyInterfaceClassActivity(final int i, final boolean z, final long j, final int i2) {
        invokeForAllObservers(new NetworkManagementEventCallback() { // from class: com.android.server.net.NetworkManagementService$$ExternalSyntheticLambda5
            @Override // com.android.server.net.NetworkManagementService.NetworkManagementEventCallback
            public final void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) {
                iNetworkManagementEventObserver.interfaceClassDataActivityChanged(i, z, j, i2);
            }
        });
    }

    public void registerTetheringStatsProvider(ITetheringStatsProvider iTetheringStatsProvider, String str) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        Objects.requireNonNull(iTetheringStatsProvider);
        synchronized (this.mTetheringStatsProviders) {
            this.mTetheringStatsProviders.put(iTetheringStatsProvider, str);
        }
    }

    public void unregisterTetheringStatsProvider(ITetheringStatsProvider iTetheringStatsProvider) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        synchronized (this.mTetheringStatsProviders) {
            this.mTetheringStatsProviders.remove(iTetheringStatsProvider);
        }
    }

    public void tetherLimitReached(ITetheringStatsProvider iTetheringStatsProvider) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        synchronized (this.mTetheringStatsProviders) {
            if (this.mTetheringStatsProviders.containsKey(iTetheringStatsProvider)) {
                this.mDaemonHandler.post(new Runnable() { // from class: com.android.server.net.NetworkManagementService$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        NetworkManagementService.this.lambda$tetherLimitReached$6();
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$tetherLimitReached$6() {
        notifyLimitReached("globalAlert", null);
    }

    public final void syncFirewallChainLocked(int i, String str) {
        SparseIntArray clone;
        synchronized (this.mRulesLock) {
            SparseIntArray uidFirewallRulesLR = getUidFirewallRulesLR(i);
            clone = uidFirewallRulesLR.clone();
            uidFirewallRulesLR.clear();
        }
        if (clone.size() > 0) {
            if (DBG) {
                Slog.d("NetworkManagement", "Pushing " + clone.size() + " active firewall " + str + "UID rules");
            }
            for (int i2 = 0; i2 < clone.size(); i2++) {
                setFirewallUidRuleLocked(i, clone.keyAt(i2), clone.valueAt(i2));
            }
        }
    }

    public final void connectNativeNetdService() {
        INetd netd = this.mDeps.getNetd();
        this.mNetdService = netd;
        try {
            netd.registerUnsolicitedEventListener(this.mNetdUnsolicitedEventListener);
            if (DBG) {
                Slog.d("NetworkManagement", "Register unsolicited event listener");
            }
        } catch (RemoteException | ServiceSpecificException e) {
            Slog.e("NetworkManagement", "Failed to set Netd unsolicited event listener " + e);
        }
    }

    public final void prepareNativeDaemon() {
        SparseBooleanArray sparseBooleanArray;
        SparseBooleanArray sparseBooleanArray2;
        synchronized (this.mQuotaLock) {
            this.mStrictEnabled = true;
            setDataSaverModeEnabled(this.mDataSaverMode);
            int size = this.mActiveQuotas.size();
            if (size > 0) {
                if (DBG) {
                    Slog.d("NetworkManagement", "Pushing " + size + " active quota rules");
                }
                HashMap<String, Long> hashMap = this.mActiveQuotas;
                this.mActiveQuotas = Maps.newHashMap();
                for (Map.Entry<String, Long> entry : hashMap.entrySet()) {
                    setInterfaceQuota(entry.getKey(), entry.getValue().longValue());
                }
            }
            int size2 = this.mActiveAlerts.size();
            if (size2 > 0) {
                if (DBG) {
                    Slog.d("NetworkManagement", "Pushing " + size2 + " active alert rules");
                }
                HashMap<String, Long> hashMap2 = this.mActiveAlerts;
                this.mActiveAlerts = Maps.newHashMap();
                for (Map.Entry<String, Long> entry2 : hashMap2.entrySet()) {
                    setInterfaceAlert(entry2.getKey(), entry2.getValue().longValue());
                }
            }
            synchronized (this.mRulesLock) {
                int size3 = this.mUidRejectOnMetered.size();
                sparseBooleanArray = null;
                if (size3 > 0) {
                    if (DBG) {
                        Slog.d("NetworkManagement", "Pushing " + size3 + " UIDs to metered denylist rules");
                    }
                    sparseBooleanArray2 = this.mUidRejectOnMetered;
                    this.mUidRejectOnMetered = new SparseBooleanArray();
                } else {
                    sparseBooleanArray2 = null;
                }
                int size4 = this.mUidAllowOnMetered.size();
                if (size4 > 0) {
                    if (DBG) {
                        Slog.d("NetworkManagement", "Pushing " + size4 + " UIDs to metered allowlist rules");
                    }
                    sparseBooleanArray = this.mUidAllowOnMetered;
                    this.mUidAllowOnMetered = new SparseBooleanArray();
                }
            }
            if (sparseBooleanArray2 != null) {
                for (int i = 0; i < sparseBooleanArray2.size(); i++) {
                    setUidOnMeteredNetworkDenylist(sparseBooleanArray2.keyAt(i), sparseBooleanArray2.valueAt(i));
                }
            }
            if (sparseBooleanArray != null) {
                for (int i2 = 0; i2 < sparseBooleanArray.size(); i2++) {
                    setUidOnMeteredNetworkAllowlist(sparseBooleanArray.keyAt(i2), sparseBooleanArray.valueAt(i2));
                }
            }
            int size5 = this.mUidCleartextPolicy.size();
            if (size5 > 0) {
                if (DBG) {
                    Slog.d("NetworkManagement", "Pushing " + size5 + " active UID cleartext policies");
                }
                SparseIntArray sparseIntArray = this.mUidCleartextPolicy;
                this.mUidCleartextPolicy = new SparseIntArray();
                for (int i3 = 0; i3 < sparseIntArray.size(); i3++) {
                    setUidCleartextNetworkPolicy(sparseIntArray.keyAt(i3), sparseIntArray.valueAt(i3));
                }
            }
            setFirewallEnabled(this.mFirewallEnabled);
            syncFirewallChainLocked(0, "");
            syncFirewallChainLocked(2, "standby ");
            syncFirewallChainLocked(1, "dozable ");
            syncFirewallChainLocked(3, "powersave ");
            syncFirewallChainLocked(4, "restricted ");
            syncFirewallChainLocked(5, "low power standby ");
            int[] iArr = {2, 1, 3, 4, 5};
            for (int i4 = 0; i4 < 5; i4++) {
                int i5 = iArr[i4];
                if (getFirewallChainState(i5)) {
                    setFirewallChainEnabled(i5, true);
                }
            }
        }
        try {
            getBatteryStats().noteNetworkStatsEnabled();
        } catch (RemoteException unused) {
        }
    }

    public final void notifyAddressUpdated(final String str, final LinkAddress linkAddress) {
        invokeForAllObservers(new NetworkManagementEventCallback() { // from class: com.android.server.net.NetworkManagementService$$ExternalSyntheticLambda3
            @Override // com.android.server.net.NetworkManagementService.NetworkManagementEventCallback
            public final void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) {
                iNetworkManagementEventObserver.addressUpdated(str, linkAddress);
            }
        });
    }

    public final void notifyAddressRemoved(final String str, final LinkAddress linkAddress) {
        invokeForAllObservers(new NetworkManagementEventCallback() { // from class: com.android.server.net.NetworkManagementService$$ExternalSyntheticLambda8
            @Override // com.android.server.net.NetworkManagementService.NetworkManagementEventCallback
            public final void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) {
                iNetworkManagementEventObserver.addressRemoved(str, linkAddress);
            }
        });
    }

    public final void notifyInterfaceDnsServerInfo(final String str, final long j, final String[] strArr) {
        invokeForAllObservers(new NetworkManagementEventCallback() { // from class: com.android.server.net.NetworkManagementService$$ExternalSyntheticLambda2
            @Override // com.android.server.net.NetworkManagementService.NetworkManagementEventCallback
            public final void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) {
                iNetworkManagementEventObserver.interfaceDnsServerInfo(str, j, strArr);
            }
        });
    }

    public final void notifyRouteChange(boolean z, final RouteInfo routeInfo) {
        if (z) {
            invokeForAllObservers(new NetworkManagementEventCallback() { // from class: com.android.server.net.NetworkManagementService$$ExternalSyntheticLambda6
                @Override // com.android.server.net.NetworkManagementService.NetworkManagementEventCallback
                public final void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) {
                    iNetworkManagementEventObserver.routeUpdated(routeInfo);
                }
            });
        } else {
            invokeForAllObservers(new NetworkManagementEventCallback() { // from class: com.android.server.net.NetworkManagementService$$ExternalSyntheticLambda7
                @Override // com.android.server.net.NetworkManagementService.NetworkManagementEventCallback
                public final void sendCallback(INetworkManagementEventObserver iNetworkManagementEventObserver) {
                    iNetworkManagementEventObserver.routeRemoved(routeInfo);
                }
            });
        }
    }

    /* loaded from: classes2.dex */
    public class NetdUnsolicitedEventListener extends INetdUnsolicitedEventListener.Stub {
        @Override // android.net.INetdUnsolicitedEventListener
        public String getInterfaceHash() {
            return "38614f80a23b92603d4851177e57c460aec1b606";
        }

        @Override // android.net.INetdUnsolicitedEventListener
        public int getInterfaceVersion() {
            return 13;
        }

        public NetdUnsolicitedEventListener() {
        }

        @Override // android.net.INetdUnsolicitedEventListener
        public void onInterfaceClassActivityChanged(final boolean z, final int i, long j, final int i2) throws RemoteException {
            if (j <= 0) {
                j = SystemClock.elapsedRealtimeNanos();
            }
            final long j2 = j;
            NetworkManagementService.this.mDaemonHandler.post(new Runnable() { // from class: com.android.server.net.NetworkManagementService$NetdUnsolicitedEventListener$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    NetworkManagementService.NetdUnsolicitedEventListener.this.lambda$onInterfaceClassActivityChanged$0(i, z, j2, i2);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onInterfaceClassActivityChanged$0(int i, boolean z, long j, int i2) {
            NetworkManagementService.this.notifyInterfaceClassActivity(i, z, j, i2);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onQuotaLimitReached$1(String str, String str2) {
            NetworkManagementService.this.notifyLimitReached(str, str2);
        }

        @Override // android.net.INetdUnsolicitedEventListener
        public void onQuotaLimitReached(final String str, final String str2) throws RemoteException {
            NetworkManagementService.this.mDaemonHandler.post(new Runnable() { // from class: com.android.server.net.NetworkManagementService$NetdUnsolicitedEventListener$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    NetworkManagementService.NetdUnsolicitedEventListener.this.lambda$onQuotaLimitReached$1(str, str2);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onInterfaceDnsServerInfo$2(String str, long j, String[] strArr) {
            NetworkManagementService.this.notifyInterfaceDnsServerInfo(str, j, strArr);
        }

        @Override // android.net.INetdUnsolicitedEventListener
        public void onInterfaceDnsServerInfo(final String str, final long j, final String[] strArr) throws RemoteException {
            NetworkManagementService.this.mDaemonHandler.post(new Runnable() { // from class: com.android.server.net.NetworkManagementService$NetdUnsolicitedEventListener$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    NetworkManagementService.NetdUnsolicitedEventListener.this.lambda$onInterfaceDnsServerInfo$2(str, j, strArr);
                }
            });
        }

        @Override // android.net.INetdUnsolicitedEventListener
        public void onInterfaceAddressUpdated(String str, final String str2, int i, int i2) throws RemoteException {
            final LinkAddress linkAddress = new LinkAddress(str, i, i2);
            NetworkManagementService.this.mDaemonHandler.post(new Runnable() { // from class: com.android.server.net.NetworkManagementService$NetdUnsolicitedEventListener$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    NetworkManagementService.NetdUnsolicitedEventListener.this.lambda$onInterfaceAddressUpdated$3(str2, linkAddress);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onInterfaceAddressUpdated$3(String str, LinkAddress linkAddress) {
            NetworkManagementService.this.notifyAddressUpdated(str, linkAddress);
        }

        @Override // android.net.INetdUnsolicitedEventListener
        public void onInterfaceAddressRemoved(String str, final String str2, int i, int i2) throws RemoteException {
            final LinkAddress linkAddress = new LinkAddress(str, i, i2);
            NetworkManagementService.this.mDaemonHandler.post(new Runnable() { // from class: com.android.server.net.NetworkManagementService$NetdUnsolicitedEventListener$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    NetworkManagementService.NetdUnsolicitedEventListener.this.lambda$onInterfaceAddressRemoved$4(str2, linkAddress);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onInterfaceAddressRemoved$4(String str, LinkAddress linkAddress) {
            NetworkManagementService.this.notifyAddressRemoved(str, linkAddress);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onInterfaceAdded$5(String str) {
            NetworkManagementService.this.notifyInterfaceAdded(str);
        }

        @Override // android.net.INetdUnsolicitedEventListener
        public void onInterfaceAdded(final String str) throws RemoteException {
            NetworkManagementService.this.mDaemonHandler.post(new Runnable() { // from class: com.android.server.net.NetworkManagementService$NetdUnsolicitedEventListener$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    NetworkManagementService.NetdUnsolicitedEventListener.this.lambda$onInterfaceAdded$5(str);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onInterfaceRemoved$6(String str) {
            NetworkManagementService.this.notifyInterfaceRemoved(str);
        }

        @Override // android.net.INetdUnsolicitedEventListener
        public void onInterfaceRemoved(final String str) throws RemoteException {
            NetworkManagementService.this.mDaemonHandler.post(new Runnable() { // from class: com.android.server.net.NetworkManagementService$NetdUnsolicitedEventListener$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    NetworkManagementService.NetdUnsolicitedEventListener.this.lambda$onInterfaceRemoved$6(str);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onInterfaceChanged$7(String str, boolean z) {
            NetworkManagementService.this.notifyInterfaceStatusChanged(str, z);
        }

        @Override // android.net.INetdUnsolicitedEventListener
        public void onInterfaceChanged(final String str, final boolean z) throws RemoteException {
            NetworkManagementService.this.mDaemonHandler.post(new Runnable() { // from class: com.android.server.net.NetworkManagementService$NetdUnsolicitedEventListener$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    NetworkManagementService.NetdUnsolicitedEventListener.this.lambda$onInterfaceChanged$7(str, z);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onInterfaceLinkStateChanged$8(String str, boolean z) {
            NetworkManagementService.this.notifyInterfaceLinkStateChanged(str, z);
        }

        @Override // android.net.INetdUnsolicitedEventListener
        public void onInterfaceLinkStateChanged(final String str, final boolean z) throws RemoteException {
            NetworkManagementService.this.mDaemonHandler.post(new Runnable() { // from class: com.android.server.net.NetworkManagementService$NetdUnsolicitedEventListener$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    NetworkManagementService.NetdUnsolicitedEventListener.this.lambda$onInterfaceLinkStateChanged$8(str, z);
                }
            });
        }

        @Override // android.net.INetdUnsolicitedEventListener
        public void onRouteChanged(final boolean z, String str, String str2, String str3) throws RemoteException {
            final RouteInfo routeInfo = new RouteInfo(new IpPrefix(str), "".equals(str2) ? null : InetAddresses.parseNumericAddress(str2), str3, 1);
            NetworkManagementService.this.mDaemonHandler.post(new Runnable() { // from class: com.android.server.net.NetworkManagementService$NetdUnsolicitedEventListener$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    NetworkManagementService.NetdUnsolicitedEventListener.this.lambda$onRouteChanged$9(z, routeInfo);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onRouteChanged$9(boolean z, RouteInfo routeInfo) {
            NetworkManagementService.this.notifyRouteChange(z, routeInfo);
        }

        @Override // android.net.INetdUnsolicitedEventListener
        public void onStrictCleartextDetected(int i, String str) throws RemoteException {
            ActivityManager.getService().notifyCleartextNetwork(i, HexDump.hexStringToByteArray(str));
        }
    }

    public String[] listInterfaces() {
        NetworkStack.checkNetworkStackPermissionOr(this.mContext, new String[]{"android.permission.CONNECTIVITY_INTERNAL"});
        try {
            return this.mNetdService.interfaceGetList();
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public static InterfaceConfigurationParcel toStableParcel(InterfaceConfiguration interfaceConfiguration, String str) {
        InterfaceConfigurationParcel interfaceConfigurationParcel = new InterfaceConfigurationParcel();
        interfaceConfigurationParcel.ifName = str;
        String hardwareAddress = interfaceConfiguration.getHardwareAddress();
        if (!TextUtils.isEmpty(hardwareAddress)) {
            interfaceConfigurationParcel.hwAddr = hardwareAddress;
        } else {
            interfaceConfigurationParcel.hwAddr = "";
        }
        interfaceConfigurationParcel.ipv4Addr = interfaceConfiguration.getLinkAddress().getAddress().getHostAddress();
        interfaceConfigurationParcel.prefixLength = interfaceConfiguration.getLinkAddress().getPrefixLength();
        ArrayList arrayList = new ArrayList();
        for (String str2 : interfaceConfiguration.getFlags()) {
            arrayList.add(str2);
        }
        interfaceConfigurationParcel.flags = (String[]) arrayList.toArray(new String[0]);
        return interfaceConfigurationParcel;
    }

    public static InterfaceConfiguration fromStableParcel(InterfaceConfigurationParcel interfaceConfigurationParcel) {
        InterfaceConfiguration interfaceConfiguration = new InterfaceConfiguration();
        interfaceConfiguration.setHardwareAddress(interfaceConfigurationParcel.hwAddr);
        interfaceConfiguration.setLinkAddress(new LinkAddress(InetAddresses.parseNumericAddress(interfaceConfigurationParcel.ipv4Addr), interfaceConfigurationParcel.prefixLength));
        for (String str : interfaceConfigurationParcel.flags) {
            interfaceConfiguration.setFlag(str);
        }
        return interfaceConfiguration;
    }

    public InterfaceConfiguration getInterfaceConfig(String str) {
        NetworkStack.checkNetworkStackPermissionOr(this.mContext, new String[]{"android.permission.CONNECTIVITY_INTERNAL"});
        try {
            try {
                return fromStableParcel(this.mNetdService.interfaceGetCfg(str));
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Invalid InterfaceConfigurationParcel", e);
            }
        } catch (RemoteException | ServiceSpecificException e2) {
            throw new IllegalStateException(e2);
        }
    }

    public void setInterfaceConfig(String str, InterfaceConfiguration interfaceConfiguration) {
        NetworkStack.checkNetworkStackPermissionOr(this.mContext, new String[]{"android.permission.CONNECTIVITY_INTERNAL"});
        LinkAddress linkAddress = interfaceConfiguration.getLinkAddress();
        if (linkAddress == null || linkAddress.getAddress() == null) {
            throw new IllegalStateException("Null LinkAddress given");
        }
        try {
            this.mNetdService.interfaceSetCfg(toStableParcel(interfaceConfiguration, str));
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setInterfaceDown(String str) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        InterfaceConfiguration interfaceConfig = getInterfaceConfig(str);
        interfaceConfig.setInterfaceDown();
        setInterfaceConfig(str, interfaceConfig);
    }

    public void setInterfaceUp(String str) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        InterfaceConfiguration interfaceConfig = getInterfaceConfig(str);
        interfaceConfig.setInterfaceUp();
        setInterfaceConfig(str, interfaceConfig);
    }

    public void setInterfaceIpv6PrivacyExtensions(String str, boolean z) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        try {
            this.mNetdService.interfaceSetIPv6PrivacyExtensions(str, z);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void clearInterfaceAddresses(String str) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        try {
            this.mNetdService.interfaceClearAddrs(str);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void enableIpv6(String str) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        try {
            this.mNetdService.interfaceSetEnableIPv6(str, true);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setIPv6AddrGenMode(String str, int i) throws ServiceSpecificException {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        try {
            this.mNetdService.setIPv6AddrGenMode(str, i);
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    public void disableIpv6(String str) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        try {
            this.mNetdService.interfaceSetEnableIPv6(str, false);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void addRoute(int i, RouteInfo routeInfo) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        NetdUtils.modifyRoute(this.mNetdService, NetdUtils.ModifyOperation.ADD, i, routeInfo);
    }

    public void removeRoute(int i, RouteInfo routeInfo) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        NetdUtils.modifyRoute(this.mNetdService, NetdUtils.ModifyOperation.REMOVE, i, routeInfo);
    }

    @EnforcePermission("android.permission.SHUTDOWN")
    public void shutdown() {
        super.shutdown_enforcePermission();
        Slog.i("NetworkManagement", "Shutting down");
    }

    public boolean getIpForwardingEnabled() throws IllegalStateException {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        try {
            return this.mNetdService.ipfwdEnabled();
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setIpForwardingEnabled(boolean z) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        try {
            if (z) {
                this.mNetdService.ipfwdEnableForwarding("tethering");
            } else {
                this.mNetdService.ipfwdDisableForwarding("tethering");
            }
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void startTethering(String[] strArr) {
        startTetheringWithConfiguration(true, strArr);
    }

    public void startTetheringWithConfiguration(boolean z, String[] strArr) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        try {
            NetdUtils.tetherStart(this.mNetdService, z, strArr);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void stopTethering() {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        try {
            this.mNetdService.tetherStop();
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean isTetheringStarted() {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        try {
            return this.mNetdService.tetherIsEnabled();
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void tetherInterface(String str) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        try {
            LinkAddress linkAddress = getInterfaceConfig(str).getLinkAddress();
            NetdUtils.tetherInterface(this.mNetdService, str, new IpPrefix(linkAddress.getAddress(), linkAddress.getPrefixLength()));
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void untetherInterface(String str) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        try {
            NetdUtils.untetherInterface(this.mNetdService, str);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public String[] listTetheredInterfaces() {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        try {
            return this.mNetdService.tetherInterfaceList();
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public String[] getDnsForwarders() {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        try {
            return this.mNetdService.tetherDnsList();
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public final void modifyInterfaceForward(boolean z, String str, String str2) {
        try {
            if (z) {
                this.mNetdService.ipfwdAddInterfaceForward(str, str2);
            } else {
                this.mNetdService.ipfwdRemoveInterfaceForward(str, str2);
            }
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void startInterfaceForwarding(String str, String str2) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        modifyInterfaceForward(true, str, str2);
    }

    public void stopInterfaceForwarding(String str, String str2) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        modifyInterfaceForward(false, str, str2);
    }

    public void enableNat(String str, String str2) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        try {
            this.mNetdService.tetherAddForward(str, str2);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void disableNat(String str, String str2) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        try {
            this.mNetdService.tetherRemoveForward(str, str2);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setInterfaceQuota(String str, long j) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        synchronized (this.mQuotaLock) {
            if (this.mActiveQuotas.containsKey(str)) {
                throw new IllegalStateException("iface " + str + " already has quota");
            }
            try {
                this.mNetdService.bandwidthSetInterfaceQuota(str, j);
                this.mActiveQuotas.put(str, Long.valueOf(j));
                synchronized (this.mTetheringStatsProviders) {
                    for (ITetheringStatsProvider iTetheringStatsProvider : this.mTetheringStatsProviders.keySet()) {
                        try {
                            iTetheringStatsProvider.setInterfaceQuota(str, j);
                        } catch (RemoteException e) {
                            Log.e("NetworkManagement", "Problem setting tethering data limit on provider " + this.mTetheringStatsProviders.get(iTetheringStatsProvider) + ": " + e);
                        }
                    }
                }
            } catch (RemoteException | ServiceSpecificException e2) {
                throw new IllegalStateException(e2);
            }
        }
    }

    public void removeInterfaceQuota(String str) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        synchronized (this.mQuotaLock) {
            if (this.mActiveQuotas.containsKey(str)) {
                this.mActiveQuotas.remove(str);
                this.mActiveAlerts.remove(str);
                try {
                    this.mNetdService.bandwidthRemoveInterfaceQuota(str);
                    synchronized (this.mTetheringStatsProviders) {
                        for (ITetheringStatsProvider iTetheringStatsProvider : this.mTetheringStatsProviders.keySet()) {
                            try {
                                iTetheringStatsProvider.setInterfaceQuota(str, -1L);
                            } catch (RemoteException e) {
                                Log.e("NetworkManagement", "Problem removing tethering data limit on provider " + this.mTetheringStatsProviders.get(iTetheringStatsProvider) + ": " + e);
                            }
                        }
                    }
                } catch (RemoteException | ServiceSpecificException e2) {
                    throw new IllegalStateException(e2);
                }
            }
        }
    }

    public void setInterfaceAlert(String str, long j) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        if (!this.mActiveQuotas.containsKey(str)) {
            throw new IllegalStateException("setting alert requires existing quota on iface");
        }
        synchronized (this.mQuotaLock) {
            if (this.mActiveAlerts.containsKey(str)) {
                throw new IllegalStateException("iface " + str + " already has alert");
            }
            try {
                this.mNetdService.bandwidthSetInterfaceAlert(str, j);
                this.mActiveAlerts.put(str, Long.valueOf(j));
            } catch (RemoteException | ServiceSpecificException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public void removeInterfaceAlert(String str) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        synchronized (this.mQuotaLock) {
            if (this.mActiveAlerts.containsKey(str)) {
                try {
                    this.mNetdService.bandwidthRemoveInterfaceAlert(str);
                    this.mActiveAlerts.remove(str);
                } catch (RemoteException | ServiceSpecificException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    }

    public void setGlobalAlert(long j) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        try {
            this.mNetdService.bandwidthSetGlobalAlert(j);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public final void setUidOnMeteredNetworkList(int i, boolean z, boolean z2) {
        SparseBooleanArray sparseBooleanArray;
        boolean z3;
        NetworkStack.checkNetworkStackPermission(this.mContext);
        synchronized (this.mQuotaLock) {
            synchronized (this.mRulesLock) {
                sparseBooleanArray = z ? this.mUidAllowOnMetered : this.mUidRejectOnMetered;
                z3 = sparseBooleanArray.get(i, false);
            }
            if (z3 == z2) {
                return;
            }
            Trace.traceBegin(2097152L, "inetd bandwidth");
            ConnectivityManager connectivityManager = (ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class);
            try {
                if (z) {
                    if (z2) {
                        connectivityManager.addUidToMeteredNetworkAllowList(i);
                    } else {
                        connectivityManager.removeUidFromMeteredNetworkAllowList(i);
                    }
                } else if (z2) {
                    connectivityManager.addUidToMeteredNetworkDenyList(i);
                } else {
                    connectivityManager.removeUidFromMeteredNetworkDenyList(i);
                }
                synchronized (this.mRulesLock) {
                    if (z2) {
                        sparseBooleanArray.put(i, true);
                    } else {
                        sparseBooleanArray.delete(i);
                    }
                }
                Trace.traceEnd(2097152L);
            } catch (RuntimeException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public void setUidOnMeteredNetworkDenylist(int i, boolean z) {
        setUidOnMeteredNetworkList(i, false, z);
    }

    public void setUidOnMeteredNetworkAllowlist(int i, boolean z) {
        setUidOnMeteredNetworkList(i, true, z);
    }

    @EnforcePermission("android.permission.NETWORK_SETTINGS")
    public boolean setDataSaverModeEnabled(boolean z) {
        super.setDataSaverModeEnabled_enforcePermission();
        if (DBG) {
            Log.d("NetworkManagement", "setDataSaverMode: " + z);
        }
        synchronized (this.mQuotaLock) {
            if (this.mDataSaverMode == z) {
                Log.w("NetworkManagement", "setDataSaverMode(): already " + this.mDataSaverMode);
                return true;
            }
            Trace.traceBegin(2097152L, "bandwidthEnableDataSaver");
            try {
                boolean bandwidthEnableDataSaver = this.mNetdService.bandwidthEnableDataSaver(z);
                if (bandwidthEnableDataSaver) {
                    this.mDataSaverMode = z;
                } else {
                    Log.w("NetworkManagement", "setDataSaverMode(" + z + "): netd command silently failed");
                }
                Trace.traceEnd(2097152L);
                return bandwidthEnableDataSaver;
            } catch (RemoteException e) {
                Log.w("NetworkManagement", "setDataSaverMode(" + z + "): netd command failed", e);
                Trace.traceEnd(2097152L);
                return false;
            }
        }
    }

    public final void applyUidCleartextNetworkPolicy(int i, int i2) {
        int i3 = 1;
        if (i2 != 0) {
            if (i2 == 1) {
                i3 = 2;
            } else if (i2 != 2) {
                throw new IllegalArgumentException("Unknown policy " + i2);
            } else {
                i3 = 3;
            }
        }
        try {
            this.mNetdService.strictUidCleartextPenalty(i, i3);
            this.mUidCleartextPolicy.put(i, i2);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setUidCleartextNetworkPolicy(int i, int i2) {
        if (this.mDeps.getCallingUid() != i) {
            NetworkStack.checkNetworkStackPermission(this.mContext);
        }
        synchronized (this.mQuotaLock) {
            int i3 = this.mUidCleartextPolicy.get(i, 0);
            if (i3 == i2) {
                return;
            }
            if (!this.mStrictEnabled) {
                this.mUidCleartextPolicy.put(i, i2);
                return;
            }
            if (i3 != 0 && i2 != 0) {
                applyUidCleartextNetworkPolicy(i, 0);
            }
            applyUidCleartextNetworkPolicy(i, i2);
        }
    }

    /* loaded from: classes2.dex */
    public class NetdTetheringStatsProvider extends ITetheringStatsProvider.Stub {
        public void setInterfaceQuota(String str, long j) {
        }

        public NetdTetheringStatsProvider() {
        }

        public NetworkStats getTetherStats(int i) {
            throw new UnsupportedOperationException();
        }
    }

    public NetworkStats getNetworkStatsTethering(int i) {
        throw new UnsupportedOperationException();
    }

    public void setFirewallEnabled(boolean z) {
        enforceSystemUid();
        try {
            this.mNetdService.firewallSetFirewallType(z ? 0 : 1);
            this.mFirewallEnabled = z;
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean isFirewallEnabled() {
        enforceSystemUid();
        return this.mFirewallEnabled;
    }

    public void setFirewallInterfaceRule(String str, boolean z) {
        enforceSystemUid();
        Preconditions.checkState(this.mFirewallEnabled);
        try {
            this.mNetdService.firewallSetInterfaceRule(str, z ? 1 : 2);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public final void closeSocketsForFirewallChainLocked(int i, String str) {
        int size;
        UidRangeParcel[] uidRangeParcelArr;
        int i2;
        UidRangeParcel[] uidRangeParcelArr2;
        int[] iArr;
        int size2;
        int i3;
        if (DBG) {
            Slog.d("NetworkManagement", "Closing sockets after enabling chain " + str);
        }
        if (getFirewallType(i) == 0) {
            uidRangeParcelArr2 = new UidRangeParcel[]{new UidRangeParcel(FrameworkStatsLog.WIFI_BYTES_TRANSFER, Integer.MAX_VALUE)};
            synchronized (this.mRulesLock) {
                SparseIntArray uidFirewallRulesLR = getUidFirewallRulesLR(i);
                size2 = uidFirewallRulesLR.size();
                iArr = new int[size2];
                i3 = 0;
                for (int i4 = 0; i4 < size2; i4++) {
                    if (uidFirewallRulesLR.valueAt(i4) == 1) {
                        iArr[i3] = uidFirewallRulesLR.keyAt(i4);
                        i3++;
                    }
                }
            }
            if (i3 != size2) {
                iArr = Arrays.copyOf(iArr, i3);
            }
        } else {
            synchronized (this.mRulesLock) {
                SparseIntArray uidFirewallRulesLR2 = getUidFirewallRulesLR(i);
                size = uidFirewallRulesLR2.size();
                uidRangeParcelArr = new UidRangeParcel[size];
                i2 = 0;
                for (int i5 = 0; i5 < size; i5++) {
                    if (uidFirewallRulesLR2.valueAt(i5) == 2) {
                        int keyAt = uidFirewallRulesLR2.keyAt(i5);
                        uidRangeParcelArr[i2] = new UidRangeParcel(keyAt, keyAt);
                        i2++;
                    }
                }
            }
            uidRangeParcelArr2 = i2 != size ? (UidRangeParcel[]) Arrays.copyOf(uidRangeParcelArr, i2) : uidRangeParcelArr;
            iArr = new int[0];
        }
        try {
            this.mNetdService.socketDestroy(uidRangeParcelArr2, iArr);
        } catch (RemoteException | ServiceSpecificException e) {
            Slog.e("NetworkManagement", "Error closing sockets after enabling chain " + str + ": " + e);
        }
    }

    public void setFirewallChainEnabled(int i, boolean z) {
        enforceSystemUid();
        synchronized (this.mQuotaLock) {
            synchronized (this.mRulesLock) {
                if (getFirewallChainState(i) == z) {
                    return;
                }
                setFirewallChainState(i, z);
                String firewallChainName = getFirewallChainName(i);
                if (i == 0) {
                    throw new IllegalArgumentException("Bad child chain: " + firewallChainName);
                }
                try {
                    ((ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class)).setFirewallChainEnabled(i, z);
                    if (z) {
                        closeSocketsForFirewallChainLocked(i, firewallChainName);
                    }
                } catch (RuntimeException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    }

    public final String getFirewallChainName(int i) {
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i != 4) {
                        if (i == 5) {
                            return "low_power_standby";
                        }
                        throw new IllegalArgumentException("Bad child chain: " + i);
                    }
                    return "restricted";
                }
                return "powersave";
            }
            return "standby";
        }
        return "dozable";
    }

    public final int getFirewallType(int i) {
        if (i != 1) {
            if (i != 2) {
                if (i == 3 || i == 4 || i == 5) {
                    return 0;
                }
                return !isFirewallEnabled();
            }
            return 1;
        }
        return 0;
    }

    public void setFirewallUidRules(int i, int[] iArr, int[] iArr2) {
        enforceSystemUid();
        synchronized (this.mQuotaLock) {
            synchronized (this.mRulesLock) {
                SparseIntArray uidFirewallRulesLR = getUidFirewallRulesLR(i);
                SparseIntArray sparseIntArray = new SparseIntArray();
                for (int length = iArr.length - 1; length >= 0; length--) {
                    int i2 = iArr[length];
                    int i3 = iArr2[length];
                    updateFirewallUidRuleLocked(i, i2, i3);
                    sparseIntArray.put(i2, i3);
                }
                SparseIntArray sparseIntArray2 = new SparseIntArray();
                for (int size = uidFirewallRulesLR.size() - 1; size >= 0; size--) {
                    int keyAt = uidFirewallRulesLR.keyAt(size);
                    if (sparseIntArray.indexOfKey(keyAt) < 0) {
                        sparseIntArray2.put(keyAt, 0);
                    }
                }
                for (int size2 = sparseIntArray2.size() - 1; size2 >= 0; size2--) {
                    updateFirewallUidRuleLocked(i, sparseIntArray2.keyAt(size2), 0);
                }
            }
            try {
                ((ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class)).replaceFirewallChain(i, iArr);
            } catch (RuntimeException e) {
                Slog.w("NetworkManagement", "Error flushing firewall chain " + i, e);
            }
        }
    }

    public void setFirewallUidRule(int i, int i2, int i3) {
        enforceSystemUid();
        synchronized (this.mQuotaLock) {
            setFirewallUidRuleLocked(i, i2, i3);
        }
    }

    public final void setFirewallUidRuleLocked(int i, int i2, int i3) {
        if (updateFirewallUidRuleLocked(i, i2, i3)) {
            try {
                ((ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class)).setUidFirewallRule(i, i2, i3);
            } catch (RuntimeException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public final boolean updateFirewallUidRuleLocked(int i, int i2, int i3) {
        synchronized (this.mRulesLock) {
            SparseIntArray uidFirewallRulesLR = getUidFirewallRulesLR(i);
            int i4 = uidFirewallRulesLR.get(i2, 0);
            boolean z = DBG;
            if (z) {
                Slog.d("NetworkManagement", "oldRule = " + i4 + ", newRule=" + i3 + " for uid=" + i2 + " on chain " + i);
            }
            if (i4 == i3) {
                if (z) {
                    Slog.d("NetworkManagement", "!!!!! Skipping change");
                }
                return false;
            }
            String firewallRuleName = getFirewallRuleName(i, i3);
            String firewallRuleName2 = getFirewallRuleName(i, i4);
            if (i3 == 0) {
                uidFirewallRulesLR.delete(i2);
            } else {
                uidFirewallRulesLR.put(i2, i3);
            }
            return firewallRuleName.equals(firewallRuleName2) ? false : true;
        }
    }

    public final String getFirewallRuleName(int i, int i2) {
        if (getFirewallType(i) == 0) {
            if (i2 != 1) {
                return "deny";
            }
        } else if (i2 == 2) {
            return "deny";
        }
        return "allow";
    }

    @GuardedBy({"mRulesLock"})
    public final SparseIntArray getUidFirewallRulesLR(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            if (i == 5) {
                                return this.mUidFirewallLowPowerStandbyRules;
                            }
                            throw new IllegalArgumentException("Unknown chain:" + i);
                        }
                        return this.mUidFirewallRestrictedRules;
                    }
                    return this.mUidFirewallPowerSaveRules;
                }
                return this.mUidFirewallStandbyRules;
            }
            return this.mUidFirewallDozableRules;
        }
        return this.mUidFirewallRules;
    }

    public final void enforceSystemUid() {
        if (this.mDeps.getCallingUid() != 1000) {
            throw new SecurityException("Only available to AID_SYSTEM");
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, "NetworkManagement", printWriter)) {
            synchronized (this.mQuotaLock) {
                printWriter.print("Active quota ifaces: ");
                printWriter.println(this.mActiveQuotas.toString());
                printWriter.print("Active alert ifaces: ");
                printWriter.println(this.mActiveAlerts.toString());
                printWriter.print("Data saver mode: ");
                printWriter.println(this.mDataSaverMode);
                synchronized (this.mRulesLock) {
                    dumpUidRuleOnQuotaLocked(printWriter, "denied UIDs", this.mUidRejectOnMetered);
                    dumpUidRuleOnQuotaLocked(printWriter, "allowed UIDs", this.mUidAllowOnMetered);
                }
            }
            synchronized (this.mRulesLock) {
                dumpUidFirewallRule(printWriter, "", this.mUidFirewallRules);
                printWriter.print("UID firewall standby chain enabled: ");
                printWriter.println(getFirewallChainState(2));
                dumpUidFirewallRule(printWriter, "standby", this.mUidFirewallStandbyRules);
                printWriter.print("UID firewall dozable chain enabled: ");
                printWriter.println(getFirewallChainState(1));
                dumpUidFirewallRule(printWriter, "dozable", this.mUidFirewallDozableRules);
                printWriter.print("UID firewall powersave chain enabled: ");
                printWriter.println(getFirewallChainState(3));
                dumpUidFirewallRule(printWriter, "powersave", this.mUidFirewallPowerSaveRules);
                printWriter.print("UID firewall restricted mode chain enabled: ");
                printWriter.println(getFirewallChainState(4));
                dumpUidFirewallRule(printWriter, "restricted", this.mUidFirewallRestrictedRules);
                printWriter.print("UID firewall low power standby chain enabled: ");
                printWriter.println(getFirewallChainState(5));
                dumpUidFirewallRule(printWriter, "low_power_standby", this.mUidFirewallLowPowerStandbyRules);
            }
            printWriter.print("Firewall enabled: ");
            printWriter.println(this.mFirewallEnabled);
            printWriter.print("Netd service status: ");
            INetd iNetd = this.mNetdService;
            if (iNetd == null) {
                printWriter.println("disconnected");
                return;
            }
            try {
                printWriter.println(iNetd.isAlive() ? "alive" : "dead");
            } catch (RemoteException unused) {
                printWriter.println(INetd.NEXTHOP_UNREACHABLE);
            }
        }
    }

    public final void dumpUidRuleOnQuotaLocked(PrintWriter printWriter, String str, SparseBooleanArray sparseBooleanArray) {
        printWriter.print("UID bandwith control ");
        printWriter.print(str);
        printWriter.print(": [");
        int size = sparseBooleanArray.size();
        for (int i = 0; i < size; i++) {
            printWriter.print(sparseBooleanArray.keyAt(i));
            if (i < size - 1) {
                printWriter.print(",");
            }
        }
        printWriter.println("]");
    }

    public final void dumpUidFirewallRule(PrintWriter printWriter, String str, SparseIntArray sparseIntArray) {
        printWriter.print("UID firewall ");
        printWriter.print(str);
        printWriter.print(" rule: [");
        int size = sparseIntArray.size();
        for (int i = 0; i < size; i++) {
            printWriter.print(sparseIntArray.keyAt(i));
            printWriter.print(XmlUtils.STRING_ARRAY_SEPARATOR);
            printWriter.print(sparseIntArray.valueAt(i));
            if (i < size - 1) {
                printWriter.print(",");
            }
        }
        printWriter.println("]");
    }

    public final void modifyInterfaceInNetwork(boolean z, int i, String str) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        try {
            if (z) {
                this.mNetdService.networkAddInterface(i, str);
            } else {
                this.mNetdService.networkRemoveInterface(i, str);
            }
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void allowProtect(int i) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        try {
            this.mNetdService.networkSetProtectAllow(i);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void denyProtect(int i) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        try {
            this.mNetdService.networkSetProtectDeny(i);
        } catch (RemoteException | ServiceSpecificException e) {
            throw new IllegalStateException(e);
        }
    }

    public void addInterfaceToLocalNetwork(String str, List<RouteInfo> list) {
        modifyInterfaceInNetwork(true, 99, str);
        NetdUtils.addRoutesToLocalNetwork(this.mNetdService, str, list);
    }

    public void removeInterfaceFromLocalNetwork(String str) {
        modifyInterfaceInNetwork(false, 99, str);
    }

    public int removeRoutesFromLocalNetwork(List<RouteInfo> list) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        return NetdUtils.removeRoutesFromLocalNetwork(this.mNetdService, list);
    }

    @EnforcePermission("android.permission.OBSERVE_NETWORK_POLICY")
    public boolean isNetworkRestricted(int i) {
        super.isNetworkRestricted_enforcePermission();
        return isNetworkRestrictedInternal(i);
    }

    public final boolean isNetworkRestrictedInternal(int i) {
        synchronized (this.mRulesLock) {
            if (getFirewallChainState(2) && this.mUidFirewallStandbyRules.get(i) == 2) {
                if (DBG) {
                    Slog.d("NetworkManagement", "Uid " + i + " restricted because of app standby mode");
                }
                return true;
            } else if (getFirewallChainState(1) && this.mUidFirewallDozableRules.get(i) != 1) {
                if (DBG) {
                    Slog.d("NetworkManagement", "Uid " + i + " restricted because of device idle mode");
                }
                return true;
            } else if (getFirewallChainState(3) && this.mUidFirewallPowerSaveRules.get(i) != 1) {
                if (DBG) {
                    Slog.d("NetworkManagement", "Uid " + i + " restricted because of power saver mode");
                }
                return true;
            } else if (getFirewallChainState(4) && this.mUidFirewallRestrictedRules.get(i) != 1) {
                if (DBG) {
                    Slog.d("NetworkManagement", "Uid " + i + " restricted because of restricted mode");
                }
                return true;
            } else if (getFirewallChainState(5) && this.mUidFirewallLowPowerStandbyRules.get(i) != 1) {
                if (DBG) {
                    Slog.d("NetworkManagement", "Uid " + i + " restricted because of low power standby");
                }
                return true;
            } else if (this.mUidRejectOnMetered.get(i)) {
                if (DBG) {
                    Slog.d("NetworkManagement", "Uid " + i + " restricted because of no metered data in the background");
                }
                return true;
            } else if (!this.mDataSaverMode || this.mUidAllowOnMetered.get(i)) {
                return false;
            } else {
                if (DBG) {
                    Slog.d("NetworkManagement", "Uid " + i + " restricted because of data saver mode");
                }
                return true;
            }
        }
    }

    public final void setFirewallChainState(int i, boolean z) {
        synchronized (this.mRulesLock) {
            this.mFirewallChainStates.put(i, z);
        }
    }

    public final boolean getFirewallChainState(int i) {
        boolean z;
        synchronized (this.mRulesLock) {
            z = this.mFirewallChainStates.get(i);
        }
        return z;
    }

    /* loaded from: classes2.dex */
    public class LocalService extends NetworkManagementInternal {
        public LocalService() {
        }

        @Override // com.android.server.net.NetworkManagementInternal
        public boolean isNetworkRestrictedForUid(int i) {
            return NetworkManagementService.this.isNetworkRestrictedInternal(i);
        }
    }
}
