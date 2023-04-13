package com.android.server.rollback;

import android.content.Context;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.VersionedPackage;
import android.content.rollback.PackageRollbackInfo;
import android.content.rollback.RollbackInfo;
import android.os.Bundle;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
/* loaded from: classes2.dex */
public final class WatchdogRollbackLogger {
    @VisibleForTesting
    public static int mapFailureReasonToMetric(int i) {
        int i2 = 1;
        if (i != 1) {
            i2 = 2;
            if (i != 2) {
                i2 = 3;
                if (i != 3) {
                    i2 = 4;
                    if (i != 4) {
                        return 0;
                    }
                }
            }
        }
        return i2;
    }

    public static String rollbackReasonToString(int i) {
        return i != 1 ? i != 2 ? i != 3 ? i != 4 ? i != 5 ? "UNKNOWN" : "REASON_NATIVE_CRASH_DURING_BOOT" : "REASON_APP_NOT_RESPONDING" : "REASON_APP_CRASH" : "REASON_EXPLICIT_HEALTH_CHECK" : "REASON_NATIVE_CRASH";
    }

    public static String rollbackTypeToString(int i) {
        return i != 1 ? i != 2 ? i != 3 ? i != 4 ? "UNKNOWN" : "ROLLBACK_BOOT_TRIGGERED" : "ROLLBACK_FAILURE" : "ROLLBACK_SUCCESS" : "ROLLBACK_INITIATE";
    }

    public static String getLoggingParentName(Context context, String str) {
        try {
            Bundle bundle = context.getPackageManager().getPackageInfo(str, 1073741952).applicationInfo.metaData;
            if (bundle == null) {
                return null;
            }
            return bundle.getString("android.content.pm.LOGGING_PARENT");
        } catch (Exception e) {
            Slog.w("WatchdogRollbackLogger", "Unable to discover logging parent package: " + str, e);
            return null;
        }
    }

    @VisibleForTesting
    public static VersionedPackage getLogPackage(Context context, VersionedPackage versionedPackage) {
        String loggingParentName = getLoggingParentName(context, versionedPackage.getPackageName());
        if (loggingParentName == null) {
            return null;
        }
        try {
            return new VersionedPackage(loggingParentName, context.getPackageManager().getPackageInfo(loggingParentName, 0).getLongVersionCode());
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    public static Set<VersionedPackage> getLogPackages(Context context, List<String> list) {
        ArraySet arraySet = new ArraySet();
        for (String str : list) {
            arraySet.add(getLogPackage(context, new VersionedPackage(str, 0)));
        }
        return arraySet;
    }

    public static void logRollbackStatusOnBoot(Context context, int i, String str, List<RollbackInfo> list) {
        VersionedPackage versionedPackage;
        RollbackInfo rollbackInfo;
        PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
        Iterator<RollbackInfo> it = list.iterator();
        while (true) {
            versionedPackage = null;
            if (!it.hasNext()) {
                rollbackInfo = null;
                break;
            }
            rollbackInfo = it.next();
            if (i == rollbackInfo.getRollbackId()) {
                break;
            }
        }
        if (rollbackInfo == null) {
            Slog.e("WatchdogRollbackLogger", "rollback info not found for last staged rollback: " + i);
            return;
        }
        if (!TextUtils.isEmpty(str)) {
            Iterator it2 = rollbackInfo.getPackages().iterator();
            while (true) {
                if (!it2.hasNext()) {
                    break;
                }
                PackageRollbackInfo packageRollbackInfo = (PackageRollbackInfo) it2.next();
                if (str.equals(packageRollbackInfo.getPackageName())) {
                    versionedPackage = packageRollbackInfo.getVersionRolledBackFrom();
                    break;
                }
            }
        }
        int committedSessionId = rollbackInfo.getCommittedSessionId();
        PackageInstaller.SessionInfo sessionInfo = packageInstaller.getSessionInfo(committedSessionId);
        if (sessionInfo == null) {
            Slog.e("WatchdogRollbackLogger", "On boot completed, could not load session id " + committedSessionId);
        } else if (sessionInfo.isStagedSessionApplied()) {
            logEvent(versionedPackage, 2, 0, "");
        } else if (sessionInfo.isStagedSessionFailed()) {
            logEvent(versionedPackage, 3, 0, "");
        }
    }

    public static void logApexdRevert(Context context, List<String> list, String str) {
        for (VersionedPackage versionedPackage : getLogPackages(context, list)) {
            logEvent(versionedPackage, 2, 5, str);
        }
    }

    public static void logEvent(VersionedPackage versionedPackage, int i, int i2, String str) {
        Slog.i("WatchdogRollbackLogger", "Watchdog event occurred with type: " + rollbackTypeToString(i) + " logPackage: " + versionedPackage + " rollbackReason: " + rollbackReasonToString(i2) + " failedPackageName: " + str);
        if (versionedPackage != null) {
            FrameworkStatsLog.write(147, i, versionedPackage.getPackageName(), versionedPackage.getVersionCode(), i2, str, new byte[0]);
        } else {
            FrameworkStatsLog.write(147, i, "", 0, i2, str, new byte[0]);
        }
        logTestProperties(versionedPackage, i, i2, str);
    }

    public static void logTestProperties(VersionedPackage versionedPackage, int i, int i2, String str) {
        if (SystemProperties.getBoolean("persist.sys.rollbacktest.enabled", false)) {
            String str2 = "persist.sys.rollbacktest." + rollbackTypeToString(i);
            SystemProperties.set(str2, String.valueOf(true));
            SystemProperties.set(str2 + ".logPackage", versionedPackage != null ? versionedPackage.toString() : "");
            SystemProperties.set(str2 + ".rollbackReason", rollbackReasonToString(i2));
            SystemProperties.set(str2 + ".failedPackageName", str);
        }
    }
}
