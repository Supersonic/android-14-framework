package com.android.server.policy;

import android.util.EventLog;
/* loaded from: classes2.dex */
public class EventLogTags {
    public static void writeScreenToggled(int i) {
        EventLog.writeEvent(70000, i);
    }

    public static void writeInterceptPower(String str, int i, int i2) {
        EventLog.writeEvent(70001, str, Integer.valueOf(i), Integer.valueOf(i2));
    }
}
