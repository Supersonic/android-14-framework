package com.android.server.net;

import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.RouteInfo;
import java.util.Arrays;
/* loaded from: classes5.dex */
public class NetlinkTracker extends BaseNetworkObserver {
    private static final boolean DBG = false;
    private final String TAG;
    private final Callback mCallback;
    private DnsServerRepository mDnsServerRepository;
    private final String mInterfaceName;
    private final LinkProperties mLinkProperties;

    /* loaded from: classes5.dex */
    public interface Callback {
        void update();
    }

    public NetlinkTracker(String iface, Callback callback) {
        this.TAG = "NetlinkTracker/" + iface;
        this.mInterfaceName = iface;
        this.mCallback = callback;
        LinkProperties linkProperties = new LinkProperties();
        this.mLinkProperties = linkProperties;
        linkProperties.setInterfaceName(iface);
        this.mDnsServerRepository = new DnsServerRepository();
    }

    private void maybeLog(String operation, String iface, LinkAddress address) {
    }

    private void maybeLog(String operation, Object o) {
    }

    @Override // com.android.server.net.BaseNetworkObserver, android.net.INetworkManagementEventObserver
    public void interfaceRemoved(String iface) {
        maybeLog("interfaceRemoved", iface);
        if (this.mInterfaceName.equals(iface)) {
            clearLinkProperties();
            this.mCallback.update();
        }
    }

    @Override // com.android.server.net.BaseNetworkObserver, android.net.INetworkManagementEventObserver
    public void addressUpdated(String iface, LinkAddress address) {
        boolean changed;
        if (this.mInterfaceName.equals(iface)) {
            maybeLog("addressUpdated", iface, address);
            synchronized (this) {
                changed = this.mLinkProperties.addLinkAddress(address);
            }
            if (changed) {
                this.mCallback.update();
            }
        }
    }

    @Override // com.android.server.net.BaseNetworkObserver, android.net.INetworkManagementEventObserver
    public void addressRemoved(String iface, LinkAddress address) {
        boolean changed;
        if (this.mInterfaceName.equals(iface)) {
            maybeLog("addressRemoved", iface, address);
            synchronized (this) {
                changed = this.mLinkProperties.removeLinkAddress(address);
            }
            if (changed) {
                this.mCallback.update();
            }
        }
    }

    @Override // com.android.server.net.BaseNetworkObserver, android.net.INetworkManagementEventObserver
    public void routeUpdated(RouteInfo route) {
        boolean changed;
        if (this.mInterfaceName.equals(route.getInterface())) {
            maybeLog("routeUpdated", route);
            synchronized (this) {
                changed = this.mLinkProperties.addRoute(route);
            }
            if (changed) {
                this.mCallback.update();
            }
        }
    }

    @Override // com.android.server.net.BaseNetworkObserver, android.net.INetworkManagementEventObserver
    public void routeRemoved(RouteInfo route) {
        boolean changed;
        if (this.mInterfaceName.equals(route.getInterface())) {
            maybeLog("routeRemoved", route);
            synchronized (this) {
                changed = this.mLinkProperties.removeRoute(route);
            }
            if (changed) {
                this.mCallback.update();
            }
        }
    }

    @Override // com.android.server.net.BaseNetworkObserver, android.net.INetworkManagementEventObserver
    public void interfaceDnsServerInfo(String iface, long lifetime, String[] addresses) {
        if (this.mInterfaceName.equals(iface)) {
            maybeLog("interfaceDnsServerInfo", Arrays.toString(addresses));
            boolean changed = this.mDnsServerRepository.addServers(lifetime, addresses);
            if (changed) {
                synchronized (this) {
                    this.mDnsServerRepository.setDnsServersOn(this.mLinkProperties);
                }
                this.mCallback.update();
            }
        }
    }

    public synchronized LinkProperties getLinkProperties() {
        return new LinkProperties(this.mLinkProperties);
    }

    public synchronized void clearLinkProperties() {
        this.mDnsServerRepository = new DnsServerRepository();
        this.mLinkProperties.clear();
        this.mLinkProperties.setInterfaceName(this.mInterfaceName);
    }
}
