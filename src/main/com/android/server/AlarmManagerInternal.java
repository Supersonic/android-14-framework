package com.android.server;

import android.app.PendingIntent;
/* loaded from: classes.dex */
public interface AlarmManagerInternal {

    /* loaded from: classes.dex */
    public interface InFlightListener {
        void broadcastAlarmComplete(int i);

        void broadcastAlarmPending(int i);
    }

    boolean isIdling();

    void registerInFlightListener(InFlightListener inFlightListener);

    void remove(PendingIntent pendingIntent);

    void removeAlarmsForUid(int i);

    void setTime(long j, int i, String str);

    void setTimeZone(String str, int i, String str2);

    boolean shouldGetBucketElevation(String str, int i);
}
