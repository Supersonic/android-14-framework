package com.android.server.notification;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.service.notification.IConditionProvider;
import android.service.notification.ZenModeConfig;
import android.util.Log;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
/* loaded from: classes2.dex */
public class ZenLog {
    public static final SimpleDateFormat FORMAT;
    public static final String[] MSGS;
    public static final int SIZE;
    public static final long[] TIMES;
    public static final int[] TYPES;
    public static int sNext;
    public static int sSize;

    public static String ringerModeToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? "unknown" : "normal" : "vibrate" : "silent";
    }

    public static String typeToString(int i) {
        switch (i) {
            case 1:
                return "intercepted";
            case 2:
                return "allow_disable";
            case 3:
                return "set_ringer_mode_external";
            case 4:
                return "set_ringer_mode_internal";
            case 5:
                return "downtime";
            case 6:
                return "set_zen_mode";
            case 7:
                return "update_zen_mode";
            case 8:
                return "exit_condition";
            case 9:
                return "subscribe";
            case 10:
                return "unsubscribe";
            case 11:
                return "config";
            case 12:
                return "not_intercepted";
            case 13:
                return "disable_effects";
            case 14:
                return "suppressor_changed";
            case 15:
                return "listener_hints_changed";
            case 16:
                return "set_notification_policy";
            case 17:
                return "set_consolidated_policy";
            case 18:
                return "matches_call_filter";
            case 19:
                return "record_caller";
            case 20:
                return "check_repeat_caller";
            case 21:
                return "alert_on_updated_intercept";
            default:
                return "unknown";
        }
    }

    public static String zenModeToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? i != 3 ? "unknown" : "alarms" : "no_interruptions" : "important_interruptions" : "off";
    }

    static {
        int i = Build.IS_DEBUGGABLE ? 200 : 100;
        SIZE = i;
        TIMES = new long[i];
        TYPES = new int[i];
        MSGS = new String[i];
        FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
    }

    public static void traceIntercepted(NotificationRecord notificationRecord, String str) {
        append(1, notificationRecord.getKey() + "," + str);
    }

    public static void traceNotIntercepted(NotificationRecord notificationRecord, String str) {
        append(12, notificationRecord.getKey() + "," + str);
    }

    public static void traceAlertOnUpdatedIntercept(NotificationRecord notificationRecord) {
        append(21, notificationRecord.getKey());
    }

    public static void traceSetRingerModeExternal(int i, int i2, String str, int i3, int i4) {
        append(3, str + ",e:" + ringerModeToString(i) + "->" + ringerModeToString(i2) + ",i:" + ringerModeToString(i3) + "->" + ringerModeToString(i4));
    }

    public static void traceSetRingerModeInternal(int i, int i2, String str, int i3, int i4) {
        append(4, str + ",i:" + ringerModeToString(i) + "->" + ringerModeToString(i2) + ",e:" + ringerModeToString(i3) + "->" + ringerModeToString(i4));
    }

    public static void traceSetZenMode(int i, String str) {
        append(6, zenModeToString(i) + "," + str);
    }

    public static void traceSetConsolidatedZenPolicy(NotificationManager.Policy policy, String str) {
        append(17, policy.toString() + "," + str);
    }

    public static void traceSetNotificationPolicy(String str, int i, NotificationManager.Policy policy) {
        String str2 = "pkg=" + str + " targetSdk=" + i + " NotificationPolicy=" + policy.toString();
        append(16, str2);
        Log.d("ZenLog", "Zen Policy Changed: " + str2);
    }

    public static void traceSubscribe(Uri uri, IConditionProvider iConditionProvider, RemoteException remoteException) {
        append(9, uri + "," + subscribeResult(iConditionProvider, remoteException));
    }

    public static void traceUnsubscribe(Uri uri, IConditionProvider iConditionProvider, RemoteException remoteException) {
        append(10, uri + "," + subscribeResult(iConditionProvider, remoteException));
    }

    public static void traceConfig(String str, ZenModeConfig zenModeConfig, ZenModeConfig zenModeConfig2) {
        if (ZenModeConfig.diff(zenModeConfig, zenModeConfig2).isEmpty()) {
            append(11, str + " no changes");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(",\n");
        sb.append(zenModeConfig2 != null ? zenModeConfig2.toString() : null);
        sb.append(",\n");
        sb.append(ZenModeConfig.diff(zenModeConfig, zenModeConfig2));
        append(11, sb.toString());
    }

    public static void traceDisableEffects(NotificationRecord notificationRecord, String str) {
        append(13, notificationRecord.getKey() + "," + str);
    }

    public static void traceEffectsSuppressorChanged(List<ComponentName> list, List<ComponentName> list2, long j) {
        append(14, "suppressed effects:" + j + "," + componentListToString(list) + "->" + componentListToString(list2));
    }

    public static void traceListenerHintsChanged(int i, int i2, int i3) {
        append(15, hintsToString(i) + "->" + hintsToString(i2) + ",listeners=" + i3);
    }

    public static void traceMatchesCallFilter(boolean z, String str, int i) {
        append(18, "result=" + z + ", reason=" + str + ", calling uid=" + i);
    }

    public static void traceRecordCaller(boolean z, boolean z2) {
        append(19, "has phone number=" + z + ", has uri=" + z2);
    }

    public static void traceCheckRepeatCaller(boolean z, boolean z2, boolean z3) {
        append(20, "res=" + z + ", given phone number=" + z2 + ", given uri=" + z3);
    }

    public static String subscribeResult(IConditionProvider iConditionProvider, RemoteException remoteException) {
        return iConditionProvider == null ? "no provider" : remoteException != null ? remoteException.getMessage() : "ok";
    }

    public static String hintsToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? i != 4 ? Integer.toString(i) : "disable_call_effects" : "disable_notification_effects" : "disable_effects" : "none";
    }

    public static String componentToString(ComponentName componentName) {
        if (componentName != null) {
            return componentName.toShortString();
        }
        return null;
    }

    public static String componentListToString(List<ComponentName> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(componentToString(list.get(i)));
        }
        return sb.toString();
    }

    public static void append(int i, String str) {
        String[] strArr = MSGS;
        synchronized (strArr) {
            TIMES[sNext] = System.currentTimeMillis();
            int[] iArr = TYPES;
            int i2 = sNext;
            iArr[i2] = i;
            strArr[i2] = str;
            int i3 = SIZE;
            sNext = (i2 + 1) % i3;
            int i4 = sSize;
            if (i4 < i3) {
                sSize = i4 + 1;
            }
        }
    }

    public static void dump(PrintWriter printWriter, String str) {
        synchronized (MSGS) {
            int i = sNext - sSize;
            int i2 = SIZE;
            int i3 = (i + i2) % i2;
            for (int i4 = 0; i4 < sSize; i4++) {
                int i5 = (i3 + i4) % SIZE;
                printWriter.print(str);
                printWriter.print(FORMAT.format(new Date(TIMES[i5])));
                printWriter.print(' ');
                printWriter.print(typeToString(TYPES[i5]));
                printWriter.print(": ");
                printWriter.println(MSGS[i5]);
            }
        }
    }
}
