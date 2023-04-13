package android.telecom.Logging;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.telecom.Log;
import android.telecom.Logging.EventManager;
import android.telecom.Logging.SessionManager;
import android.text.TextUtils;
import android.util.Pair;
import com.android.internal.util.IndentingPrintWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.ToLongFunction;
/* loaded from: classes3.dex */
public class EventManager {
    public static final int DEFAULT_EVENTS_TO_CACHE = 10;
    public static final String TAG = "Logging.Events";
    private SessionManager.ISessionIdQueryHandler mSessionIdHandler;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final Object mSync = new Object();
    private final Map<Loggable, EventRecord> mCallEventRecordMap = new HashMap();
    private LinkedBlockingQueue<EventRecord> mEventRecords = new LinkedBlockingQueue<>(10);
    private List<EventListener> mEventListeners = new ArrayList();
    private final Map<String, List<TimedEventPair>> requestResponsePairs = new HashMap();

    /* loaded from: classes3.dex */
    public interface EventListener {
        void eventRecordAdded(EventRecord eventRecord);
    }

    /* loaded from: classes3.dex */
    public interface Loggable {
        String getDescription();

        String getId();
    }

    /* loaded from: classes3.dex */
    public static class TimedEventPair {
        private static final long DEFAULT_TIMEOUT = 3000;
        String mName;
        String mRequest;
        String mResponse;
        long mTimeoutMillis;

        public TimedEventPair(String request, String response, String name) {
            this.mTimeoutMillis = 3000L;
            this.mRequest = request;
            this.mResponse = response;
            this.mName = name;
        }

        public TimedEventPair(String request, String response, String name, long timeoutMillis) {
            this.mTimeoutMillis = 3000L;
            this.mRequest = request;
            this.mResponse = response;
            this.mName = name;
            this.mTimeoutMillis = timeoutMillis;
        }
    }

    public void addRequestResponsePair(TimedEventPair p) {
        if (this.requestResponsePairs.containsKey(p.mRequest)) {
            this.requestResponsePairs.get(p.mRequest).add(p);
            return;
        }
        ArrayList<TimedEventPair> responses = new ArrayList<>();
        responses.add(p);
        this.requestResponsePairs.put(p.mRequest, responses);
    }

    /* loaded from: classes3.dex */
    public static class Event {
        public Object data;
        public String eventId;
        public String sessionId;
        public long time;
        public final String timestampString;

