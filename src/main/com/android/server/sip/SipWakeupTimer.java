package com.android.server.sip;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.telephony.Rlog;
import gov.nist.core.Separators;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.Executor;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class SipWakeupTimer extends BroadcastReceiver {
    private static final boolean DBG = false;
    private static final String TAG = "SipWakeupTimer";
    private static final String TRIGGER_TIME = "TriggerTime";
    private AlarmManager mAlarmManager;
    private Context mContext;
    private TreeSet<MyEvent> mEventQueue = new TreeSet<>(new MyEventComparator());
    private Executor mExecutor;
    private PendingIntent mPendingIntent;

    public SipWakeupTimer(Context context, Executor executor) {
        this.mContext = context;
        this.mAlarmManager = (AlarmManager) context.getSystemService("alarm");
        IntentFilter filter = new IntentFilter(getAction());
        context.registerReceiver(this, filter, 2);
        this.mExecutor = executor;
    }

    public synchronized void stop() {
        this.mContext.unregisterReceiver(this);
        PendingIntent pendingIntent = this.mPendingIntent;
        if (pendingIntent != null) {
            this.mAlarmManager.cancel(pendingIntent);
            this.mPendingIntent = null;
        }
        this.mEventQueue.clear();
        this.mEventQueue = null;
    }

    private boolean stopped() {
        if (this.mEventQueue == null) {
            return true;
        }
        return DBG;
    }

    private void cancelAlarm() {
        this.mAlarmManager.cancel(this.mPendingIntent);
        this.mPendingIntent = null;
    }

    private void recalculatePeriods() {
        if (this.mEventQueue.isEmpty()) {
            return;
        }
        MyEvent firstEvent = this.mEventQueue.first();
        int minPeriod = firstEvent.mMaxPeriod;
        long minTriggerTime = firstEvent.mTriggerTime;
        Iterator<MyEvent> it = this.mEventQueue.iterator();
        while (it.hasNext()) {
            MyEvent e = it.next();
            e.mPeriod = (e.mMaxPeriod / minPeriod) * minPeriod;
            int interval = (int) ((e.mLastTriggerTime + e.mMaxPeriod) - minTriggerTime);
            e.mTriggerTime = ((interval / minPeriod) * minPeriod) + minTriggerTime;
        }
        TreeSet<MyEvent> newQueue = new TreeSet<>(this.mEventQueue.comparator());
        newQueue.addAll(this.mEventQueue);
        this.mEventQueue.clear();
        this.mEventQueue = newQueue;
    }

    private void insertEvent(MyEvent event) {
        long now = SystemClock.elapsedRealtime();
        if (this.mEventQueue.isEmpty()) {
            event.mTriggerTime = event.mPeriod + now;
            this.mEventQueue.add(event);
            return;
        }
        MyEvent firstEvent = this.mEventQueue.first();
        int minPeriod = firstEvent.mPeriod;
        if (minPeriod <= event.mMaxPeriod) {
            event.mPeriod = (event.mMaxPeriod / minPeriod) * minPeriod;
            int interval = event.mMaxPeriod;
            event.mTriggerTime = firstEvent.mTriggerTime + (((interval - ((int) (firstEvent.mTriggerTime - now))) / minPeriod) * minPeriod);
            this.mEventQueue.add(event);
            return;
        }
        long triggerTime = event.mPeriod + now;
        if (firstEvent.mTriggerTime < triggerTime) {
            event.mTriggerTime = firstEvent.mTriggerTime;
            event.mLastTriggerTime -= event.mPeriod;
        } else {
            event.mTriggerTime = triggerTime;
        }
        this.mEventQueue.add(event);
        recalculatePeriods();
    }

    public synchronized void set(int period, Runnable callback) {
        if (stopped()) {
            return;
        }
        long now = SystemClock.elapsedRealtime();
        MyEvent event = new MyEvent(period, callback, now);
        insertEvent(event);
        if (this.mEventQueue.first() == event) {
            if (this.mEventQueue.size() > 1) {
                cancelAlarm();
            }
            scheduleNext();
        }
        long j = event.mTriggerTime;
    }

    public synchronized void cancel(Runnable callback) {
        if (!stopped() && !this.mEventQueue.isEmpty()) {
            MyEvent firstEvent = this.mEventQueue.first();
            Iterator<MyEvent> iter = this.mEventQueue.iterator();
            while (iter.hasNext()) {
                MyEvent event = iter.next();
                if (event.mCallback == callback) {
                    iter.remove();
                }
            }
            if (this.mEventQueue.isEmpty()) {
                cancelAlarm();
            } else if (this.mEventQueue.first() != firstEvent) {
                cancelAlarm();
                MyEvent firstEvent2 = this.mEventQueue.first();
                firstEvent2.mPeriod = firstEvent2.mMaxPeriod;
                firstEvent2.mTriggerTime = firstEvent2.mLastTriggerTime + firstEvent2.mPeriod;
                recalculatePeriods();
                scheduleNext();
            }
        }
    }

    private void scheduleNext() {
        if (stopped() || this.mEventQueue.isEmpty()) {
            return;
        }
        if (this.mPendingIntent != null) {
            throw new RuntimeException("pendingIntent is not null!");
        }
        MyEvent event = this.mEventQueue.first();
        Intent intent = new Intent(getAction());
        intent.putExtra(TRIGGER_TIME, event.mTriggerTime);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.mContext, 0, intent, 134217728);
        this.mPendingIntent = pendingIntent;
        this.mAlarmManager.set(2, event.mTriggerTime, pendingIntent);
    }

    @Override // android.content.BroadcastReceiver
    public synchronized void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (getAction().equals(action) && intent.getExtras().containsKey(TRIGGER_TIME)) {
            this.mPendingIntent = null;
            long triggerTime = intent.getLongExtra(TRIGGER_TIME, -1L);
            execute(triggerTime);
        } else {
            log("onReceive: unrecognized intent: " + intent);
        }
    }

    private void printQueue() {
        int count = 0;
        Iterator<MyEvent> it = this.mEventQueue.iterator();
        while (it.hasNext()) {
            MyEvent event = it.next();
            log("     " + event + ": scheduled at " + showTime(event.mTriggerTime) + ": last at " + showTime(event.mLastTriggerTime));
            count++;
            if (count >= 5) {
                break;
            }
        }
        if (this.mEventQueue.size() > count) {
            log("     .....");
        } else if (count == 0) {
            log("     <empty>");
        }
    }

    private void execute(long triggerTime) {
        if (stopped() || this.mEventQueue.isEmpty()) {
            return;
        }
        Iterator<MyEvent> it = this.mEventQueue.iterator();
        while (it.hasNext()) {
            MyEvent event = it.next();
            if (event.mTriggerTime == triggerTime) {
                event.mLastTriggerTime = triggerTime;
                event.mTriggerTime += event.mPeriod;
                this.mExecutor.execute(event.mCallback);
            }
        }
        scheduleNext();
    }

    private String getAction() {
        return toString();
    }

    private String showTime(long time) {
        int ms = (int) (time % 1000);
        int s = (int) (time / 1000);
        int m = s / 60;
        return String.format("%d.%d.%d", Integer.valueOf(m), Integer.valueOf(s % 60), Integer.valueOf(ms));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MyEvent {
        Runnable mCallback;
        long mLastTriggerTime;
        int mMaxPeriod;
        int mPeriod;
        long mTriggerTime;

        MyEvent(int period, Runnable callback, long now) {
            this.mMaxPeriod = period;
            this.mPeriod = period;
            this.mCallback = callback;
            this.mLastTriggerTime = now;
        }

        public String toString() {
            String s = super.toString();
            return s.substring(s.indexOf(Separators.f10AT)) + Separators.COLON + (this.mPeriod / 1000) + Separators.COLON + (this.mMaxPeriod / 1000) + Separators.COLON + toString(this.mCallback);
        }

        private String toString(Object o) {
            String s = o.toString();
            int index = s.indexOf("$");
            return index > 0 ? s.substring(index + 1) : s;
        }
    }

    /* loaded from: classes.dex */
    private static class MyEventComparator implements Comparator<MyEvent> {
        private MyEventComparator() {
        }

        @Override // java.util.Comparator
        public int compare(MyEvent e1, MyEvent e2) {
            if (e1 == e2) {
                return 0;
            }
            int diff = e1.mMaxPeriod - e2.mMaxPeriod;
            if (diff == 0) {
                return -1;
            }
            return diff;
        }

        @Override // java.util.Comparator
        public boolean equals(Object that) {
            if (this == that) {
                return true;
            }
            return SipWakeupTimer.DBG;
        }
    }

    private void log(String s) {
        Rlog.d(TAG, s);
    }
}
