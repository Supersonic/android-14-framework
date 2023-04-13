package com.android.internal.jank;

import android.util.EventLog;
/* loaded from: classes4.dex */
public class EventLogTags {
    public static final int JANK_CUJ_EVENTS_BEGIN_REQUEST = 37001;
    public static final int JANK_CUJ_EVENTS_CANCEL_REQUEST = 37003;
    public static final int JANK_CUJ_EVENTS_END_REQUEST = 37002;

    private EventLogTags() {
    }

    public static void writeJankCujEventsBeginRequest(int cujType, long unixTimeNs, long elapsedTimeNs, long uptimeNs) {
        EventLog.writeEvent((int) JANK_CUJ_EVENTS_BEGIN_REQUEST, Integer.valueOf(cujType), Long.valueOf(unixTimeNs), Long.valueOf(elapsedTimeNs), Long.valueOf(uptimeNs));
    }

    public static void writeJankCujEventsEndRequest(int cujType, long unixTimeNs, long elapsedTimeNs, long uptimeTimeNs) {
        EventLog.writeEvent((int) JANK_CUJ_EVENTS_END_REQUEST, Integer.valueOf(cujType), Long.valueOf(unixTimeNs), Long.valueOf(elapsedTimeNs), Long.valueOf(uptimeTimeNs));
    }

    public static void writeJankCujEventsCancelRequest(int cujType, long unixTimeNs, long elapsedTimeNs, long uptimeTimeNs) {
        EventLog.writeEvent((int) JANK_CUJ_EVENTS_CANCEL_REQUEST, Integer.valueOf(cujType), Long.valueOf(unixTimeNs), Long.valueOf(elapsedTimeNs), Long.valueOf(uptimeTimeNs));
    }
}
