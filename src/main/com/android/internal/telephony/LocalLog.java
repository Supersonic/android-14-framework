package com.android.internal.telephony;

import android.os.SystemClock;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
/* loaded from: classes.dex */
public final class LocalLog {
    private final Deque<String> mLog;
    private final int mMaxLines;
    private final boolean mUseLocalTimestamps;

    public LocalLog(int i) {
        this(i, true);
    }

    public LocalLog(int i, boolean z) {
        int max = Math.max(0, i);
        this.mMaxLines = max;
        this.mLog = new ArrayDeque(max);
        this.mUseLocalTimestamps = z;
    }

    public void log(String str) {
        String str2;
        if (this.mMaxLines <= 0) {
            return;
        }
        if (this.mUseLocalTimestamps) {
            str2 = LocalDateTime.now() + " - " + str;
        } else {
            str2 = Duration.ofMillis(SystemClock.elapsedRealtime()) + " / " + Instant.now() + " - " + str;
        }
        append(str2);
    }

    private synchronized void append(String str) {
        while (this.mLog.size() >= this.mMaxLines) {
            this.mLog.remove();
        }
        this.mLog.add(str);
    }

    public synchronized void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        dump(printWriter);
    }

    public synchronized void dump(PrintWriter printWriter) {
        dump(PhoneConfigurationManager.SSSS, printWriter);
    }

    public synchronized void dump(String str, PrintWriter printWriter) {
        Iterator<String> it = this.mLog.iterator();
        while (it.hasNext()) {
            printWriter.printf("%s%s\n", str, it.next());
        }
    }

    public synchronized void reverseDump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        reverseDump(printWriter);
    }

    public synchronized void reverseDump(PrintWriter printWriter) {
        Iterator<String> descendingIterator = this.mLog.descendingIterator();
        while (descendingIterator.hasNext()) {
            printWriter.println(descendingIterator.next());
        }
    }

    /* loaded from: classes.dex */
    public static class ReadOnlyLocalLog {
        private final LocalLog mLog;

        ReadOnlyLocalLog(LocalLog localLog) {
            this.mLog = localLog;
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            this.mLog.dump(printWriter);
        }

        public void dump(PrintWriter printWriter) {
            this.mLog.dump(printWriter);
        }

        public void reverseDump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            this.mLog.reverseDump(printWriter);
        }

        public void reverseDump(PrintWriter printWriter) {
            this.mLog.reverseDump(printWriter);
        }
    }

    public ReadOnlyLocalLog readOnlyLocalLog() {
        return new ReadOnlyLocalLog(this);
    }
}
