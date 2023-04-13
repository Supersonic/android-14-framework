package android.telephony;

import android.Manifest;
import android.app.PropertyInvalidatedCache;
import android.content.Context;
import android.content.Intent;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.p008os.BatteryStats;
import android.p008os.ParcelUuid;
import android.provider.DeviceConfig;
import com.android.internal.telephony.TelephonyStatsLog;
import com.android.internal.util.IndentingPrintWriter;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
/* loaded from: classes3.dex */
public final class AnomalyReporter {
    private static final String KEY_IS_TELEPHONY_ANOMALY_REPORT_ENABLED = "is_telephony_anomaly_report_enabled";
    private static final String TAG = "AnomalyReporter";
    private static Context sContext = null;
    private static Map<UUID, Integer> sEvents = new ConcurrentHashMap();
    private static String sDebugPackageName = null;

    private AnomalyReporter() {
    }

    public static void reportAnomaly(UUID eventId, String description) {
        reportAnomaly(eventId, description, -1);
    }

    public static void reportAnomaly(UUID eventId, String description, int carrierId) {
        if (sContext == null) {
            com.android.telephony.Rlog.m2w(TAG, "AnomalyReporter not yet initialized, dropping event=" + eventId);
            return;
        }
        TelephonyStatsLog.write(461, carrierId, eventId.getLeastSignificantBits(), eventId.getMostSignificantBits());
        try {
            boolean isAnomalyReportEnabledFromServer = DeviceConfig.getBoolean(PropertyInvalidatedCache.MODULE_TELEPHONY, KEY_IS_TELEPHONY_ANOMALY_REPORT_ENABLED, false);
            if (isAnomalyReportEnabledFromServer) {
                Integer count = Integer.valueOf(sEvents.containsKey(eventId) ? sEvents.get(eventId).intValue() + 1 : 1);
                sEvents.put(eventId, count);
                if (count.intValue() <= 1 && sDebugPackageName != null) {
                    Intent dbgIntent = new Intent(TelephonyManager.ACTION_ANOMALY_REPORTED);
                    dbgIntent.putExtra(TelephonyManager.EXTRA_ANOMALY_ID, new ParcelUuid(eventId));
                    if (description != null) {
                        dbgIntent.putExtra(TelephonyManager.EXTRA_ANOMALY_DESCRIPTION, description);
                    }
                    dbgIntent.setPackage(sDebugPackageName);
                    sContext.sendBroadcast(dbgIntent, Manifest.C0000permission.READ_PRIVILEGED_PHONE_STATE);
                }
            }
        } catch (Exception e) {
            com.android.telephony.Rlog.m2w(TAG, "Unable to read device config, dropping event=" + eventId);
        }
    }

    public static void initialize(Context context) {
        List<ResolveInfo> packages;
        if (context == null) {
            throw new IllegalArgumentException("AnomalyReporter needs a non-null context.");
        }
        context.enforceCallingOrSelfPermission(Manifest.C0000permission.MODIFY_PHONE_STATE, "This app does not have privileges to send debug events");
        sContext = context;
        PackageManager pm = context.getPackageManager();
        if (pm == null || (packages = pm.queryBroadcastReceivers(new Intent(TelephonyManager.ACTION_ANOMALY_REPORTED), BatteryStats.HistoryItem.MOST_INTERESTING_STATES)) == null || packages.isEmpty()) {
            return;
        }
        if (packages.size() > 1) {
            com.android.telephony.Rlog.m8e(TAG, "Multiple Anomaly Receivers installed.");
        }
        for (ResolveInfo r : packages) {
            if (r.activityInfo == null || pm.checkPermission(Manifest.C0000permission.READ_PRIVILEGED_PHONE_STATE, r.activityInfo.packageName) != 0) {
                com.android.telephony.Rlog.m2w(TAG, "Found package without proper permissions or no activity" + r.activityInfo.packageName);
            } else {
                com.android.telephony.Rlog.m10d(TAG, "Found a valid package " + r.activityInfo.packageName);
                sDebugPackageName = r.activityInfo.packageName;
                return;
            }
        }
    }

    public static void dump(FileDescriptor fd, PrintWriter printWriter, String[] args) {
        if (sContext == null) {
            return;
        }
        IndentingPrintWriter pw = new IndentingPrintWriter(printWriter, "  ");
        sContext.enforceCallingOrSelfPermission(Manifest.C0000permission.DUMP, "Requires DUMP");
        pw.println("Initialized=" + (sContext != null ? "Yes" : "No"));
        pw.println("Debug Package=" + sDebugPackageName);
        pw.println("Anomaly Counts:");
        pw.increaseIndent();
        for (UUID event : sEvents.keySet()) {
            pw.println(event + ": " + sEvents.get(event));
        }
        pw.decreaseIndent();
        pw.flush();
    }
}
