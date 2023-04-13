package com.android.internal.telephony.metrics;

import android.os.SystemClock;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyCallSession;
import java.util.ArrayDeque;
import java.util.Deque;
/* loaded from: classes.dex */
public class InProgressCallSession {
    private long mLastElapsedTimeMs;
    private int mLastKnownPhoneState;
    public final int phoneId;
    public final long startElapsedTimeMs;
    private boolean mEventsDropped = false;
    public final Deque<TelephonyProto$TelephonyCallSession.Event> events = new ArrayDeque();
    public final int startSystemTimeMin = TelephonyMetrics.roundSessionStart(System.currentTimeMillis());

    public boolean isEventsDropped() {
        return this.mEventsDropped;
    }

    public InProgressCallSession(int i) {
        this.phoneId = i;
        long elapsedRealtime = SystemClock.elapsedRealtime();
        this.startElapsedTimeMs = elapsedRealtime;
        this.mLastElapsedTimeMs = elapsedRealtime;
    }

    public void addEvent(CallSessionEventBuilder callSessionEventBuilder) {
        addEvent(SystemClock.elapsedRealtime(), callSessionEventBuilder);
    }

    public synchronized void addEvent(long j, CallSessionEventBuilder callSessionEventBuilder) {
        if (this.events.size() >= 300) {
            this.events.removeFirst();
            this.mEventsDropped = true;
        }
        callSessionEventBuilder.setDelay(TelephonyMetrics.toPrivacyFuzzedTimeInterval(this.mLastElapsedTimeMs, j));
        this.events.add(callSessionEventBuilder.build());
        this.mLastElapsedTimeMs = j;
    }

    public synchronized boolean containsCsCalls() {
        for (TelephonyProto$TelephonyCallSession.Event event : this.events) {
            if (event.type == 10) {
                return true;
            }
        }
        return false;
    }

    public void setLastKnownPhoneState(int i) {
        this.mLastKnownPhoneState = i;
    }

    public boolean isPhoneIdle() {
        return this.mLastKnownPhoneState == 1;
    }
}
