package com.android.server.trust;

import android.content.ComponentName;
import android.os.SystemClock;
import android.util.TimeUtils;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Iterator;
/* loaded from: classes2.dex */
public class TrustArchive {
    public ArrayDeque<Event> mEvents = new ArrayDeque<>();

    /* loaded from: classes2.dex */
    public static class Event {
        public final ComponentName agent;
        public final long duration;
        public final long elapsedTimestamp;
        public final int flags;
        public final boolean managingTrust;
        public final String message;
        public final int type;
        public final int userId;

        public Event(int i, int i2, ComponentName componentName, String str, long j, int i3, boolean z) {
            this.type = i;
            this.userId = i2;
            this.agent = componentName;
            this.elapsedTimestamp = SystemClock.elapsedRealtime();
            this.message = str;
            this.duration = j;
            this.flags = i3;
            this.managingTrust = z;
        }
    }

    public void logGrantTrust(int i, ComponentName componentName, String str, long j, int i2) {
        addEvent(new Event(0, i, componentName, str, j, i2, false));
    }

    public void logRevokeTrust(int i, ComponentName componentName) {
        addEvent(new Event(1, i, componentName, null, 0L, 0, false));
    }

    public void logTrustTimeout(int i, ComponentName componentName) {
        addEvent(new Event(2, i, componentName, null, 0L, 0, false));
    }

    public void logAgentDied(int i, ComponentName componentName) {
        addEvent(new Event(3, i, componentName, null, 0L, 0, false));
    }

    public void logAgentConnected(int i, ComponentName componentName) {
        addEvent(new Event(4, i, componentName, null, 0L, 0, false));
    }

    public void logAgentStopped(int i, ComponentName componentName) {
        addEvent(new Event(5, i, componentName, null, 0L, 0, false));
    }

    public void logManagingTrust(int i, ComponentName componentName, boolean z) {
        addEvent(new Event(6, i, componentName, null, 0L, 0, z));
    }

    public void logDevicePolicyChanged() {
        addEvent(new Event(7, -1, null, null, 0L, 0, false));
    }

    public final void addEvent(Event event) {
        if (this.mEvents.size() >= 200) {
            this.mEvents.removeFirst();
        }
        this.mEvents.addLast(event);
    }

    public void dump(PrintWriter printWriter, int i, int i2, String str, boolean z) {
        int i3;
        Iterator<Event> descendingIterator = this.mEvents.descendingIterator();
        int i4 = 0;
        while (descendingIterator.hasNext() && i4 < i) {
            Event next = descendingIterator.next();
            if (i2 == -1 || i2 == (i3 = next.userId) || i3 == -1) {
                printWriter.print(str);
                printWriter.printf("#%-2d %s %s: ", Integer.valueOf(i4), formatElapsed(next.elapsedTimestamp), dumpType(next.type));
                if (i2 == -1) {
                    printWriter.print("user=");
                    printWriter.print(next.userId);
                    printWriter.print(", ");
                }
                if (next.agent != null) {
                    printWriter.print("agent=");
                    if (z) {
                        printWriter.print(next.agent.flattenToShortString());
                    } else {
                        printWriter.print(getSimpleName(next.agent));
                    }
                }
                int i5 = next.type;
                if (i5 == 0) {
                    printWriter.printf(", message=\"%s\", duration=%s, flags=%s", next.message, formatDuration(next.duration), dumpGrantFlags(next.flags));
                } else if (i5 == 6) {
                    printWriter.printf(", managingTrust=" + next.managingTrust, new Object[0]);
                }
                printWriter.println();
                i4++;
            }
        }
    }

    public static String formatDuration(long j) {
        StringBuilder sb = new StringBuilder();
        TimeUtils.formatDuration(j, sb);
        return sb.toString();
    }

    public static String formatElapsed(long j) {
        return TimeUtils.logTimeOfDay((j - SystemClock.elapsedRealtime()) + System.currentTimeMillis());
    }

    public static String getSimpleName(ComponentName componentName) {
        String className = componentName.getClassName();
        int lastIndexOf = className.lastIndexOf(46);
        return (lastIndexOf >= className.length() || lastIndexOf < 0) ? className : className.substring(lastIndexOf + 1);
    }

    public final String dumpType(int i) {
        switch (i) {
            case 0:
                return "GrantTrust";
            case 1:
                return "RevokeTrust";
            case 2:
                return "TrustTimeout";
            case 3:
                return "AgentDied";
            case 4:
                return "AgentConnected";
            case 5:
                return "AgentStopped";
            case 6:
                return "ManagingTrust";
            case 7:
                return "DevicePolicyChanged";
            default:
                return "Unknown(" + i + ")";
        }
    }

    public final String dumpGrantFlags(int i) {
        StringBuilder sb = new StringBuilder();
        if ((i & 1) != 0) {
            if (sb.length() != 0) {
                sb.append('|');
            }
            sb.append("INITIATED_BY_USER");
        }
        if ((i & 2) != 0) {
            if (sb.length() != 0) {
                sb.append('|');
            }
            sb.append("DISMISS_KEYGUARD");
        }
        if (sb.length() == 0) {
            sb.append('0');
        }
        return sb.toString();
    }
}
