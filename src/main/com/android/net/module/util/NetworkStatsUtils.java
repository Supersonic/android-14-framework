package com.android.net.module.util;

import android.app.usage.NetworkStats;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.net.NetworkStats;
/* loaded from: classes5.dex */
public class NetworkStatsUtils {
    private static final int DEFAULT_NETWORK_ALL = -1;
    public static final String LIMIT_GLOBAL_ALERT = "globalAlert";
    private static final int METERED_ALL = -1;
    private static final int ROAMING_ALL = -1;
    private static final int SET_ALL = -1;
    public static final int SUBSCRIBER_ID_MATCH_RULE_ALL = 1;
    public static final int SUBSCRIBER_ID_MATCH_RULE_EXACT = 0;

    public static long multiplySafeByRational(long value, long num, long den) {
        if (den != 0) {
            long r = value * num;
            long ax = Math.abs(value);
            long ay = Math.abs(num);
            if (((ax | ay) >>> 31) != 0) {
                if ((num != 0 && r / num != value) || (value == Long.MIN_VALUE && num == -1)) {
                    return (long) ((num / den) * value);
                }
            }
            long x = r / den;
            return x;
        }
        throw new ArithmeticException("Invalid Denominator");
    }

    public static int constrain(int amount, int low, int high) {
        if (low <= high) {
            return amount < low ? low : amount > high ? high : amount;
        }
        throw new IllegalArgumentException("low(" + low + ") > high(" + high + NavigationBarInflaterView.KEY_CODE_END);
    }

    public static long constrain(long amount, long low, long high) {
        if (low <= high) {
            return amount < low ? low : amount > high ? high : amount;
        }
        throw new IllegalArgumentException("low(" + low + ") > high(" + high + NavigationBarInflaterView.KEY_CODE_END);
    }

    public static NetworkStats fromPublicNetworkStats(android.app.usage.NetworkStats publiceNetworkStats) {
        NetworkStats stats = new NetworkStats(0L, 0);
        while (publiceNetworkStats.hasNextBucket()) {
            NetworkStats.Bucket bucket = new NetworkStats.Bucket();
            publiceNetworkStats.getNextBucket(bucket);
            NetworkStats.Entry entry = fromBucket(bucket);
            stats = stats.addEntry(entry);
        }
        return stats;
    }

    public static NetworkStats.Entry fromBucket(NetworkStats.Bucket bucket) {
        return new NetworkStats.Entry((String) null, bucket.getUid(), convertBucketState(bucket.getState()), convertBucketTag(bucket.getTag()), convertBucketMetered(bucket.getMetered()), convertBucketRoaming(bucket.getRoaming()), convertBucketDefaultNetworkStatus(bucket.getDefaultNetworkStatus()), bucket.getRxBytes(), bucket.getRxPackets(), bucket.getTxBytes(), bucket.getTxPackets(), 0L);
    }

    private static int convertBucketState(int networkStatsSet) {
        switch (networkStatsSet) {
            case -1:
                return -1;
            case 0:
            default:
                return 0;
            case 1:
                return 0;
            case 2:
                return 1;
        }
    }

    private static int convertBucketTag(int tag) {
        switch (tag) {
            case 0:
                return 0;
            default:
                return tag;
        }
    }

    private static int convertBucketMetered(int metered) {
        switch (metered) {
            case -1:
                return -1;
            case 0:
            default:
                return 0;
            case 1:
                return 0;
            case 2:
                return 1;
        }
    }

    private static int convertBucketRoaming(int roaming) {
        switch (roaming) {
            case -1:
                return -1;
            case 0:
            default:
                return 0;
            case 1:
                return 0;
            case 2:
                return 1;
        }
    }

    private static int convertBucketDefaultNetworkStatus(int defaultNetworkStatus) {
        switch (defaultNetworkStatus) {
            case -1:
                return -1;
            case 0:
            default:
                return 0;
            case 1:
                return 0;
            case 2:
                return 1;
        }
    }
}
