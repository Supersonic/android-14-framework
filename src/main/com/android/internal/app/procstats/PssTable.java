package com.android.internal.app.procstats;

import android.util.proto.ProtoOutputStream;
import android.util.proto.ProtoUtils;
import com.android.internal.app.procstats.SparseMappingTable;
/* loaded from: classes4.dex */
public class PssTable extends SparseMappingTable.Table {
    public PssTable(SparseMappingTable tableData) {
        super(tableData);
    }

    public void mergeStats(PssTable that) {
        int N = that.getKeyCount();
        for (int i = 0; i < N; i++) {
            int thatKey = that.getKeyAt(i);
            int state = SparseMappingTable.getIdFromKey(thatKey);
            int key = getOrAddKey((byte) state, 10);
            long[] stats = getArrayForKey(key);
            int statsIndex = SparseMappingTable.getIndexFromKey(key);
            long[] thatStats = that.getArrayForKey(thatKey);
            int thatStatsIndex = SparseMappingTable.getIndexFromKey(thatKey);
            mergeStats(stats, statsIndex, thatStats, thatStatsIndex);
        }
    }

    public void mergeStats(int state, int inCount, long minPss, long avgPss, long maxPss, long minUss, long avgUss, long maxUss, long minRss, long avgRss, long maxRss) {
        int key = getOrAddKey((byte) state, 10);
        long[] stats = getArrayForKey(key);
        int statsIndex = SparseMappingTable.getIndexFromKey(key);
        mergeStats(stats, statsIndex, inCount, minPss, avgPss, maxPss, minUss, avgUss, maxUss, minRss, avgRss, maxRss);
    }

    public static void mergeStats(long[] stats, int statsIndex, long[] thatStats, int thatStatsIndex) {
        mergeStats(stats, statsIndex, (int) thatStats[thatStatsIndex + 0], thatStats[thatStatsIndex + 1], thatStats[thatStatsIndex + 2], thatStats[thatStatsIndex + 3], thatStats[thatStatsIndex + 4], thatStats[thatStatsIndex + 5], thatStats[thatStatsIndex + 6], thatStats[thatStatsIndex + 7], thatStats[thatStatsIndex + 8], thatStats[thatStatsIndex + 9]);
    }

    public static void mergeStats(long[] stats, int statsIndex, int inCount, long minPss, long avgPss, long maxPss, long minUss, long avgUss, long maxUss, long minRss, long avgRss, long maxRss) {
        long count = stats[statsIndex + 0];
        if (count == 0) {
            stats[statsIndex + 0] = inCount;
            stats[statsIndex + 1] = minPss;
            stats[statsIndex + 2] = avgPss;
            stats[statsIndex + 3] = maxPss;
            stats[statsIndex + 4] = minUss;
            stats[statsIndex + 5] = avgUss;
            stats[statsIndex + 6] = maxUss;
            stats[statsIndex + 7] = minRss;
            stats[statsIndex + 8] = avgRss;
            stats[statsIndex + 9] = maxRss;
            return;
        }
        stats[statsIndex + 0] = inCount + count;
        if (stats[statsIndex + 1] > minPss) {
            stats[statsIndex + 1] = minPss;
        }
        stats[statsIndex + 2] = (long) (((stats[statsIndex + 2] * count) + (avgPss * inCount)) / (inCount + count));
        if (stats[statsIndex + 3] < maxPss) {
            stats[statsIndex + 3] = maxPss;
        }
        if (stats[statsIndex + 4] > minUss) {
            stats[statsIndex + 4] = minUss;
        }
        stats[statsIndex + 5] = (long) (((stats[statsIndex + 5] * count) + (avgUss * inCount)) / (inCount + count));
        if (stats[statsIndex + 6] < maxUss) {
            stats[statsIndex + 6] = maxUss;
        }
        long j = stats[statsIndex + 7];
        stats[statsIndex + 8] = (long) (((stats[statsIndex + 8] * count) + (avgRss * inCount)) / (inCount + count));
        if (stats[statsIndex + 9] < maxRss) {
            stats[statsIndex + 9] = maxRss;
        }
    }

    public void writeStatsToProtoForKey(ProtoOutputStream proto, int key) {
        long[] stats = getArrayForKey(key);
        int statsIndex = SparseMappingTable.getIndexFromKey(key);
        writeStatsToProto(proto, stats, statsIndex);
    }

    public static void writeStatsToProto(ProtoOutputStream proto, long[] stats, int statsIndex) {
        proto.write(1120986464261L, stats[statsIndex + 0]);
        ProtoUtils.toAggStatsProto(proto, 1146756268038L, stats[statsIndex + 1], stats[statsIndex + 2], stats[statsIndex + 3]);
        ProtoUtils.toAggStatsProto(proto, 1146756268039L, stats[statsIndex + 4], stats[statsIndex + 5], stats[statsIndex + 6]);
        ProtoUtils.toAggStatsProto(proto, 1146756268040L, stats[statsIndex + 7], stats[statsIndex + 8], stats[statsIndex + 9]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long[] getRssMeanAndMax(int key) {
        long[] stats = getArrayForKey(key);
        int statsIndex = SparseMappingTable.getIndexFromKey(key);
        return new long[]{stats[statsIndex + 8], stats[statsIndex + 9]};
    }
}
