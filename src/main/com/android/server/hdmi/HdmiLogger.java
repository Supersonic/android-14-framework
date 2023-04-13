package com.android.server.hdmi;

import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import java.util.HashMap;
/* loaded from: classes.dex */
public final class HdmiLogger {
    public static final boolean DEBUG = Log.isLoggable("HDMI", 3);
    public static final ThreadLocal<HdmiLogger> sLogger = new ThreadLocal<>();
    public final HashMap<String, Pair<Long, Integer>> mWarningTimingCache = new HashMap<>();
    public final HashMap<String, Pair<Long, Integer>> mErrorTimingCache = new HashMap<>();

    public static final void warning(String str, Object... objArr) {
        getLogger().warningInternal(toLogString(str, objArr));
    }

    public final void warningInternal(String str) {
        String updateLog = updateLog(this.mWarningTimingCache, str);
        if (updateLog.isEmpty()) {
            return;
        }
        Slog.w("HDMI", updateLog);
    }

    public static final void error(String str, Object... objArr) {
        getLogger().errorInternal(toLogString(str, objArr));
    }

    public static final void error(String str, Exception exc, Object... objArr) {
        HdmiLogger logger = getLogger();
        logger.errorInternal(toLogString(str + exc, objArr));
    }

    public final void errorInternal(String str) {
        String updateLog = updateLog(this.mErrorTimingCache, str);
        if (updateLog.isEmpty()) {
            return;
        }
        Slog.e("HDMI", updateLog);
    }

    public static final void debug(String str, Object... objArr) {
        getLogger().debugInternal(toLogString(str, objArr));
    }

    public final void debugInternal(String str) {
        if (DEBUG) {
            Slog.d("HDMI", str);
        }
    }

    public static final String toLogString(String str, Object[] objArr) {
        return objArr.length > 0 ? String.format(str, objArr) : str;
    }

    public static HdmiLogger getLogger() {
        ThreadLocal<HdmiLogger> threadLocal = sLogger;
        HdmiLogger hdmiLogger = threadLocal.get();
        if (hdmiLogger == null) {
            HdmiLogger hdmiLogger2 = new HdmiLogger();
            threadLocal.set(hdmiLogger2);
            return hdmiLogger2;
        }
        return hdmiLogger;
    }

    public static String updateLog(HashMap<String, Pair<Long, Integer>> hashMap, String str) {
        long uptimeMillis = SystemClock.uptimeMillis();
        Pair<Long, Integer> pair = hashMap.get(str);
        if (shouldLogNow(pair, uptimeMillis)) {
            String buildMessage = buildMessage(str, pair);
            hashMap.put(str, new Pair<>(Long.valueOf(uptimeMillis), 1));
            return buildMessage;
        }
        increaseLogCount(hashMap, str);
        return "";
    }

    public static String buildMessage(String str, Pair<Long, Integer> pair) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(pair == null ? 1 : ((Integer) pair.second).intValue());
        sb.append("]:");
        sb.append(str);
        return sb.toString();
    }

    public static void increaseLogCount(HashMap<String, Pair<Long, Integer>> hashMap, String str) {
        Pair<Long, Integer> pair = hashMap.get(str);
        if (pair != null) {
            hashMap.put(str, new Pair<>((Long) pair.first, Integer.valueOf(((Integer) pair.second).intValue() + 1)));
        }
    }

    public static boolean shouldLogNow(Pair<Long, Integer> pair, long j) {
        return pair == null || j - ((Long) pair.first).longValue() > 20000;
    }
}
