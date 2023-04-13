package com.android.internal.telephony.util;

import android.app.usage.NetworkStats;
import android.net.NetworkStats;
import com.android.net.module.annotation.VisibleForTesting;
/* loaded from: classes.dex */
public class NetworkStatsUtils {
    public static final String LIMIT_GLOBAL_ALERT = "globalAlert";
    public static final int SUBSCRIBER_ID_MATCH_RULE_ALL = 1;
    public static final int SUBSCRIBER_ID_MATCH_RULE_EXACT = 0;

    private static int convertBucketDefaultNetworkStatus(int i) {
        if (i != -1) {
            return i != 2 ? 0 : 1;
        }
        return -1;
    }

    private static int convertBucketMetered(int i) {
        if (i != -1) {
            return i != 2 ? 0 : 1;
        }
        return -1;
    }

    private static int convertBucketRoaming(int i) {
        if (i != -1) {
            return i != 2 ? 0 : 1;
        }
        return -1;
    }

    private static int convertBucketState(int i) {
        if (i != -1) {
            return i != 2 ? 0 : 1;
        }
        return -1;
    }

    private static int convertBucketTag(int i) {
        if (i != 0) {
            return i;
        }
        return 0;
    }

    public static long multiplySafeByRational(long j, long j2, long j3) {
        if (j3 != 0) {
            long j4 = j * j2;
            return (((Math.abs(j) | Math.abs(j2)) >>> 31) == 0 || ((j2 == 0 || j4 / j2 == j) && !(j == Long.MIN_VALUE && j2 == -1))) ? j4 / j3 : (long) ((j2 / j3) * j);
        }
        throw new ArithmeticException("Invalid Denominator");
    }

    public static int constrain(int i, int i2, int i3) {
        if (i2 <= i3) {
            return i < i2 ? i2 : i > i3 ? i3 : i;
        }
        throw new IllegalArgumentException("low(" + i2 + ") > high(" + i3 + ")");
    }

    public static long constrain(long j, long j2, long j3) {
        if (j2 <= j3) {
            return j < j2 ? j2 : j > j3 ? j3 : j;
        }
        throw new IllegalArgumentException("low(" + j2 + ") > high(" + j3 + ")");
    }

    public static NetworkStats fromPublicNetworkStats(android.app.usage.NetworkStats networkStats) {
        NetworkStats networkStats2 = new NetworkStats(0L, 0);
        while (networkStats.hasNextBucket()) {
            NetworkStats.Bucket bucket = new NetworkStats.Bucket();
            networkStats.getNextBucket(bucket);
            networkStats2 = networkStats2.addEntry(fromBucket(bucket));
        }
        return networkStats2;
    }

    @VisibleForTesting
    public static NetworkStats.Entry fromBucket(NetworkStats.Bucket bucket) {
        return new NetworkStats.Entry((String) null, bucket.getUid(), convertBucketState(bucket.getState()), convertBucketTag(bucket.getTag()), convertBucketMetered(bucket.getMetered()), convertBucketRoaming(bucket.getRoaming()), convertBucketDefaultNetworkStatus(bucket.getDefaultNetworkStatus()), bucket.getRxBytes(), bucket.getRxPackets(), bucket.getTxBytes(), bucket.getTxPackets(), 0L);
    }
}
