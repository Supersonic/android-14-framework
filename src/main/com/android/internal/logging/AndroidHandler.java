package com.android.internal.logging;

import android.media.MediaMetrics;
import android.util.Log;
import com.android.internal.util.FastPrintWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
/* loaded from: classes4.dex */
public class AndroidHandler extends Handler {
    private static final Formatter THE_FORMATTER = new Formatter() { // from class: com.android.internal.logging.AndroidHandler.1
        @Override // java.util.logging.Formatter
        public String format(LogRecord r) {
            Throwable thrown = r.getThrown();
            if (thrown != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new FastPrintWriter((Writer) sw, false, 256);
                sw.write(r.getMessage());
                sw.write("\n");
                thrown.printStackTrace(pw);
                pw.flush();
                return sw.toString();
            }
            return r.getMessage();
        }
    };

    public AndroidHandler() {
        setFormatter(THE_FORMATTER);
    }

    @Override // java.util.logging.Handler
    public void close() {
    }

    @Override // java.util.logging.Handler
    public void flush() {
    }

    private static String loggerNameToTag(String loggerName) {
        if (loggerName == null) {
            return "null";
        }
        int length = loggerName.length();
        if (length <= 23) {
            return loggerName;
        }
        int lastPeriod = loggerName.lastIndexOf(MediaMetrics.SEPARATOR);
        if (length - (lastPeriod + 1) > 23) {
            return loggerName.substring(loggerName.length() - 23);
        }
        return loggerName.substring(lastPeriod + 1);
    }

    @Override // java.util.logging.Handler
    public void publish(LogRecord record) {
        int level = getAndroidLevel(record.getLevel());
        String tag = loggerNameToTag(record.getLoggerName());
        if (!Log.isLoggable(tag, level)) {
            return;
        }
        try {
            String message = getFormatter().format(record);
            Log.println(level, tag, message);
        } catch (RuntimeException e) {
            Log.m109e("AndroidHandler", "Error logging message.", e);
        }
    }

    public void publish(Logger source, String tag, Level level, String message) {
        int priority = getAndroidLevel(level);
        if (!Log.isLoggable(tag, priority)) {
            return;
        }
        try {
            Log.println(priority, tag, message);
        } catch (RuntimeException e) {
            Log.m109e("AndroidHandler", "Error logging message.", e);
        }
    }

    static int getAndroidLevel(Level level) {
        int value = level.intValue();
        if (value >= 1000) {
            return 6;
        }
        if (value >= 900) {
            return 5;
        }
        if (value >= 800) {
            return 4;
        }
        return 3;
    }
}
