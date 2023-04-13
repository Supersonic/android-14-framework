package android.net.metrics;

import android.app.blob.XmlTags;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.SystemClock;
import android.util.SparseIntArray;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public class WakeupStats {
    private static final int NO_UID = -1;
    public final String iface;
    public final long creationTimeMs = SystemClock.elapsedRealtime();
    public long totalWakeups = 0;
    public long rootWakeups = 0;
    public long systemWakeups = 0;
    public long nonApplicationWakeups = 0;
    public long applicationWakeups = 0;
    public long noUidWakeups = 0;
    public long durationSec = 0;
    public long l2UnicastCount = 0;
    public long l2MulticastCount = 0;
    public long l2BroadcastCount = 0;
    public final SparseIntArray ethertypes = new SparseIntArray();
    public final SparseIntArray ipNextHeaders = new SparseIntArray();

    public WakeupStats(String iface) {
        this.iface = iface;
    }

    public void updateDuration() {
        this.durationSec = (SystemClock.elapsedRealtime() - this.creationTimeMs) / 1000;
    }

    public void countEvent(WakeupEvent ev) {
        this.totalWakeups++;
        switch (ev.uid) {
            case -1:
                this.noUidWakeups++;
                break;
            case 0:
                this.rootWakeups++;
                break;
            case 1000:
                this.systemWakeups++;
                break;
            default:
                if (ev.uid >= 10000) {
                    this.applicationWakeups++;
                    break;
                } else {
                    this.nonApplicationWakeups++;
                    break;
                }
        }
        switch (ev.dstHwAddr.getAddressType()) {
            case 1:
                this.l2UnicastCount++;
                break;
            case 2:
                this.l2MulticastCount++;
                break;
            case 3:
                this.l2BroadcastCount++;
                break;
        }
        increment(this.ethertypes, ev.ethertype);
        if (ev.ipNextHeader >= 0) {
            increment(this.ipNextHeaders, ev.ipNextHeader);
        }
    }

    public String toString() {
        updateDuration();
        StringJoiner j = new StringJoiner(", ", "WakeupStats(", NavigationBarInflaterView.KEY_CODE_END);
        j.add(this.iface);
        j.add("" + this.durationSec + XmlTags.TAG_SESSION);
        j.add("total: " + this.totalWakeups);
        j.add("root: " + this.rootWakeups);
        j.add("system: " + this.systemWakeups);
        j.add("apps: " + this.applicationWakeups);
        j.add("non-apps: " + this.nonApplicationWakeups);
        j.add("no uid: " + this.noUidWakeups);
        j.add(String.format("l2 unicast/multicast/broadcast: %d/%d/%d", Long.valueOf(this.l2UnicastCount), Long.valueOf(this.l2MulticastCount), Long.valueOf(this.l2BroadcastCount)));
        for (int i = 0; i < this.ethertypes.size(); i++) {
            int eth = this.ethertypes.keyAt(i);
            int count = this.ethertypes.valueAt(i);
            j.add(String.format("ethertype 0x%x: %d", Integer.valueOf(eth), Integer.valueOf(count)));
        }
        for (int i2 = 0; i2 < this.ipNextHeaders.size(); i2++) {
            int proto = this.ipNextHeaders.keyAt(i2);
            int count2 = this.ipNextHeaders.valueAt(i2);
            j.add(String.format("ipNxtHdr %d: %d", Integer.valueOf(proto), Integer.valueOf(count2)));
        }
        return j.toString();
    }

    private static void increment(SparseIntArray counters, int key) {
        int newcount = counters.get(key, 0) + 1;
        counters.put(key, newcount);
    }
}
