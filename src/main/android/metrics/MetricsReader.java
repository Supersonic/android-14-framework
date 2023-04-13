package android.metrics;

import android.annotation.SystemApi;
import android.util.EventLog;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
@SystemApi
/* loaded from: classes2.dex */
public class MetricsReader {
    private Queue<LogMaker> mPendingQueue = new LinkedList();
    private Queue<LogMaker> mSeenQueue = new LinkedList();
    private int[] LOGTAGS = {524292};
    private LogReader mReader = new LogReader();
    private int mCheckpointTag = -1;

    public void setLogReader(LogReader reader) {
        this.mReader = reader;
    }

    public void read(long horizonMs) {
        ArrayList<Event> nativeEvents = new ArrayList<>();
        try {
            this.mReader.readEvents(this.LOGTAGS, horizonMs, nativeEvents);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.mPendingQueue.clear();
        this.mSeenQueue.clear();
        Iterator<Event> it = nativeEvents.iterator();
        while (it.hasNext()) {
            Event event = it.next();
            long eventTimestampMs = event.getTimeMillis();
            Object data = event.getData();
            Object[] objects = data instanceof Object[] ? (Object[]) data : new Object[]{data};
            LogMaker log = new LogMaker(objects).setTimestamp(eventTimestampMs).setUid(event.getUid()).setProcessId(event.getProcessId());
            if (log.getCategory() == 920) {
                if (log.getSubtype() == this.mCheckpointTag) {
                    this.mPendingQueue.clear();
                }
            } else {
                this.mPendingQueue.offer(log);
            }
        }
    }

    public void checkpoint() {
        int currentTimeMillis = (int) (System.currentTimeMillis() % 2147483647L);
        this.mCheckpointTag = currentTimeMillis;
        this.mReader.writeCheckpoint(currentTimeMillis);
        this.mPendingQueue.clear();
        this.mSeenQueue.clear();
    }

    public void reset() {
        this.mSeenQueue.addAll(this.mPendingQueue);
        this.mPendingQueue.clear();
        this.mCheckpointTag = -1;
        Queue<LogMaker> tmp = this.mPendingQueue;
        this.mPendingQueue = this.mSeenQueue;
        this.mSeenQueue = tmp;
    }

    public boolean hasNext() {
        return !this.mPendingQueue.isEmpty();
    }

    public LogMaker next() {
        LogMaker next = this.mPendingQueue.poll();
        if (next != null) {
            this.mSeenQueue.offer(next);
        }
        return next;
    }

    /* loaded from: classes2.dex */
    public static class Event {
        Object mData;
        int mPid;
        long mTimeMillis;
        int mUid;

        public Event(long timeMillis, int pid, int uid, Object data) {
            this.mTimeMillis = timeMillis;
            this.mPid = pid;
            this.mUid = uid;
            this.mData = data;
        }

        Event(EventLog.Event nativeEvent) {
            this.mTimeMillis = TimeUnit.MILLISECONDS.convert(nativeEvent.getTimeNanos(), TimeUnit.NANOSECONDS);
            this.mPid = nativeEvent.getProcessId();
            this.mUid = nativeEvent.getUid();
            this.mData = nativeEvent.getData();
        }

        public long getTimeMillis() {
            return this.mTimeMillis;
        }

        public int getProcessId() {
            return this.mPid;
        }

        public int getUid() {
            return this.mUid;
        }

        public Object getData() {
            return this.mData;
        }

        public void setData(Object data) {
            this.mData = data;
        }
    }

    /* loaded from: classes2.dex */
    public static class LogReader {
        public void readEvents(int[] tags, long horizonMs, Collection<Event> events) throws IOException {
            ArrayList<EventLog.Event> nativeEvents = new ArrayList<>();
            long horizonNs = TimeUnit.NANOSECONDS.convert(horizonMs, TimeUnit.MILLISECONDS);
            EventLog.readEventsOnWrapping(tags, horizonNs, nativeEvents);
            Iterator<EventLog.Event> it = nativeEvents.iterator();
            while (it.hasNext()) {
                EventLog.Event nativeEvent = it.next();
                Event event = new Event(nativeEvent);
                events.add(event);
            }
        }

        public void writeCheckpoint(int tag) {
            MetricsLogger logger = new MetricsLogger();
            logger.action(MetricsProto.MetricsEvent.METRICS_CHECKPOINT, tag);
        }
    }
}
