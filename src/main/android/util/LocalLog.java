package android.util;

import android.p008os.SystemClock;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
/* loaded from: classes3.dex */
public final class LocalLog {
    private final Deque<String> mLog;
    private final int mMaxLines;
    private final boolean mUseLocalTimestamps;

    public LocalLog(int maxLines) {
        this(maxLines, true);
    }

    public LocalLog(int maxLines, boolean useLocalTimestamps) {
        int max = Math.max(0, maxLines);
        this.mMaxLines = max;
        this.mLog = new ArrayDeque(max);
        this.mUseLocalTimestamps = useLocalTimestamps;
    }

    public void log(String msg) {
        String logLine;
        if (this.mMaxLines <= 0) {
            return;
        }
        if (this.mUseLocalTimestamps) {
            logLine = LocalDateTime.now() + " - " + msg;
        } else {
            logLine = java.time.Duration.ofMillis(SystemClock.elapsedRealtime()) + " / " + Instant.now() + " - " + msg;
        }
        append(logLine);
    }

    private synchronized void append(String logLine) {
        while (this.mLog.size() >= this.mMaxLines) {
            this.mLog.remove();
        }
        this.mLog.add(logLine);
    }

    public synchronized void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        dump(pw);
    }

    public synchronized void dump(PrintWriter pw) {
        dump("", pw);
    }

    public synchronized void dump(String indent, PrintWriter pw) {
        Iterator<String> itr = this.mLog.iterator();
        while (itr.hasNext()) {
            pw.printf("%s%s\n", indent, itr.next());
        }
    }

    public synchronized void reverseDump(FileDescriptor fd, PrintWriter pw, String[] args) {
        reverseDump(pw);
    }

    public synchronized void reverseDump(PrintWriter pw) {
        Iterator<String> itr = this.mLog.descendingIterator();
        while (itr.hasNext()) {
            pw.println(itr.next());
        }
    }

    /* loaded from: classes3.dex */
    public static class ReadOnlyLocalLog {
        private final LocalLog mLog;

        ReadOnlyLocalLog(LocalLog log) {
            this.mLog = log;
        }

        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            this.mLog.dump(pw);
        }

        public void dump(PrintWriter pw) {
            this.mLog.dump(pw);
        }

        public void reverseDump(FileDescriptor fd, PrintWriter pw, String[] args) {
            this.mLog.reverseDump(pw);
        }

        public void reverseDump(PrintWriter pw) {
            this.mLog.reverseDump(pw);
        }
    }

    public ReadOnlyLocalLog readOnlyLocalLog() {
        return new ReadOnlyLocalLog(this);
    }
}
