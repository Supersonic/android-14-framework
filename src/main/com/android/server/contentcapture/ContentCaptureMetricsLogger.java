package com.android.server.contentcapture;

import android.content.ComponentName;
import android.content.ContentCaptureOptions;
import android.service.contentcapture.FlushMetrics;
import com.android.internal.util.FrameworkStatsLog;
import java.util.List;
/* loaded from: classes.dex */
public final class ContentCaptureMetricsLogger {
    public static void writeServiceEvent(int i, String str) {
        FrameworkStatsLog.write(207, i, str, (String) null, 0, 0);
    }

    public static void writeServiceEvent(int i, ComponentName componentName) {
        writeServiceEvent(i, ComponentName.flattenToShortString(componentName));
    }

    public static void writeSetWhitelistEvent(ComponentName componentName, List<String> list, List<ComponentName> list2) {
        FrameworkStatsLog.write(207, 3, ComponentName.flattenToShortString(componentName), (String) null, list != null ? list.size() : 0, list2 != null ? list2.size() : 0);
    }

    public static void writeSessionEvent(int i, int i2, int i3, ComponentName componentName, boolean z) {
        FrameworkStatsLog.write(208, i, i2, i3, ComponentName.flattenToShortString(componentName), (String) null, z);
    }

    public static void writeSessionFlush(int i, ComponentName componentName, FlushMetrics flushMetrics, ContentCaptureOptions contentCaptureOptions, int i2) {
        FrameworkStatsLog.write(209, i, ComponentName.flattenToShortString(componentName), (String) null, flushMetrics.sessionStarted, flushMetrics.sessionFinished, flushMetrics.viewAppearedCount, flushMetrics.viewDisappearedCount, flushMetrics.viewTextChangedCount, contentCaptureOptions.maxBufferSize, contentCaptureOptions.idleFlushingFrequencyMs, contentCaptureOptions.textChangeFlushingFrequencyMs, i2);
    }
}
