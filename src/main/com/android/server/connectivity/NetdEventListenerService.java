package com.android.server.connectivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.INetdEventCallback;
import android.net.MacAddress;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.metrics.ConnectStats;
import android.net.metrics.DnsEvent;
import android.net.metrics.INetdEventListener;
import android.net.metrics.NetworkMetrics;
import android.net.metrics.WakeupEvent;
import android.net.metrics.WakeupStats;
import android.os.RemoteException;
import android.os.SystemClock;
import android.p005os.BatteryStatsInternal;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.BitUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.RingBuffer;
import com.android.internal.util.TokenBucket;
import com.android.net.module.util.BaseNetdEventListener;
import com.android.server.LocalServices;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.connectivity.metrics.nano.IpConnectivityLogClass;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
/* loaded from: classes.dex */
public class NetdEventListenerService extends BaseNetdEventListener {
    @VisibleForTesting
    static final int WAKEUP_EVENT_BUFFER_LENGTH = 1024;
    @VisibleForTesting
    static final String WAKEUP_EVENT_PREFIX_DELIM = ":";
    public final TransportForNetIdNetworkCallback mCallback;
    public final ConnectivityManager mCm;
    @GuardedBy({"this"})
    public final TokenBucket mConnectTb;
    @GuardedBy({"this"})
    public long mLastSnapshot;
    @GuardedBy({"this"})
    public INetdEventCallback[] mNetdEventCallbackList;
    @GuardedBy({"this"})
    public final SparseArray<NetworkMetrics> mNetworkMetrics;
    @GuardedBy({"this"})
    public final RingBuffer<NetworkMetricsSnapshot> mNetworkMetricsSnapshots;
    @GuardedBy({"this"})
    public final RingBuffer<WakeupEvent> mWakeupEvents;
    @GuardedBy({"this"})
    public final ArrayMap<String, WakeupStats> mWakeupStats;
    public static final String TAG = NetdEventListenerService.class.getSimpleName();
    @GuardedBy({"this"})
    public static final int[] ALLOWED_CALLBACK_TYPES = {0, 1, 2};

    @Override // com.android.net.module.util.BaseNetdEventListener, android.net.metrics.INetdEventListener
    public String getInterfaceHash() {
        return INetdEventListener.HASH;
    }

    @Override // com.android.net.module.util.BaseNetdEventListener, android.net.metrics.INetdEventListener
    public int getInterfaceVersion() {
        return 1;
    }

    public synchronized boolean addNetdEventCallback(int i, INetdEventCallback iNetdEventCallback) {
        if (!isValidCallerType(i)) {
            String str = TAG;
            Log.e(str, "Invalid caller type: " + i);
            return false;
        }
        this.mNetdEventCallbackList[i] = iNetdEventCallback;
        return true;
    }

    public synchronized boolean removeNetdEventCallback(int i) {
        if (!isValidCallerType(i)) {
            String str = TAG;
            Log.e(str, "Invalid caller type: " + i);
            return false;
        }
        this.mNetdEventCallbackList[i] = null;
        return true;
    }

    public static boolean isValidCallerType(int i) {
        int i2 = 0;
        while (true) {
            int[] iArr = ALLOWED_CALLBACK_TYPES;
            if (i2 >= iArr.length) {
                return false;
            }
            if (i == iArr[i2]) {
                return true;
            }
            i2++;
        }
    }

    public NetdEventListenerService(Context context) {
        this((ConnectivityManager) context.getSystemService(ConnectivityManager.class));
    }

    @VisibleForTesting
    public NetdEventListenerService(ConnectivityManager connectivityManager) {
        this.mNetworkMetrics = new SparseArray<>();
        this.mNetworkMetricsSnapshots = new RingBuffer<>(NetworkMetricsSnapshot.class, 48);
        this.mLastSnapshot = 0L;
        this.mWakeupStats = new ArrayMap<>();
        this.mWakeupEvents = new RingBuffer<>(WakeupEvent.class, 1024);
        this.mConnectTb = new TokenBucket(15000, 5000);
        TransportForNetIdNetworkCallback transportForNetIdNetworkCallback = new TransportForNetIdNetworkCallback();
        this.mCallback = transportForNetIdNetworkCallback;
        this.mNetdEventCallbackList = new INetdEventCallback[ALLOWED_CALLBACK_TYPES.length];
        this.mCm = connectivityManager;
        connectivityManager.registerNetworkCallback(new NetworkRequest.Builder().clearCapabilities().build(), transportForNetIdNetworkCallback);
    }

