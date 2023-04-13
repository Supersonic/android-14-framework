package com.android.internal.expresslog;

import com.android.internal.util.FrameworkStatsLog;
/* loaded from: classes4.dex */
public final class Counter {
    private Counter() {
    }

    public static void logIncrement(String metricId) {
        logIncrement(metricId, 1L);
    }

    public static void logIncrement(String metricId, long amount) {
        long metricIdHash = Utils.hashString(metricId);
        FrameworkStatsLog.write(528, metricIdHash, amount);
    }
}
