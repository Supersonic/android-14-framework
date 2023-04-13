package com.android.server.connectivity;

import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.metrics.DefaultNetworkEvent;
import android.os.SystemClock;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.BitUtils;
import com.android.internal.util.RingBuffer;
import com.android.server.connectivity.metrics.nano.IpConnectivityLogClass;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class DefaultNetworkMetrics {
    public final long creationTimeMs;
    @GuardedBy({"this"})
    public DefaultNetworkEvent mCurrentDefaultNetwork;
    @GuardedBy({"this"})
    public final List<DefaultNetworkEvent> mEvents;
    @GuardedBy({"this"})
    public final RingBuffer<DefaultNetworkEvent> mEventsLog;
    @GuardedBy({"this"})
    public boolean mIsCurrentlyValid;
    @GuardedBy({"this"})
    public int mLastTransports;
    @GuardedBy({"this"})
    public long mLastValidationTimeMs;

    public DefaultNetworkMetrics() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        this.creationTimeMs = elapsedRealtime;
        this.mEvents = new ArrayList();
        this.mEventsLog = new RingBuffer<>(DefaultNetworkEvent.class, 64);
        newDefaultNetwork(elapsedRealtime, null, 0, false, null, null);
    }

    public synchronized void listEvents(PrintWriter printWriter) {
        printWriter.println("default network events:");
        long currentTimeMillis = System.currentTimeMillis();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        for (DefaultNetworkEvent defaultNetworkEvent : (DefaultNetworkEvent[]) this.mEventsLog.toArray()) {
            printEvent(currentTimeMillis, printWriter, defaultNetworkEvent);
        }
        this.mCurrentDefaultNetwork.updateDuration(elapsedRealtime);
        if (this.mIsCurrentlyValid) {
            updateValidationTime(elapsedRealtime);
            this.mLastValidationTimeMs = elapsedRealtime;
        }
        printEvent(currentTimeMillis, printWriter, this.mCurrentDefaultNetwork);
    }

    public synchronized List<IpConnectivityLogClass.IpConnectivityEvent> listEventsAsProto() {
        ArrayList arrayList;
        arrayList = new ArrayList();
        for (DefaultNetworkEvent defaultNetworkEvent : (DefaultNetworkEvent[]) this.mEventsLog.toArray()) {
            arrayList.add(IpConnectivityEventBuilder.toProto(defaultNetworkEvent));
        }
        return arrayList;
    }

    public synchronized void flushEvents(List<IpConnectivityLogClass.IpConnectivityEvent> list) {
        for (DefaultNetworkEvent defaultNetworkEvent : this.mEvents) {
            list.add(IpConnectivityEventBuilder.toProto(defaultNetworkEvent));
        }
        this.mEvents.clear();
    }

    public synchronized void logDefaultNetworkValidity(long j, boolean z) {
        if (!z) {
            try {
                if (this.mIsCurrentlyValid) {
                    this.mIsCurrentlyValid = false;
                    updateValidationTime(j);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        if (z && !this.mIsCurrentlyValid) {
            this.mIsCurrentlyValid = true;
            this.mLastValidationTimeMs = j;
        }
    }

    public final void updateValidationTime(long j) {
        this.mCurrentDefaultNetwork.validatedMs += j - this.mLastValidationTimeMs;
    }

    public synchronized void logDefaultNetworkEvent(long j, Network network, int i, boolean z, LinkProperties linkProperties, NetworkCapabilities networkCapabilities, Network network2, int i2, LinkProperties linkProperties2, NetworkCapabilities networkCapabilities2) {
        logCurrentDefaultNetwork(j, network2, i2, linkProperties2, networkCapabilities2);
        newDefaultNetwork(j, network, i, z, linkProperties, networkCapabilities);
    }

    public final void logCurrentDefaultNetwork(long j, Network network, int i, LinkProperties linkProperties, NetworkCapabilities networkCapabilities) {
        if (this.mIsCurrentlyValid) {
            updateValidationTime(j);
        }
        DefaultNetworkEvent defaultNetworkEvent = this.mCurrentDefaultNetwork;
        defaultNetworkEvent.updateDuration(j);
        defaultNetworkEvent.previousTransports = this.mLastTransports;
        if (network != null) {
            fillLinkInfo(defaultNetworkEvent, network, linkProperties, networkCapabilities);
            defaultNetworkEvent.finalScore = i;
        }
        int i2 = defaultNetworkEvent.transports;
        if (i2 != 0) {
            this.mLastTransports = i2;
        }
        this.mEvents.add(defaultNetworkEvent);
        this.mEventsLog.append(defaultNetworkEvent);
    }

    public final void newDefaultNetwork(long j, Network network, int i, boolean z, LinkProperties linkProperties, NetworkCapabilities networkCapabilities) {
        DefaultNetworkEvent defaultNetworkEvent = new DefaultNetworkEvent(j);
        defaultNetworkEvent.durationMs = j;
        if (network != null) {
            fillLinkInfo(defaultNetworkEvent, network, linkProperties, networkCapabilities);
            defaultNetworkEvent.initialScore = i;
            if (z) {
                this.mIsCurrentlyValid = true;
                this.mLastValidationTimeMs = j;
            }
        } else {
            this.mIsCurrentlyValid = false;
        }
        this.mCurrentDefaultNetwork = defaultNetworkEvent;
    }

    public static void fillLinkInfo(DefaultNetworkEvent defaultNetworkEvent, Network network, LinkProperties linkProperties, NetworkCapabilities networkCapabilities) {
        defaultNetworkEvent.netId = network.getNetId();
        defaultNetworkEvent.transports = (int) (defaultNetworkEvent.transports | BitUtils.packBits(networkCapabilities.getTransportTypes()));
        boolean z = true;
        defaultNetworkEvent.ipv4 |= linkProperties.hasIpv4Address() && linkProperties.hasIpv4DefaultRoute();
        boolean z2 = defaultNetworkEvent.ipv6;
        if (!linkProperties.hasGlobalIpv6Address() || !linkProperties.hasIpv6DefaultRoute()) {
            z = false;
        }
        defaultNetworkEvent.ipv6 = z2 | z;
    }

    public static void printEvent(long j, PrintWriter printWriter, DefaultNetworkEvent defaultNetworkEvent) {
        long j2 = j - defaultNetworkEvent.durationMs;
        printWriter.println(String.format("%tT.%tL: %s", Long.valueOf(j2), Long.valueOf(j2), defaultNetworkEvent));
    }
}