    public static long projectSnapshotTime(long j) {
        return (j / BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS) * BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS;
    }

    public final NetworkMetrics getMetricsForNetwork(long j, int i) {
        NetworkMetrics networkMetrics = this.mNetworkMetrics.get(i);
        NetworkCapabilities networkCapabilities = this.mCallback.getNetworkCapabilities(i);
        long packBits = networkCapabilities != null ? BitUtils.packBits(networkCapabilities.getTransportTypes()) : 0L;
        boolean z = (networkMetrics == null || networkCapabilities == null || networkMetrics.transports == packBits) ? false : true;
        collectPendingMetricsSnapshot(j, z);
        if (networkMetrics == null || z) {
            NetworkMetrics networkMetrics2 = new NetworkMetrics(i, packBits, this.mConnectTb);
            this.mNetworkMetrics.put(i, networkMetrics2);
            return networkMetrics2;
        }
        return networkMetrics;
    }

    public final NetworkMetricsSnapshot[] getNetworkMetricsSnapshots() {
        collectPendingMetricsSnapshot(System.currentTimeMillis(), false);
        return (NetworkMetricsSnapshot[]) this.mNetworkMetricsSnapshots.toArray();
    }

    public final void collectPendingMetricsSnapshot(long j, boolean z) {
        if (z || Math.abs(j - this.mLastSnapshot) > BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS) {
            long projectSnapshotTime = projectSnapshotTime(j);
            this.mLastSnapshot = projectSnapshotTime;
            NetworkMetricsSnapshot collect = NetworkMetricsSnapshot.collect(projectSnapshotTime, this.mNetworkMetrics);
            if (collect.stats.isEmpty()) {
                return;
            }
            this.mNetworkMetricsSnapshots.append(collect);
        }
    }