        public Event(String eventId, String sessionId, long time, Object data) {
            this.eventId = eventId;
            this.sessionId = sessionId;
            this.time = time;
            this.timestampString = ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()).format(EventManager.DATE_TIME_FORMATTER);
            this.data = data;
        }
    }

    /* loaded from: classes3.dex */
    public class EventRecord {
        private final List<Event> mEvents = Collections.synchronizedList(new ArrayList());
        private final Loggable mRecordEntry;

        /* loaded from: classes3.dex */
        public class EventTiming extends TimedEvent<String> {
            public String name;
            public long time;

            public EventTiming(String name, long time) {
                this.name = name;
                this.time = time;
            }

            @Override // android.telecom.Logging.TimedEvent
            public String getKey() {
                return this.name;
            }

            @Override // android.telecom.Logging.TimedEvent
            public long getTime() {
                return this.time;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public class PendingResponse {
            String name;
            String requestEventId;
            long requestEventTimeMillis;
            long timeoutMillis;

            public PendingResponse(String requestEventId, long requestEventTimeMillis, long timeoutMillis, String name) {
                this.requestEventId = requestEventId;
                this.requestEventTimeMillis = requestEventTimeMillis;
                this.timeoutMillis = timeoutMillis;
                this.name = name;
            }
        }

        public EventRecord(Loggable recordEntry) {
            this.mRecordEntry = recordEntry;
        }

        public Loggable getRecordEntry() {
            return this.mRecordEntry;
        }

        public void addEvent(String event, String sessionId, Object data) {
            this.mEvents.add(new Event(event, sessionId, System.currentTimeMillis(), data));
            Log.m134i("Event", "RecordEntry %s: %s, %s", this.mRecordEntry.getId(), event, data);
        }

        public List<Event> getEvents() {
            return new ArrayList(this.mEvents);
        }

        public List<EventTiming> extractEventTimings() {
            Iterator<Event> it;
            if (this.mEvents == null) {
                return Collections.emptyList();
            }
            ArrayList<EventTiming> result = new ArrayList<>();
            Map<String, PendingResponse> pendingResponses = new HashMap<>();
            synchronized (this.mEvents) {
                Iterator<Event> it2 = this.mEvents.iterator();
                while (it2.hasNext()) {
                    Event event = it2.next();
                    if (!EventManager.this.requestResponsePairs.containsKey(event.eventId)) {
                        it = it2;
                    } else {
                        Iterator it3 = ((List) EventManager.this.requestResponsePairs.get(event.eventId)).iterator();
                        while (it3.hasNext()) {
                            TimedEventPair p = (TimedEventPair) it3.next();
                            pendingResponses.put(p.mResponse, new PendingResponse(event.eventId, event.time, p.mTimeoutMillis, p.mName));
                            it3 = it3;
                            it2 = it2;
                        }
                        it = it2;
                    }
                    PendingResponse pendingResponse = pendingResponses.remove(event.eventId);
                    if (pendingResponse != null) {
                        long elapsedTime = event.time - pendingResponse.requestEventTimeMillis;
                        if (elapsedTime < pendingResponse.timeoutMillis) {
                            result.add(new EventTiming(pendingResponse.name, elapsedTime));
                        }
                    }
                    it2 = it;
                }
            }
            return result;
        }

        public void dump(IndentingPrintWriter pw) {
            EventRecord record;
            pw.print(this.mRecordEntry.getDescription());
            pw.increaseIndent();
            for (Event event : getEvents()) {
                pw.print(event.timestampString);
                pw.print(" - ");
                pw.print(event.eventId);
                if (event.data != null) {
                    pw.print(" (");
                    Object data = event.data;
                    if ((data instanceof Loggable) && (record = (EventRecord) EventManager.this.mCallEventRecordMap.get(data)) != null) {
                        data = "RecordEntry " + record.mRecordEntry.getId();
                    }
                    pw.print(data);
                    pw.print(NavigationBarInflaterView.KEY_CODE_END);
                }
                if (!TextUtils.isEmpty(event.sessionId)) {
                    pw.print(":");
                    pw.print(event.sessionId);
                }
                pw.println();
            }
            pw.println("Timings (average for this call, milliseconds):");
            pw.increaseIndent();
            Map<String, Double> avgEventTimings = EventTiming.averageTimings(extractEventTimings());
            List<String> eventNames = new ArrayList<>(avgEventTimings.keySet());
            Collections.sort(eventNames);
            for (String eventName : eventNames) {
                pw.printf("%s: %.2f\n", new Object[]{eventName, avgEventTimings.get(eventName)});
            }
            pw.decreaseIndent();
            pw.decreaseIndent();
        }
    }

    public EventManager(SessionManager.ISessionIdQueryHandler l) {
        this.mSessionIdHandler = l;
    }

    public void event(Loggable recordEntry, String event, Object data) {
        String currentSessionID = this.mSessionIdHandler.getSessionId();
        if (recordEntry == null) {
            Log.m134i(TAG, "Non-call EVENT: %s, %s", event, data);
            return;
        }
        synchronized (this.mEventRecords) {
            if (!this.mCallEventRecordMap.containsKey(recordEntry)) {
                EventRecord newRecord = new EventRecord(recordEntry);
                addEventRecord(newRecord);
            }
            EventRecord record = this.mCallEventRecordMap.get(recordEntry);
            record.addEvent(event, currentSessionID, data);
        }
    }

    public void event(Loggable recordEntry, String event, String format, Object... args) {
        String msg;
        String format2;
        if (args != null) {
            try {
            } catch (IllegalFormatException ife) {
                Log.m137e(this, ife, "IllegalFormatException: formatString='%s' numArgs=%d", format, Integer.valueOf(args.length));
                msg = format + " (An error occurred while formatting the message.)";
            }
            if (args.length != 0) {
                format2 = String.format(Locale.US, format, args);
                msg = format2;
                event(recordEntry, event, msg);
            }
        }
        format2 = format;
        msg = format2;
        event(recordEntry, event, msg);
    }

    public void dumpEvents(IndentingPrintWriter pw) {
        pw.println("Historical Events:");
        pw.increaseIndent();
        Iterator<EventRecord> it = this.mEventRecords.iterator();
        while (it.hasNext()) {
            EventRecord eventRecord = it.next();
            eventRecord.dump(pw);
        }
        pw.decreaseIndent();
    }

    public void dumpEventsTimeline(IndentingPrintWriter pw) {
        pw.println("Historical Events (sorted by time):");
        List<Pair<Loggable, Event>> events = new ArrayList<>();
        Iterator<EventRecord> it = this.mEventRecords.iterator();
        while (it.hasNext()) {
            EventRecord er = it.next();
            for (Event ev : er.getEvents()) {
                events.add(new Pair<>(er.getRecordEntry(), ev));
            }
        }
        Comparator<Pair<Loggable, Event>> byEventTime = Comparator.comparingLong(new ToLongFunction() { // from class: android.telecom.Logging.EventManager$$ExternalSyntheticLambda0
            @Override // java.util.function.ToLongFunction
            public final long applyAsLong(Object obj) {
                long j;
                j = ((EventManager.Event) ((Pair) obj).second).time;
                return j;
            }
        });
        events.sort(byEventTime);
        pw.increaseIndent();
        for (Pair<Loggable, Event> event : events) {
            pw.print(((Event) event.second).timestampString);
            pw.print(",");
            pw.print(((Loggable) event.first).getId());
            pw.print(",");
            pw.print(((Event) event.second).eventId);
            pw.print(",");
            pw.println(((Event) event.second).data);
        }
        pw.decreaseIndent();
    }

    public void changeEventCacheSize(int newSize) {
        LinkedBlockingQueue<EventRecord> oldEventLog = this.mEventRecords;
        this.mEventRecords = new LinkedBlockingQueue<>(newSize);
        this.mCallEventRecordMap.clear();
        oldEventLog.forEach(new Consumer() { // from class: android.telecom.Logging.EventManager$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                EventManager.this.lambda$changeEventCacheSize$1((EventManager.EventRecord) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$changeEventCacheSize$1(EventRecord newRecord) {
        EventRecord record;
        Loggable recordEntry = newRecord.getRecordEntry();
        if (this.mEventRecords.remainingCapacity() == 0 && (record = this.mEventRecords.poll()) != null) {
            this.mCallEventRecordMap.remove(record.getRecordEntry());
        }
        this.mEventRecords.add(newRecord);
        this.mCallEventRecordMap.put(recordEntry, newRecord);
    }

    public void registerEventListener(EventListener e) {
        if (e != null) {
            synchronized (mSync) {
                this.mEventListeners.add(e);
            }
        }
    }

    public LinkedBlockingQueue<EventRecord> getEventRecords() {
        return this.mEventRecords;
    }

    public Map<Loggable, EventRecord> getCallEventRecordMap() {
        return this.mCallEventRecordMap;
    }

    private void addEventRecord(EventRecord newRecord) {
        EventRecord record;
        Loggable recordEntry = newRecord.getRecordEntry();
        if (this.mEventRecords.remainingCapacity() == 0 && (record = this.mEventRecords.poll()) != null) {
            this.mCallEventRecordMap.remove(record.getRecordEntry());
        }
        this.mEventRecords.add(newRecord);
        this.mCallEventRecordMap.put(recordEntry, newRecord);
        synchronized (mSync) {
            for (EventListener l : this.mEventListeners) {
                l.eventRecordAdded(newRecord);
            }
        }
    }
}
