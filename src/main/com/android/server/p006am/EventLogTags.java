package com.android.server.p006am;

import android.util.EventLog;
/* renamed from: com.android.server.am.EventLogTags */
/* loaded from: classes.dex */
public class EventLogTags {
    public static void writeConfigurationChanged(int i) {
        EventLog.writeEvent(2719, i);
    }

    public static void writeCpu(int i, int i2, int i3, int i4, int i5, int i6) {
        EventLog.writeEvent(2721, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6));
    }

    public static void writeBootProgressAmsReady(long j) {
        EventLog.writeEvent(3040, j);
    }

    public static void writeBootProgressEnableScreen(long j) {
        EventLog.writeEvent(3050, j);
    }

    public static void writeAmProcBound(int i, int i2, String str) {
        EventLog.writeEvent(30010, Integer.valueOf(i), Integer.valueOf(i2), str);
    }

    public static void writeAmProcDied(int i, int i2, String str, int i3, int i4) {
        EventLog.writeEvent(30011, Integer.valueOf(i), Integer.valueOf(i2), str, Integer.valueOf(i3), Integer.valueOf(i4));
    }

    public static void writeAmLowMemory(int i) {
        EventLog.writeEvent(30017, i);
    }

    public static void writeAmDropProcess(int i) {
        EventLog.writeEvent(30033, i);
    }

    public static void writeAmProviderLostProcess(int i, String str, int i2, String str2) {
        EventLog.writeEvent(30036, Integer.valueOf(i), str, Integer.valueOf(i2), str2);
    }

    public static void writeAmProcessStartTimeout(int i, int i2, int i3, String str) {
        EventLog.writeEvent(30037, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), str);
    }

    public static void writeAmCrash(int i, int i2, String str, int i3, String str2, String str3, String str4, int i4) {
        EventLog.writeEvent(30039, Integer.valueOf(i), Integer.valueOf(i2), str, Integer.valueOf(i3), str2, str3, str4, Integer.valueOf(i4));
    }

    public static void writeAmWtf(int i, int i2, String str, int i3, String str2, String str3) {
        EventLog.writeEvent(30040, Integer.valueOf(i), Integer.valueOf(i2), str, Integer.valueOf(i3), str2, str3);
    }

    public static void writeAmSwitchUser(int i) {
        EventLog.writeEvent(30041, i);
    }

    public static void writeAmPreBoot(int i, String str) {
        EventLog.writeEvent(30045, Integer.valueOf(i), str);
    }

    public static void writeAmMeminfo(long j, long j2, long j3, long j4, long j5) {
        EventLog.writeEvent(30046, Long.valueOf(j), Long.valueOf(j2), Long.valueOf(j3), Long.valueOf(j4), Long.valueOf(j5));
    }

    public static void writeAmPss(int i, int i2, String str, long j, long j2, long j3, long j4, int i3, int i4, long j5) {
        EventLog.writeEvent(30047, Integer.valueOf(i), Integer.valueOf(i2), str, Long.valueOf(j), Long.valueOf(j2), Long.valueOf(j3), Long.valueOf(j4), Integer.valueOf(i3), Integer.valueOf(i4), Long.valueOf(j5));
    }

    public static void writeAmMemFactor(int i, int i2) {
        EventLog.writeEvent(30050, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public static void writeAmUserStateChanged(int i, int i2) {
        EventLog.writeEvent(30051, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public static void writeAmUidRunning(int i) {
        EventLog.writeEvent(30052, i);
    }

    public static void writeAmUidStopped(int i) {
        EventLog.writeEvent(30053, i);
    }

    public static void writeAmUidActive(int i) {
        EventLog.writeEvent(30054, i);
    }

    public static void writeAmUidIdle(int i) {
        EventLog.writeEvent(30055, i);
    }

    public static void writeAmStopIdleService(int i, String str) {
        EventLog.writeEvent(30056, Integer.valueOf(i), str);
    }

    public static void writeAmIntentSenderRedirectUser(int i) {
        EventLog.writeEvent(30110, i);
    }
}
