package com.android.server.location.injector;

import android.location.Geofence;
import android.location.LocationRequest;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.clipboard.ClipboardService;
import java.time.Instant;
/* loaded from: classes.dex */
public class LocationUsageLogger {
    @GuardedBy({"this"})
    public long mLastApiUsageLogHour = 0;
    @GuardedBy({"this"})
    public int mApiUsageLogHourlyCount = 0;

    public static int bucketizeDistance(float f) {
        if (f <= 0.0f) {
            return 1;
        }
        return (f <= 0.0f || f > 100.0f) ? 3 : 2;
    }

    public static int bucketizeExpireIn(long j) {
        if (j == Long.MAX_VALUE) {
            return 6;
        }
        if (j < 20000) {
            return 1;
        }
        if (j < 60000) {
            return 2;
        }
        if (j < 600000) {
            return 3;
        }
        return j < ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS ? 4 : 5;
    }

    public static int bucketizeInterval(long j) {
        if (j < 1000) {
            return 1;
        }
        if (j < 5000) {
            return 2;
        }
        if (j < 60000) {
            return 3;
        }
        if (j < 600000) {
            return 4;
        }
        return j < ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS ? 5 : 6;
    }

    public static int bucketizeRadius(float f) {
        if (f < 0.0f) {
            return 7;
        }
        if (f < 100.0f) {
            return 1;
        }
        if (f < 200.0f) {
            return 2;
        }
        if (f < 300.0f) {
            return 3;
        }
        if (f < 1000.0f) {
            return 4;
        }
        return f < 10000.0f ? 5 : 6;
    }

    public static int categorizeActivityImportance(boolean z) {
        return z ? 1 : 3;
    }

    public static int getCallbackType(int i, boolean z, boolean z2) {
        if (i == 5) {
            return 1;
        }
        if (z2) {
            return 3;
        }
        return z ? 2 : 0;
    }

    /* JADX WARN: Removed duplicated region for block: B:39:0x0076 A[Catch: Exception -> 0x008f, TryCatch #0 {Exception -> 0x008f, blocks: (B:2:0x0000, B:36:0x006d, B:40:0x007f, B:39:0x0076, B:33:0x0054, B:28:0x0047, B:24:0x0038, B:21:0x002b, B:18:0x0022, B:15:0x0019), top: B:45:0x0000 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void logLocationApiUsage(int i, int i2, String str, String str2, String str3, LocationRequest locationRequest, boolean z, boolean z2, Geofence geofence, boolean z3) {
        int i3;
        boolean z4;
        boolean z5;
        int i4;
        try {
            if (hitApiUsageLogCap()) {
                return;
            }
            boolean z6 = locationRequest == null;
            boolean z7 = geofence == null;
            int bucketizeProvider = z6 ? 0 : bucketizeProvider(str3);
            int quality = z6 ? 0 : locationRequest.getQuality();
            int bucketizeInterval = z6 ? 0 : bucketizeInterval(locationRequest.getIntervalMillis());
            int bucketizeDistance = z6 ? 0 : bucketizeDistance(locationRequest.getMinUpdateDistanceMeters());
            long maxUpdates = z6 ? 0L : locationRequest.getMaxUpdates();
            if (!z6 && i != 1) {
                z5 = z;
                z4 = z2;
                i3 = bucketizeExpireIn(locationRequest.getDurationMillis());
                i4 = i2;
                FrameworkStatsLog.write(210, i, i2, str, bucketizeProvider, quality, bucketizeInterval, bucketizeDistance, maxUpdates, i3, getCallbackType(i4, z5, z4), z7 ? 0 : bucketizeRadius(geofence.getRadius()), categorizeActivityImportance(z3), str2);
            }
            i4 = i2;
            z5 = z;
            z4 = z2;
            i3 = 0;
            FrameworkStatsLog.write(210, i, i2, str, bucketizeProvider, quality, bucketizeInterval, bucketizeDistance, maxUpdates, i3, getCallbackType(i4, z5, z4), z7 ? 0 : bucketizeRadius(geofence.getRadius()), categorizeActivityImportance(z3), str2);
        } catch (Exception e) {
            Log.w("LocationManagerService", "Failed to log API usage to statsd.", e);
        }
    }

    public void logLocationApiUsage(int i, int i2, String str) {
        try {
            if (hitApiUsageLogCap()) {
                return;
            }
            FrameworkStatsLog.write(210, i, i2, (String) null, bucketizeProvider(str), 0, 0, 0, 0L, 0, getCallbackType(i2, true, true), 0, 0, (String) null);
        } catch (Exception e) {
            Log.w("LocationManagerService", "Failed to log API usage to statsd.", e);
        }
    }

    public synchronized void logLocationEnabledStateChanged(boolean z) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.LOCATION_ENABLED_STATE_CHANGED, z);
    }

    public static int bucketizeProvider(String str) {
        if ("network".equals(str)) {
            return 1;
        }
        if ("gps".equals(str)) {
            return 2;
        }
        if ("passive".equals(str)) {
            return 3;
        }
        return "fused".equals(str) ? 4 : 0;
    }

    public final synchronized boolean hitApiUsageLogCap() {
        long epochMilli = Instant.now().toEpochMilli() / ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS;
        if (epochMilli > this.mLastApiUsageLogHour) {
            this.mLastApiUsageLogHour = epochMilli;
            this.mApiUsageLogHourlyCount = 0;
            return false;
        }
        int min = Math.min(this.mApiUsageLogHourlyCount + 1, 60);
        this.mApiUsageLogHourlyCount = min;
        return min >= 60;
    }
}
