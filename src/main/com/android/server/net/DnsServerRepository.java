package com.android.server.net;

import android.net.LinkProperties;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
/* compiled from: NetlinkTracker.java */
/* loaded from: classes5.dex */
class DnsServerRepository {
    public static final int NUM_CURRENT_SERVERS = 3;
    public static final int NUM_SERVERS = 12;
    public static final String TAG = "DnsServerRepository";
    private Set<InetAddress> mCurrentServers = new HashSet();
    private ArrayList<DnsServerEntry> mAllServers = new ArrayList<>(12);
    private HashMap<InetAddress, DnsServerEntry> mIndex = new HashMap<>(12);

    public synchronized void setDnsServersOn(LinkProperties lp) {
        lp.setDnsServers(this.mCurrentServers);
    }

    public synchronized boolean addServers(long lifetime, String[] addresses) {
        long now = System.currentTimeMillis();
        long expiry = (1000 * lifetime) + now;
        for (String addressString : addresses) {
            try {
                InetAddress address = InetAddress.parseNumericAddress(addressString);
                if (!updateExistingEntry(address, expiry) && expiry > now) {
                    DnsServerEntry entry = new DnsServerEntry(address, expiry);
                    this.mAllServers.add(entry);
                    this.mIndex.put(address, entry);
                }
            } catch (IllegalArgumentException e) {
            }
        }
        Collections.sort(this.mAllServers);
        return updateCurrentServers();
    }

    private synchronized boolean updateExistingEntry(InetAddress address, long expiry) {
        DnsServerEntry existing = this.mIndex.get(address);
        if (existing != null) {
            existing.expiry = expiry;
            return true;
        }
        return false;
    }

    private synchronized boolean updateCurrentServers() {
        boolean changed;
        long now = System.currentTimeMillis();
        changed = false;
        for (int i = this.mAllServers.size() - 1; i >= 0 && (i >= 12 || this.mAllServers.get(i).expiry < now); i--) {
            DnsServerEntry removed = this.mAllServers.remove(i);
            this.mIndex.remove(removed.address);
            changed |= this.mCurrentServers.remove(removed.address);
        }
        Iterator<DnsServerEntry> it = this.mAllServers.iterator();
        while (it.hasNext()) {
            DnsServerEntry entry = it.next();
            if (this.mCurrentServers.size() >= 3) {
                break;
            }
            changed |= this.mCurrentServers.add(entry.address);
        }
        return changed;
    }
}
