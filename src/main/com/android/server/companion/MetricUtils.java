package com.android.server.companion;

import android.util.ArrayMap;
import com.android.internal.util.FrameworkStatsLog;
import java.util.Collections;
import java.util.Map;
/* loaded from: classes.dex */
public final class MetricUtils {
    public static final Map<String, Integer> METRIC_DEVICE_PROFILE;

    static {
        ArrayMap arrayMap = new ArrayMap();
        arrayMap.put(null, 0);
        arrayMap.put("android.app.role.COMPANION_DEVICE_WATCH", 1);
        arrayMap.put("android.app.role.COMPANION_DEVICE_APP_STREAMING", 2);
        arrayMap.put("android.app.role.SYSTEM_AUTOMOTIVE_PROJECTION", 3);
        arrayMap.put("android.app.role.COMPANION_DEVICE_COMPUTER", 4);
        arrayMap.put("android.app.role.COMPANION_DEVICE_GLASSES", 5);
        arrayMap.put("android.app.role.COMPANION_DEVICE_NEARBY_DEVICE_STREAMING", 6);
        METRIC_DEVICE_PROFILE = Collections.unmodifiableMap(arrayMap);
    }

    public static void logCreateAssociation(String str) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.CDM_ASSOCIATION_ACTION, 1, METRIC_DEVICE_PROFILE.get(str).intValue());
    }

    public static void logRemoveAssociation(String str) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.CDM_ASSOCIATION_ACTION, 2, METRIC_DEVICE_PROFILE.get(str).intValue());
    }
}
