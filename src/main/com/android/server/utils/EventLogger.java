package com.android.server.utils;

import android.util.Log;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
/* loaded from: classes2.dex */
public class EventLogger {
    public final ArrayDeque<Event> mEvents;
    public final int mMemSize;
    public final String mTag;

    /* loaded from: classes2.dex */
    public interface DumpSink {
        void sink(String str, List<Event> list);
    }

    public EventLogger(int i, String str) {
        this.mEvents = new ArrayDeque<>(i);
        this.mMemSize = i;
        this.mTag = str;
    }

    public synchronized void enqueue(Event event) {
        if (this.mEvents.size() >= this.mMemSize) {
            this.mEvents.removeFirst();
        }
        this.mEvents.addLast(event);
    }

    public synchronized void enqueueAndLog(String str, int i, String str2) {
        enqueue(new StringEvent(str).printLog(i, str2));
    }

    public synchronized void dump(DumpSink dumpSink) {
        dumpSink.sink(this.mTag, new ArrayList(this.mEvents));
    }

    public synchronized void dump(PrintWriter printWriter) {
        dump(printWriter, "");
    }

    public String getDumpTitle() {
        if (this.mTag == null) {
            return "Events log: ";
        }
        return "Events log: " + this.mTag;
    }

    public synchronized void dump(PrintWriter printWriter, String str) {
        printWriter.println(getDumpTitle());
        Iterator<Event> it = this.mEvents.iterator();
        while (it.hasNext()) {
            printWriter.println(str + it.next().toString());
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Event {
        public static final SimpleDateFormat sFormat = new SimpleDateFormat("MM-dd HH:mm:ss:SSS", Locale.US);
        public final long mTimestamp = System.currentTimeMillis();

        public abstract String eventToString();

        public String toString() {
            return sFormat.format(new Date(this.mTimestamp)) + " " + eventToString();
        }

        public Event printLog(String str) {
            return printLog(0, str);
        }

        public Event printLog(int i, String str) {
            if (i == 0) {
                Log.i(str, eventToString());
            } else if (i == 1) {
                Log.e(str, eventToString());
            } else if (i == 2) {
                Log.w(str, eventToString());
            } else {
                Log.v(str, eventToString());
            }
            return this;
        }
    }

    /* loaded from: classes2.dex */
    public static class StringEvent extends Event {
        public final String mDescription;
        public final String mSource;

        public StringEvent(String str) {
            this(null, str);
        }

        public StringEvent(String str, String str2) {
            this.mSource = str;
            this.mDescription = str2;
        }

        @Override // com.android.server.utils.EventLogger.Event
        public String eventToString() {
            String str = this.mSource;
            if (str == null) {
                return this.mDescription;
            }
            Object[] objArr = new Object[2];
            objArr[0] = str;
            String str2 = this.mDescription;
            if (str2 == null) {
                str2 = "";
            }
            objArr[1] = str2;
            return String.format("[%-40s] %s", objArr);
        }
    }
}
