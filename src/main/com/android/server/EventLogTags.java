package com.android.server;

import android.util.EventLog;
/* loaded from: classes.dex */
public class EventLogTags {
    public static void writePowerSleepRequested(int i) {
        EventLog.writeEvent(2724, i);
    }

    public static void writePowerScreenState(int i, int i2, long j, int i3, int i4) {
        EventLog.writeEvent(2728, Integer.valueOf(i), Integer.valueOf(i2), Long.valueOf(j), Integer.valueOf(i3), Integer.valueOf(i4));
    }

    public static void writePowerSoftSleepRequested(long j) {
        EventLog.writeEvent(2731, j);
    }

    public static void writeBatterySaverMode(int i, int i2, int i3, int i4, int i5, String str, int i6) {
        EventLog.writeEvent(2739, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5), str, Integer.valueOf(i6));
    }

    public static void writeBatterySavingStats(int i, int i2, int i3, long j, int i4, int i5, long j2, int i6, int i7) {
        EventLog.writeEvent(27390, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Long.valueOf(j), Integer.valueOf(i4), Integer.valueOf(i5), Long.valueOf(j2), Integer.valueOf(i6), Integer.valueOf(i7));
    }

    public static void writeUserActivityTimeoutOverride(long j) {
        EventLog.writeEvent(27391, j);
    }

    public static void writeBatterySaverSetting(int i) {
        EventLog.writeEvent(27392, i);
    }

    public static void writeCacheFileDeleted(String str) {
        EventLog.writeEvent(2748, str);
    }

    public static void writeStorageState(String str, int i, int i2, long j, long j2) {
        EventLog.writeEvent(2749, str, Integer.valueOf(i), Integer.valueOf(i2), Long.valueOf(j), Long.valueOf(j2));
    }

    public static void writeNotificationEnqueue(int i, int i2, String str, int i3, String str2, int i4, String str3, int i5) {
        EventLog.writeEvent(2750, Integer.valueOf(i), Integer.valueOf(i2), str, Integer.valueOf(i3), str2, Integer.valueOf(i4), str3, Integer.valueOf(i5));
    }

    public static void writeNotificationCancel(int i, int i2, String str, int i3, String str2, int i4, int i5, int i6, int i7, String str3) {
        EventLog.writeEvent(2751, Integer.valueOf(i), Integer.valueOf(i2), str, Integer.valueOf(i3), str2, Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6), Integer.valueOf(i7), str3);
    }

    public static void writeNotificationCancelAll(int i, int i2, String str, int i3, int i4, int i5, int i6, String str2) {
        EventLog.writeEvent(2752, Integer.valueOf(i), Integer.valueOf(i2), str, Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6), str2);
    }

    public static void writeNotificationPanelRevealed(int i) {
        EventLog.writeEvent(27500, i);
    }

    public static void writeNotificationPanelHidden() {
        EventLog.writeEvent(27501, new Object[0]);
    }

    public static void writeNotificationClicked(String str, int i, int i2, int i3, int i4, int i5) {
        EventLog.writeEvent(27520, str, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5));
    }

    public static void writeNotificationActionClicked(String str, int i, int i2, int i3, int i4, int i5, int i6) {
        EventLog.writeEvent(27521, str, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6));
    }

    public static void writeNotificationCanceled(String str, int i, int i2, int i3, int i4, int i5, int i6, String str2) {
        EventLog.writeEvent(27530, str, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6), str2);
    }

    public static void writeNotificationVisibility(String str, int i, int i2, int i3, int i4, int i5) {
        EventLog.writeEvent(27531, str, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5));
    }

    public static void writeNotificationAlert(String str, int i, int i2, int i3) {
        EventLog.writeEvent(27532, str, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3));
    }

    public static void writeNotificationAutogrouped(String str) {
        EventLog.writeEvent(27533, str);
    }

    public static void writeNotificationUnautogrouped(String str) {
        EventLog.writeEvent(275534, str);
    }

    public static void writeNotificationAdjusted(String str, String str2, String str3) {
        EventLog.writeEvent(27535, str, str2, str3);
    }

    public static void writeRescueNote(int i, int i2, long j) {
        EventLog.writeEvent(2900, Integer.valueOf(i), Integer.valueOf(i2), Long.valueOf(j));
    }

    public static void writeRescueSuccess(int i) {
        EventLog.writeEvent(2902, i);
    }

    public static void writeRescueFailure(int i, String str) {
        EventLog.writeEvent(2903, Integer.valueOf(i), str);
    }

    public static void writeBackupAgentFailure(String str, String str2) {
        EventLog.writeEvent(2823, str, str2);
    }

    public static void writePmCriticalInfo(String str) {
        EventLog.writeEvent(3120, str);
    }

    public static void writePmSnapshotRebuild(int i, int i2) {
        EventLog.writeEvent(3131, Integer.valueOf(i), Integer.valueOf(i2));
    }

    public static void writeDeviceIdle(int i, String str) {
        EventLog.writeEvent(34000, Integer.valueOf(i), str);
    }

    public static void writeDeviceIdleStep() {
        EventLog.writeEvent(34001, new Object[0]);
    }

    public static void writeDeviceIdleWakeFromIdle(int i, String str) {
        EventLog.writeEvent(34002, Integer.valueOf(i), str);
    }

    public static void writeDeviceIdleOnStart() {
        EventLog.writeEvent(34003, new Object[0]);
    }

    public static void writeDeviceIdleOnPhase(String str) {
        EventLog.writeEvent(34004, str);
    }

    public static void writeDeviceIdleOnComplete() {
        EventLog.writeEvent(34005, new Object[0]);
    }

    public static void writeDeviceIdleOffStart(String str) {
        EventLog.writeEvent(34006, str);
    }

    public static void writeDeviceIdleOffPhase(String str) {
        EventLog.writeEvent(34007, str);
    }

    public static void writeDeviceIdleOffComplete() {
        EventLog.writeEvent(34008, new Object[0]);
    }

    public static void writeDeviceIdleLight(int i, String str) {
        EventLog.writeEvent(34009, Integer.valueOf(i), str);
    }

    public static void writeDeviceIdleLightStep() {
        EventLog.writeEvent(34010, new Object[0]);
    }

    public static void writeIfwIntentMatched(int i, String str, int i2, int i3, String str2, String str3, String str4, String str5, int i4) {
        EventLog.writeEvent(51400, Integer.valueOf(i), str, Integer.valueOf(i2), Integer.valueOf(i3), str2, str3, str4, str5, Integer.valueOf(i4));
    }

    public static void writeVolumeChanged(int i, int i2, int i3, int i4, String str) {
        EventLog.writeEvent(40000, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), str);
    }

    public static void writeStreamDevicesChanged(int i, int i2, int i3) {
        EventLog.writeEvent(40001, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3));
    }

    public static void writeCameraGestureTriggered(long j, long j2, long j3, int i) {
        EventLog.writeEvent(40100, Long.valueOf(j), Long.valueOf(j2), Long.valueOf(j3), Integer.valueOf(i));
    }
}
