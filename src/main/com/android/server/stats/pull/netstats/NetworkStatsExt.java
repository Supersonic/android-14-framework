package com.android.server.stats.pull.netstats;

import android.net.NetworkStats;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes2.dex */
public class NetworkStatsExt {
    public final int oemManaged;
    public final int ratType;
    public final boolean slicedByFgbg;
    public final boolean slicedByMetered;
    public final boolean slicedByTag;
    public final NetworkStats stats;
    public final SubInfo subInfo;
    public final int[] transports;

    public NetworkStatsExt(NetworkStats networkStats, int[] iArr, boolean z) {
        this(networkStats, iArr, z, false, false, 0, null, -1);
    }

    public NetworkStatsExt(NetworkStats networkStats, int[] iArr, boolean z, boolean z2, boolean z3, int i, SubInfo subInfo, int i2) {
        this.stats = networkStats;
        int[] copyOf = Arrays.copyOf(iArr, iArr.length);
        this.transports = copyOf;
        Arrays.sort(copyOf);
        this.slicedByFgbg = z;
        this.slicedByTag = z2;
        this.slicedByMetered = z3;
        this.ratType = i;
        this.subInfo = subInfo;
        this.oemManaged = i2;
    }

    public boolean hasSameSlicing(NetworkStatsExt networkStatsExt) {
        return Arrays.equals(this.transports, networkStatsExt.transports) && this.slicedByFgbg == networkStatsExt.slicedByFgbg && this.slicedByTag == networkStatsExt.slicedByTag && this.slicedByMetered == networkStatsExt.slicedByMetered && this.ratType == networkStatsExt.ratType && Objects.equals(this.subInfo, networkStatsExt.subInfo) && this.oemManaged == networkStatsExt.oemManaged;
    }
}
