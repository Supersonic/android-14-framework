package com.android.server;

import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.LocalLog;
import android.util.Slog;
import com.android.i18n.timezone.ZoneInfoDb;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public final class SystemTimeZone {
    public static final LocalLog sTimeZoneDebugLog = new LocalLog(30, false);

    public static boolean isValidTimeZoneConfidence(int i) {
        return i >= 0 && i <= 100;
    }

    public static void initializeTimeZoneSettingsIfRequired() {
        String str = SystemProperties.get("persist.sys.timezone");
        if (isValidTimeZoneId(str)) {
            return;
        }
        String str2 = "initializeTimeZoneSettingsIfRequired():persist.sys.timezone is not valid (" + str + "); setting to GMT";
        Slog.w("SystemTimeZone", str2);
        setTimeZoneId("GMT", 0, str2);
    }

    public static void addDebugLogEntry(String str) {
        sTimeZoneDebugLog.log(str);
    }

    public static boolean setTimeZoneId(String str, int i, String str2) {
        boolean z = false;
        if (TextUtils.isEmpty(str) || !isValidTimeZoneId(str)) {
            addDebugLogEntry("setTimeZoneId: Invalid time zone ID. timeZoneId=" + str + ", confidence=" + i + ", logInfo=" + str2);
            return false;
        }
        synchronized (SystemTimeZone.class) {
            String timeZoneId = getTimeZoneId();
            if (timeZoneId == null || !timeZoneId.equals(str)) {
                SystemProperties.set("persist.sys.timezone", str);
                z = true;
            }
            boolean timeZoneConfidence = setTimeZoneConfidence(i);
            if (z || timeZoneConfidence) {
                addDebugLogEntry("Time zone or confidence set:  (new) timeZoneId=" + str + ", (new) confidence=" + i + ", logInfo=" + str2);
            }
        }
        return z;
    }

    public static boolean setTimeZoneConfidence(int i) {
        if (getTimeZoneConfidence() != i) {
            SystemProperties.set("persist.sys.timezone_confidence", Integer.toString(i));
            return true;
        }
        return false;
    }

    public static int getTimeZoneConfidence() {
        int i = SystemProperties.getInt("persist.sys.timezone_confidence", 0);
        if (isValidTimeZoneConfidence(i)) {
            return i;
        }
        return 0;
    }

    public static String getTimeZoneId() {
        return SystemProperties.get("persist.sys.timezone");
    }

    public static void dump(PrintWriter printWriter) {
        sTimeZoneDebugLog.dump(printWriter);
    }

    public static boolean isValidTimeZoneId(String str) {
        return (str == null || str.isEmpty() || !ZoneInfoDb.getInstance().hasTimeZone(str)) ? false : true;
    }
}