    @Override // com.android.net.module.util.BaseNetdEventListener, android.net.metrics.INetdEventListener
    public synchronized void onDnsEvent(int i, int i2, int i3, int i4, String str, String[] strArr, int i5, int i6) {
        int i7;
        int i8;
        INetdEventCallback[] iNetdEventCallbackArr;
        long currentTimeMillis = System.currentTimeMillis();
        getMetricsForNetwork(currentTimeMillis, i).addDnsResult(i2, i3, i4);
        INetdEventCallback[] iNetdEventCallbackArr2 = this.mNetdEventCallbackList;
        int length = iNetdEventCallbackArr2.length;
        int i9 = 0;
        while (i9 < length) {
            INetdEventCallback iNetdEventCallback = iNetdEventCallbackArr2[i9];
            if (iNetdEventCallback != null) {
                i7 = length;
                i8 = i9;
                iNetdEventCallbackArr = iNetdEventCallbackArr2;
                try {
                    iNetdEventCallback.onDnsEvent(i, i2, i3, str, strArr, i5, currentTimeMillis, i6);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            } else {
                i7 = length;
                i8 = i9;
                iNetdEventCallbackArr = iNetdEventCallbackArr2;
            }
            i9 = i8 + 1;
            length = i7;
            iNetdEventCallbackArr2 = iNetdEventCallbackArr;
        }
    }

    @Override // com.android.net.module.util.BaseNetdEventListener, android.net.metrics.INetdEventListener
    public synchronized void onNat64PrefixEvent(int i, boolean z, String str, int i2) {
        INetdEventCallback[] iNetdEventCallbackArr;
        for (INetdEventCallback iNetdEventCallback : this.mNetdEventCallbackList) {
            if (iNetdEventCallback != null) {
                try {
                    iNetdEventCallback.onNat64PrefixEvent(i, z, str, i2);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
        }
    }

    @Override // com.android.net.module.util.BaseNetdEventListener, android.net.metrics.INetdEventListener
    public synchronized void onPrivateDnsValidationEvent(int i, String str, String str2, boolean z) {
        INetdEventCallback[] iNetdEventCallbackArr;
        for (INetdEventCallback iNetdEventCallback : this.mNetdEventCallbackList) {
            if (iNetdEventCallback != null) {
                try {
                    iNetdEventCallback.onPrivateDnsValidationEvent(i, str, str2, z);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
        }
    }

    @Override // com.android.net.module.util.BaseNetdEventListener, android.net.metrics.INetdEventListener
    public synchronized void onConnectEvent(int i, int i2, int i3, String str, int i4, int i5) {
        INetdEventCallback[] iNetdEventCallbackArr;
        long currentTimeMillis = System.currentTimeMillis();
        getMetricsForNetwork(currentTimeMillis, i).addConnectResult(i2, i3, str);
        for (INetdEventCallback iNetdEventCallback : this.mNetdEventCallbackList) {
            if (iNetdEventCallback != null) {
                try {
                    iNetdEventCallback.onConnectEvent(str, i4, currentTimeMillis, i5);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
        }
    }

    @Override // com.android.net.module.util.BaseNetdEventListener, android.net.metrics.INetdEventListener
    public synchronized void onWakeupEvent(String str, int i, int i2, int i3, byte[] bArr, String str2, String str3, int i4, int i5, long j) {
        String[] split = str.split(":");
        if (split.length != 2) {
            throw new IllegalArgumentException("Prefix " + str + " required in format <nethandle>:<interface>");
        }
        WakeupEvent wakeupEvent = new WakeupEvent();
        wakeupEvent.iface = split[1];
        wakeupEvent.uid = i;
        wakeupEvent.ethertype = i2;
        wakeupEvent.dstHwAddr = MacAddress.fromBytes(bArr);
        wakeupEvent.srcIp = str2;
        wakeupEvent.dstIp = str3;
        wakeupEvent.ipNextHeader = i3;
        wakeupEvent.srcPort = i4;
        wakeupEvent.dstPort = i5;
        if (j > 0) {
            wakeupEvent.timestampMs = j / 1000000;
        } else {
            wakeupEvent.timestampMs = System.currentTimeMillis();
        }
        addWakeupEvent(wakeupEvent);
        BatteryStatsInternal batteryStatsInternal = (BatteryStatsInternal) LocalServices.getService(BatteryStatsInternal.class);
        if (batteryStatsInternal != null) {
            long parseLong = Long.parseLong(split[0]);
            batteryStatsInternal.noteCpuWakingNetworkPacket(Network.fromNetworkHandle(parseLong), (SystemClock.elapsedRealtime() + wakeupEvent.timestampMs) - System.currentTimeMillis(), wakeupEvent.uid);
        }
        FrameworkStatsLog.write(44, i, wakeupEvent.iface, i2, wakeupEvent.dstHwAddr.toString(), str2, str3, i3, i4, i5);
    }

    @Override // com.android.net.module.util.BaseNetdEventListener, android.net.metrics.INetdEventListener
    public synchronized void onTcpSocketStatsEvent(int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4, int[] iArr5) {
        if (iArr.length == iArr2.length && iArr.length == iArr3.length && iArr.length == iArr4.length && iArr.length == iArr5.length) {
            long currentTimeMillis = System.currentTimeMillis();
            for (int i = 0; i < iArr.length; i++) {
                int i2 = iArr[i];
                getMetricsForNetwork(currentTimeMillis, i2).addTcpStatsResult(iArr2[i], iArr3[i], iArr4[i], iArr5[i]);
            }
            return;
        }
        Log.e(TAG, "Mismatched lengths of TCP socket stats data arrays");
    }

    public final void addWakeupEvent(WakeupEvent wakeupEvent) {
        String str = wakeupEvent.iface;
        this.mWakeupEvents.append(wakeupEvent);
        WakeupStats wakeupStats = this.mWakeupStats.get(str);
        if (wakeupStats == null) {
            wakeupStats = new WakeupStats(str);
            this.mWakeupStats.put(str, wakeupStats);
        }
        wakeupStats.countEvent(wakeupEvent);
    }

    public synchronized void flushStatistics(List<IpConnectivityLogClass.IpConnectivityEvent> list) {
        for (int i = 0; i < this.mNetworkMetrics.size(); i++) {
            ConnectStats connectStats = this.mNetworkMetrics.valueAt(i).connectMetrics;
            if (connectStats.eventCount != 0) {
                list.add(IpConnectivityEventBuilder.toProto(connectStats));
            }
        }
        for (int i2 = 0; i2 < this.mNetworkMetrics.size(); i2++) {
            DnsEvent dnsEvent = this.mNetworkMetrics.valueAt(i2).dnsMetrics;
            if (dnsEvent.eventCount != 0) {
                list.add(IpConnectivityEventBuilder.toProto(dnsEvent));
            }
        }
        for (int i3 = 0; i3 < this.mWakeupStats.size(); i3++) {
            list.add(IpConnectivityEventBuilder.toProto(this.mWakeupStats.valueAt(i3)));
        }
        this.mNetworkMetrics.clear();
        this.mWakeupStats.clear();
    }

    public synchronized void list(PrintWriter printWriter) {
        printWriter.println("dns/connect events:");
        for (int i = 0; i < this.mNetworkMetrics.size(); i++) {
            printWriter.println(this.mNetworkMetrics.valueAt(i).connectMetrics);
        }
        for (int i2 = 0; i2 < this.mNetworkMetrics.size(); i2++) {
            printWriter.println(this.mNetworkMetrics.valueAt(i2).dnsMetrics);
        }
        printWriter.println("");
        printWriter.println("network statistics:");
        for (NetworkMetricsSnapshot networkMetricsSnapshot : getNetworkMetricsSnapshots()) {
            printWriter.println(networkMetricsSnapshot);
        }
        printWriter.println("");
        printWriter.println("packet wakeup events:");
        for (int i3 = 0; i3 < this.mWakeupStats.size(); i3++) {
            printWriter.println(this.mWakeupStats.valueAt(i3));
        }
        for (WakeupEvent wakeupEvent : (WakeupEvent[]) this.mWakeupEvents.toArray()) {
            printWriter.println(wakeupEvent);
        }
    }

    public synchronized List<IpConnectivityLogClass.IpConnectivityEvent> listAsProtos() {
        ArrayList arrayList;
        arrayList = new ArrayList();
        for (int i = 0; i < this.mNetworkMetrics.size(); i++) {
            arrayList.add(IpConnectivityEventBuilder.toProto(this.mNetworkMetrics.valueAt(i).connectMetrics));
        }
        for (int i2 = 0; i2 < this.mNetworkMetrics.size(); i2++) {
            arrayList.add(IpConnectivityEventBuilder.toProto(this.mNetworkMetrics.valueAt(i2).dnsMetrics));
        }
        for (int i3 = 0; i3 < this.mWakeupStats.size(); i3++) {
            arrayList.add(IpConnectivityEventBuilder.toProto(this.mWakeupStats.valueAt(i3)));
        }
        return arrayList;
    }

    /* loaded from: classes.dex */
    public static class NetworkMetricsSnapshot {
        public List<NetworkMetrics.Summary> stats = new ArrayList();
        public long timeMs;

        public static NetworkMetricsSnapshot collect(long j, SparseArray<NetworkMetrics> sparseArray) {
            NetworkMetricsSnapshot networkMetricsSnapshot = new NetworkMetricsSnapshot();
            networkMetricsSnapshot.timeMs = j;
            for (int i = 0; i < sparseArray.size(); i++) {
                NetworkMetrics.Summary pendingStats = sparseArray.valueAt(i).getPendingStats();
                if (pendingStats != null) {
                    networkMetricsSnapshot.stats.add(pendingStats);
                }
            }
            return networkMetricsSnapshot;
        }

        public String toString() {
            StringJoiner stringJoiner = new StringJoiner(", ");
            for (NetworkMetrics.Summary summary : this.stats) {
                stringJoiner.add(summary.toString());
            }
            return String.format("%tT.%tL: %s", Long.valueOf(this.timeMs), Long.valueOf(this.timeMs), stringJoiner.toString());
        }
    }

    /* loaded from: classes.dex */
    public class TransportForNetIdNetworkCallback extends ConnectivityManager.NetworkCallback {
        public final SparseArray<NetworkCapabilities> mCapabilities;

        public TransportForNetIdNetworkCallback() {
            this.mCapabilities = new SparseArray<>();
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            synchronized (this.mCapabilities) {
                this.mCapabilities.put(network.getNetId(), networkCapabilities);
            }
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onLost(Network network) {
            synchronized (this.mCapabilities) {
                this.mCapabilities.remove(network.getNetId());
            }
        }

        public NetworkCapabilities getNetworkCapabilities(int i) {
            NetworkCapabilities networkCapabilities;
            synchronized (this.mCapabilities) {
                networkCapabilities = this.mCapabilities.get(i);
            }
            return networkCapabilities;
        }
    }
}
