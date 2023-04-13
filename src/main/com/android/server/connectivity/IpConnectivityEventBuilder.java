package com.android.server.connectivity;

import android.net.ConnectivityMetricsEvent;
import android.net.metrics.ApfProgramEvent;
import android.net.metrics.ApfStats;
import android.net.metrics.ConnectStats;
import android.net.metrics.DefaultNetworkEvent;
import android.net.metrics.DhcpClientEvent;
import android.net.metrics.DhcpErrorEvent;
import android.net.metrics.DnsEvent;
import android.net.metrics.IpManagerEvent;
import android.net.metrics.IpReachabilityEvent;
import android.net.metrics.NetworkEvent;
import android.net.metrics.RaEvent;
import android.net.metrics.ValidationProbeEvent;
import android.net.metrics.WakeupStats;
import android.os.Parcelable;
import android.util.SparseIntArray;
import com.android.server.connectivity.metrics.nano.IpConnectivityLogClass;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public final class IpConnectivityEventBuilder {
    public static final int[] IFNAME_LINKLAYERS;
    public static final String[] IFNAME_PREFIXES;
    public static final SparseIntArray TRANSPORT_LINKLAYER_MAP;

    public static boolean isBitSet(int i, int i2) {
        return (i & (1 << i2)) != 0;
    }

    public static byte[] serialize(int i, List<IpConnectivityLogClass.IpConnectivityEvent> list) throws IOException {
        IpConnectivityLogClass.IpConnectivityLog ipConnectivityLog = new IpConnectivityLogClass.IpConnectivityLog();
        IpConnectivityLogClass.IpConnectivityEvent[] ipConnectivityEventArr = (IpConnectivityLogClass.IpConnectivityEvent[]) list.toArray(new IpConnectivityLogClass.IpConnectivityEvent[list.size()]);
        ipConnectivityLog.events = ipConnectivityEventArr;
        ipConnectivityLog.droppedEvents = i;
        if (ipConnectivityEventArr.length > 0 || i > 0) {
            ipConnectivityLog.version = 2;
        }
        return IpConnectivityLogClass.IpConnectivityLog.toByteArray(ipConnectivityLog);
    }

    public static List<IpConnectivityLogClass.IpConnectivityEvent> toProto(List<ConnectivityMetricsEvent> list) {
        ArrayList arrayList = new ArrayList(list.size());
        for (ConnectivityMetricsEvent connectivityMetricsEvent : list) {
            IpConnectivityLogClass.IpConnectivityEvent proto = toProto(connectivityMetricsEvent);
            if (proto != null) {
                arrayList.add(proto);
            }
        }
        return arrayList;
    }

    public static IpConnectivityLogClass.IpConnectivityEvent toProto(ConnectivityMetricsEvent connectivityMetricsEvent) {
        IpConnectivityLogClass.IpConnectivityEvent buildEvent = buildEvent(connectivityMetricsEvent.netId, connectivityMetricsEvent.transports, connectivityMetricsEvent.ifname);
        buildEvent.timeMs = connectivityMetricsEvent.timestamp;
        if (setEvent(buildEvent, connectivityMetricsEvent.data)) {
            return buildEvent;
        }
        return null;
    }

    public static IpConnectivityLogClass.IpConnectivityEvent toProto(ConnectStats connectStats) {
        IpConnectivityLogClass.ConnectStatistics connectStatistics = new IpConnectivityLogClass.ConnectStatistics();
        connectStatistics.connectCount = connectStats.connectCount;
        connectStatistics.connectBlockingCount = connectStats.connectBlockingCount;
        connectStatistics.ipv6AddrCount = connectStats.ipv6ConnectCount;
        connectStatistics.latenciesMs = connectStats.latencies.toArray();
        connectStatistics.errnosCounters = toPairArray(connectStats.errnos);
        IpConnectivityLogClass.IpConnectivityEvent buildEvent = buildEvent(connectStats.netId, connectStats.transports, null);
        buildEvent.setConnectStatistics(connectStatistics);
        return buildEvent;
    }

    public static IpConnectivityLogClass.IpConnectivityEvent toProto(DnsEvent dnsEvent) {
        IpConnectivityLogClass.DNSLookupBatch dNSLookupBatch = new IpConnectivityLogClass.DNSLookupBatch();
        dnsEvent.resize(dnsEvent.eventCount);
        dNSLookupBatch.eventTypes = bytesToInts(dnsEvent.eventTypes);
        dNSLookupBatch.returnCodes = bytesToInts(dnsEvent.returnCodes);
        dNSLookupBatch.latenciesMs = dnsEvent.latenciesMs;
        IpConnectivityLogClass.IpConnectivityEvent buildEvent = buildEvent(dnsEvent.netId, dnsEvent.transports, null);
        buildEvent.setDnsLookupBatch(dNSLookupBatch);
        return buildEvent;
    }

    public static IpConnectivityLogClass.IpConnectivityEvent toProto(WakeupStats wakeupStats) {
        IpConnectivityLogClass.WakeupStats wakeupStats2 = new IpConnectivityLogClass.WakeupStats();
        wakeupStats.updateDuration();
        wakeupStats2.durationSec = wakeupStats.durationSec;
        wakeupStats2.totalWakeups = wakeupStats.totalWakeups;
        wakeupStats2.rootWakeups = wakeupStats.rootWakeups;
        wakeupStats2.systemWakeups = wakeupStats.systemWakeups;
        wakeupStats2.nonApplicationWakeups = wakeupStats.nonApplicationWakeups;
        wakeupStats2.applicationWakeups = wakeupStats.applicationWakeups;
        wakeupStats2.noUidWakeups = wakeupStats.noUidWakeups;
        wakeupStats2.l2UnicastCount = wakeupStats.l2UnicastCount;
        wakeupStats2.l2MulticastCount = wakeupStats.l2MulticastCount;
        wakeupStats2.l2BroadcastCount = wakeupStats.l2BroadcastCount;
        wakeupStats2.ethertypeCounts = toPairArray(wakeupStats.ethertypes);
        wakeupStats2.ipNextHeaderCounts = toPairArray(wakeupStats.ipNextHeaders);
        IpConnectivityLogClass.IpConnectivityEvent buildEvent = buildEvent(0, 0L, wakeupStats.iface);
        buildEvent.setWakeupStats(wakeupStats2);
        return buildEvent;
    }

    public static IpConnectivityLogClass.IpConnectivityEvent toProto(DefaultNetworkEvent defaultNetworkEvent) {
        IpConnectivityLogClass.DefaultNetworkEvent defaultNetworkEvent2 = new IpConnectivityLogClass.DefaultNetworkEvent();
        defaultNetworkEvent2.finalScore = defaultNetworkEvent.finalScore;
        defaultNetworkEvent2.initialScore = defaultNetworkEvent.initialScore;
        defaultNetworkEvent2.ipSupport = ipSupportOf(defaultNetworkEvent);
        defaultNetworkEvent2.defaultNetworkDurationMs = defaultNetworkEvent.durationMs;
        defaultNetworkEvent2.validationDurationMs = defaultNetworkEvent.validatedMs;
        defaultNetworkEvent2.previousDefaultNetworkLinkLayer = transportsToLinkLayer(defaultNetworkEvent.previousTransports);
        IpConnectivityLogClass.IpConnectivityEvent buildEvent = buildEvent(defaultNetworkEvent.netId, defaultNetworkEvent.transports, null);
        if (defaultNetworkEvent.transports == 0) {
            buildEvent.linkLayer = 5;
        }
        buildEvent.setDefaultNetworkEvent(defaultNetworkEvent2);
        return buildEvent;
    }

    public static IpConnectivityLogClass.IpConnectivityEvent buildEvent(int i, long j, String str) {
        IpConnectivityLogClass.IpConnectivityEvent ipConnectivityEvent = new IpConnectivityLogClass.IpConnectivityEvent();
        ipConnectivityEvent.networkId = i;
        ipConnectivityEvent.transports = j;
        if (str != null) {
            ipConnectivityEvent.ifName = str;
        }
        inferLinkLayer(ipConnectivityEvent);
        return ipConnectivityEvent;
    }

    public static boolean setEvent(IpConnectivityLogClass.IpConnectivityEvent ipConnectivityEvent, Parcelable parcelable) {
        if (parcelable instanceof DhcpErrorEvent) {
            setDhcpErrorEvent(ipConnectivityEvent, (DhcpErrorEvent) parcelable);
            return true;
        } else if (parcelable instanceof DhcpClientEvent) {
            setDhcpClientEvent(ipConnectivityEvent, (DhcpClientEvent) parcelable);
            return true;
        } else if (parcelable instanceof IpManagerEvent) {
            setIpManagerEvent(ipConnectivityEvent, (IpManagerEvent) parcelable);
            return true;
        } else if (parcelable instanceof IpReachabilityEvent) {
            setIpReachabilityEvent(ipConnectivityEvent, (IpReachabilityEvent) parcelable);
            return true;
        } else if (parcelable instanceof NetworkEvent) {
            setNetworkEvent(ipConnectivityEvent, (NetworkEvent) parcelable);
            return true;
        } else if (parcelable instanceof ValidationProbeEvent) {
            setValidationProbeEvent(ipConnectivityEvent, (ValidationProbeEvent) parcelable);
            return true;
        } else if (parcelable instanceof ApfProgramEvent) {
            setApfProgramEvent(ipConnectivityEvent, (ApfProgramEvent) parcelable);
            return true;
        } else if (parcelable instanceof ApfStats) {
            setApfStats(ipConnectivityEvent, (ApfStats) parcelable);
            return true;
        } else if (parcelable instanceof RaEvent) {
            setRaEvent(ipConnectivityEvent, (RaEvent) parcelable);
            return true;
        } else {
            return false;
        }
    }

    public static void setDhcpErrorEvent(IpConnectivityLogClass.IpConnectivityEvent ipConnectivityEvent, DhcpErrorEvent dhcpErrorEvent) {
        IpConnectivityLogClass.DHCPEvent dHCPEvent = new IpConnectivityLogClass.DHCPEvent();
        dHCPEvent.setErrorCode(dhcpErrorEvent.errorCode);
        ipConnectivityEvent.setDhcpEvent(dHCPEvent);
    }

    public static void setDhcpClientEvent(IpConnectivityLogClass.IpConnectivityEvent ipConnectivityEvent, DhcpClientEvent dhcpClientEvent) {
        IpConnectivityLogClass.DHCPEvent dHCPEvent = new IpConnectivityLogClass.DHCPEvent();
        dHCPEvent.setStateTransition(dhcpClientEvent.msg);
        dHCPEvent.durationMs = dhcpClientEvent.durationMs;
        ipConnectivityEvent.setDhcpEvent(dHCPEvent);
    }

    public static void setIpManagerEvent(IpConnectivityLogClass.IpConnectivityEvent ipConnectivityEvent, IpManagerEvent ipManagerEvent) {
        IpConnectivityLogClass.IpProvisioningEvent ipProvisioningEvent = new IpConnectivityLogClass.IpProvisioningEvent();
        ipProvisioningEvent.eventType = ipManagerEvent.eventType;
        ipProvisioningEvent.latencyMs = (int) ipManagerEvent.durationMs;
        ipConnectivityEvent.setIpProvisioningEvent(ipProvisioningEvent);
    }

    public static void setIpReachabilityEvent(IpConnectivityLogClass.IpConnectivityEvent ipConnectivityEvent, IpReachabilityEvent ipReachabilityEvent) {
        IpConnectivityLogClass.IpReachabilityEvent ipReachabilityEvent2 = new IpConnectivityLogClass.IpReachabilityEvent();
        ipReachabilityEvent2.eventType = ipReachabilityEvent.eventType;
        ipConnectivityEvent.setIpReachabilityEvent(ipReachabilityEvent2);
    }

    public static void setNetworkEvent(IpConnectivityLogClass.IpConnectivityEvent ipConnectivityEvent, NetworkEvent networkEvent) {
        IpConnectivityLogClass.NetworkEvent networkEvent2 = new IpConnectivityLogClass.NetworkEvent();
        networkEvent2.eventType = networkEvent.eventType;
        networkEvent2.latencyMs = (int) networkEvent.durationMs;
        ipConnectivityEvent.setNetworkEvent(networkEvent2);
    }

    public static void setValidationProbeEvent(IpConnectivityLogClass.IpConnectivityEvent ipConnectivityEvent, ValidationProbeEvent validationProbeEvent) {
        IpConnectivityLogClass.ValidationProbeEvent validationProbeEvent2 = new IpConnectivityLogClass.ValidationProbeEvent();
        validationProbeEvent2.latencyMs = (int) validationProbeEvent.durationMs;
        validationProbeEvent2.probeType = validationProbeEvent.probeType;
        validationProbeEvent2.probeResult = validationProbeEvent.returnCode;
        ipConnectivityEvent.setValidationProbeEvent(validationProbeEvent2);
    }

    public static void setApfProgramEvent(IpConnectivityLogClass.IpConnectivityEvent ipConnectivityEvent, ApfProgramEvent apfProgramEvent) {
        IpConnectivityLogClass.ApfProgramEvent apfProgramEvent2 = new IpConnectivityLogClass.ApfProgramEvent();
        apfProgramEvent2.lifetime = apfProgramEvent.lifetime;
        apfProgramEvent2.effectiveLifetime = apfProgramEvent.actualLifetime;
        apfProgramEvent2.filteredRas = apfProgramEvent.filteredRas;
        apfProgramEvent2.currentRas = apfProgramEvent.currentRas;
        apfProgramEvent2.programLength = apfProgramEvent.programLength;
        if (isBitSet(apfProgramEvent.flags, 0)) {
            apfProgramEvent2.dropMulticast = true;
        }
        if (isBitSet(apfProgramEvent.flags, 1)) {
            apfProgramEvent2.hasIpv4Addr = true;
        }
        ipConnectivityEvent.setApfProgramEvent(apfProgramEvent2);
    }

    public static void setApfStats(IpConnectivityLogClass.IpConnectivityEvent ipConnectivityEvent, ApfStats apfStats) {
        IpConnectivityLogClass.ApfStatistics apfStatistics = new IpConnectivityLogClass.ApfStatistics();
        apfStatistics.durationMs = apfStats.durationMs;
        apfStatistics.receivedRas = apfStats.receivedRas;
        apfStatistics.matchingRas = apfStats.matchingRas;
        apfStatistics.droppedRas = apfStats.droppedRas;
        apfStatistics.zeroLifetimeRas = apfStats.zeroLifetimeRas;
        apfStatistics.parseErrors = apfStats.parseErrors;
        apfStatistics.programUpdates = apfStats.programUpdates;
        apfStatistics.programUpdatesAll = apfStats.programUpdatesAll;
        apfStatistics.programUpdatesAllowingMulticast = apfStats.programUpdatesAllowingMulticast;
        apfStatistics.maxProgramSize = apfStats.maxProgramSize;
        ipConnectivityEvent.setApfStatistics(apfStatistics);
    }

    public static void setRaEvent(IpConnectivityLogClass.IpConnectivityEvent ipConnectivityEvent, RaEvent raEvent) {
        IpConnectivityLogClass.RaEvent raEvent2 = new IpConnectivityLogClass.RaEvent();
        raEvent2.routerLifetime = raEvent.routerLifetime;
        raEvent2.prefixValidLifetime = raEvent.prefixValidLifetime;
        raEvent2.prefixPreferredLifetime = raEvent.prefixPreferredLifetime;
        raEvent2.routeInfoLifetime = raEvent.routeInfoLifetime;
        raEvent2.rdnssLifetime = raEvent.rdnssLifetime;
        raEvent2.dnsslLifetime = raEvent.dnsslLifetime;
        ipConnectivityEvent.setRaEvent(raEvent2);
    }

    public static int[] bytesToInts(byte[] bArr) {
        int[] iArr = new int[bArr.length];
        for (int i = 0; i < bArr.length; i++) {
            iArr[i] = bArr[i] & 255;
        }
        return iArr;
    }

    public static IpConnectivityLogClass.Pair[] toPairArray(SparseIntArray sparseIntArray) {
        int size = sparseIntArray.size();
        IpConnectivityLogClass.Pair[] pairArr = new IpConnectivityLogClass.Pair[size];
        for (int i = 0; i < size; i++) {
            IpConnectivityLogClass.Pair pair = new IpConnectivityLogClass.Pair();
            pair.key = sparseIntArray.keyAt(i);
            pair.value = sparseIntArray.valueAt(i);
            pairArr[i] = pair;
        }
        return pairArr;
    }

    public static int ipSupportOf(DefaultNetworkEvent defaultNetworkEvent) {
        boolean z = defaultNetworkEvent.ipv4;
        if (z && defaultNetworkEvent.ipv6) {
            return 3;
        }
        if (defaultNetworkEvent.ipv6) {
            return 2;
        }
        return z ? 1 : 0;
    }

    public static void inferLinkLayer(IpConnectivityLogClass.IpConnectivityEvent ipConnectivityEvent) {
        int ifnameToLinkLayer;
        long j = ipConnectivityEvent.transports;
        if (j != 0) {
            ifnameToLinkLayer = transportsToLinkLayer(j);
        } else {
            String str = ipConnectivityEvent.ifName;
            ifnameToLinkLayer = str != null ? ifnameToLinkLayer(str) : 0;
        }
        if (ifnameToLinkLayer == 0) {
            return;
        }
        ipConnectivityEvent.linkLayer = ifnameToLinkLayer;
        ipConnectivityEvent.ifName = "";
    }

    public static int transportsToLinkLayer(long j) {
        int bitCount = Long.bitCount(j);
        if (bitCount != 0) {
            if (bitCount != 1) {
                return 6;
            }
            return TRANSPORT_LINKLAYER_MAP.get(Long.numberOfTrailingZeros(j), 0);
        }
        return 0;
    }

    static {
        SparseIntArray sparseIntArray = new SparseIntArray();
        TRANSPORT_LINKLAYER_MAP = sparseIntArray;
        sparseIntArray.append(0, 2);
        sparseIntArray.append(1, 4);
        sparseIntArray.append(2, 1);
        sparseIntArray.append(3, 3);
        sparseIntArray.append(4, 0);
        sparseIntArray.append(5, 8);
        sparseIntArray.append(6, 9);
        IFNAME_PREFIXES = r10;
        IFNAME_LINKLAYERS = r11;
        String[] strArr = {"rmnet", "wlan", "bt-pan", "p2p", "aware", "eth", "wpan"};
        int[] iArr = {2, 4, 1, 7, 8, 3, 9};
    }

    public static int ifnameToLinkLayer(String str) {
        for (int i = 0; i < 7; i++) {
            if (str.startsWith(IFNAME_PREFIXES[i])) {
                return IFNAME_LINKLAYERS[i];
            }
        }
        return 0;
    }
}
