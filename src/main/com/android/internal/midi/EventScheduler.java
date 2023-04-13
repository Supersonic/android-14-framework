package com.android.internal.midi;

import java.util.SortedMap;
import java.util.TreeMap;
/* loaded from: classes4.dex */
public class EventScheduler {
    public static final long NANOS_PER_MILLI = 1000000;
    private boolean mClosed;
    private final Object mLock = new Object();
    protected FastEventQueue mEventPool = null;
    private int mMaxPoolSize = 200;
    protected volatile SortedMap<Long, FastEventQueue> mEventBuffer = new TreeMap();

    /* loaded from: classes4.dex */
    public static class FastEventQueue {
        volatile long mEventsAdded = 1;
        volatile long mEventsRemoved = 0;
        volatile SchedulableEvent mFirst;
        volatile SchedulableEvent mLast;

        public FastEventQueue(SchedulableEvent event) {
            this.mFirst = event;
            this.mLast = this.mFirst;
        }

        int size() {
            return (int) (this.mEventsAdded - this.mEventsRemoved);
        }

        public SchedulableEvent remove() {
            this.mEventsRemoved++;
            SchedulableEvent event = this.mFirst;
            this.mFirst = event.mNext;
            event.mNext = null;
            return event;
        }

        public void add(SchedulableEvent event) {
            event.mNext = null;
            this.mLast.mNext = event;
            this.mLast = event;
            this.mEventsAdded++;
        }
    }

    /* loaded from: classes4.dex */
    public static class SchedulableEvent {
        private volatile SchedulableEvent mNext = null;
        private long mTimestamp;

        public SchedulableEvent(long timestamp) {
            this.mTimestamp = timestamp;
        }

        public long getTimestamp() {
            return this.mTimestamp;
        }

        public void setTimestamp(long timestamp) {
            this.mTimestamp = timestamp;
        }
    }

    public SchedulableEvent removeEventfromPool() {
        FastEventQueue fastEventQueue = this.mEventPool;
        if (fastEventQueue == null || fastEventQueue.size() <= 1) {
            return null;
        }
        SchedulableEvent event = this.mEventPool.remove();
        return event;
    }

    public void addEventToPool(SchedulableEvent event) {
        FastEventQueue fastEventQueue = this.mEventPool;
        if (fastEventQueue == null) {
            this.mEventPool = new FastEventQueue(event);
        } else if (fastEventQueue.size() < this.mMaxPoolSize) {
            this.mEventPool.add(event);
        }
    }

    public void add(SchedulableEvent event) {
        Object lock = getLock();
        synchronized (lock) {
            FastEventQueue list = this.mEventBuffer.get(Long.valueOf(event.getTimestamp()));
            if (list == null) {
                long lowestTime = this.mEventBuffer.isEmpty() ? Long.MAX_VALUE : this.mEventBuffer.firstKey().longValue();
                this.mEventBuffer.put(Long.valueOf(event.getTimestamp()), new FastEventQueue(event));
                if (event.getTimestamp() < lowestTime) {
                    lock.notify();
                }
            } else {
                list.add(event);
            }
        }
    }

    protected SchedulableEvent removeNextEventLocked(long lowestTime) {
        FastEventQueue list = this.mEventBuffer.get(Long.valueOf(lowestTime));
        if (list.size() == 1) {
            this.mEventBuffer.remove(Long.valueOf(lowestTime));
        }
        SchedulableEvent event = list.remove();
        return event;
    }

    public SchedulableEvent getNextEvent(long time) {
        SchedulableEvent event = null;
        Object lock = getLock();
        synchronized (lock) {
            if (!this.mEventBuffer.isEmpty()) {
                long lowestTime = this.mEventBuffer.firstKey().longValue();
                if (lowestTime <= time) {
                    event = removeNextEventLocked(lowestTime);
                }
            }
        }
        return event;
    }

    public SchedulableEvent waitNextEvent() throws InterruptedException {
        SchedulableEvent event = null;
        Object lock = getLock();
        synchronized (lock) {
            while (true) {
                if (this.mClosed) {
                    break;
                }
                long millisToWait = 2147483647L;
                if (!this.mEventBuffer.isEmpty()) {
                    long now = System.nanoTime();
                    long lowestTime = this.mEventBuffer.firstKey().longValue();
                    if (lowestTime <= now) {
                        event = removeNextEventLocked(lowestTime);
                        break;
                    }
                    long nanosToWait = lowestTime - now;
                    millisToWait = (nanosToWait / 1000000) + 1;
                    if (millisToWait > 2147483647L) {
                        millisToWait = 2147483647L;
                    }
                }
                lock.wait((int) millisToWait);
            }
        }
        return event;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void flush() {
        this.mEventBuffer = new TreeMap();
    }

    public void close() {
        Object lock = getLock();
        synchronized (lock) {
            this.mClosed = true;
            lock.notify();
        }
    }

    protected Object getLock() {
        return this.mLock;
    }
}
