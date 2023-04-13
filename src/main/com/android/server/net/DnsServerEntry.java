package com.android.server.net;

import java.net.InetAddress;
/* compiled from: NetlinkTracker.java */
/* loaded from: classes5.dex */
class DnsServerEntry implements Comparable<DnsServerEntry> {
    public final InetAddress address;
    public long expiry;

    public DnsServerEntry(InetAddress address, long expiry) throws IllegalArgumentException {
        this.address = address;
        this.expiry = expiry;
    }

    @Override // java.lang.Comparable
    public int compareTo(DnsServerEntry other) {
        return Long.compare(other.expiry, this.expiry);
    }
}
