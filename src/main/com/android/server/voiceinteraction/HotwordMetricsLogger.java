package com.android.server.voiceinteraction;

import com.android.internal.util.FrameworkStatsLog;
/* loaded from: classes2.dex */
public final class HotwordMetricsLogger {
    public static int getAudioEgressDetectorType(int i) {
        int i2 = 1;
        if (i != 1) {
            i2 = 2;
            if (i != 2) {
                return 0;
            }
        }
        return i2;
    }

    public static int getCreateMetricsDetectorType(int i) {
        int i2 = 1;
        if (i != 1) {
            i2 = 2;
            if (i != 2) {
                return 0;
            }
        }
        return i2;
    }

    public static int getDetectorMetricsDetectorType(int i) {
        int i2 = 1;
        if (i != 1) {
            i2 = 2;
            if (i != 2) {
                return 0;
            }
        }
        return i2;
    }

    public static int getInitMetricsDetectorType(int i) {
        int i2 = 1;
        if (i != 1) {
            i2 = 2;
            if (i != 2) {
                return 0;
            }
        }
        return i2;
    }

    public static int getKeyphraseMetricsDetectorType(int i) {
        int i2 = 1;
        if (i != 1) {
            i2 = 2;
            if (i != 2) {
                return 0;
            }
        }
        return i2;
    }

    public static int getRestartMetricsDetectorType(int i) {
        int i2 = 1;
        if (i != 1) {
            i2 = 2;
            if (i != 2) {
                return 0;
            }
        }
        return i2;
    }

    public static void writeDetectorCreateEvent(int i, boolean z, int i2) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.HOTWORD_DETECTOR_CREATE_REQUESTED, getCreateMetricsDetectorType(i), z, i2);
    }

    public static void writeServiceInitResultEvent(int i, int i2, int i3) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.HOTWORD_DETECTION_SERVICE_INIT_RESULT_REPORTED, getInitMetricsDetectorType(i), i2, i3);
    }

    public static void writeServiceRestartEvent(int i, int i2, int i3) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.HOTWORD_DETECTION_SERVICE_RESTARTED, getRestartMetricsDetectorType(i), i2, i3);
    }

    public static void writeKeyphraseTriggerEvent(int i, int i2, int i3) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.HOTWORD_DETECTOR_KEYPHRASE_TRIGGERED, getKeyphraseMetricsDetectorType(i), i2, i3);
    }

    public static void writeDetectorEvent(int i, int i2, int i3) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.HOTWORD_DETECTOR_EVENTS, getDetectorMetricsDetectorType(i), i2, i3);
    }

    public static void writeAudioEgressEvent(int i, int i2, int i3, int i4, int i5, int i6) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.HOTWORD_AUDIO_EGRESS_EVENT_REPORTED, getAudioEgressDetectorType(i), i2, i3, i4, i5, i6);
    }
}
