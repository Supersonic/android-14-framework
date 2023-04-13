package com.android.server.utils;

import android.util.Slog;
import android.util.TimingsTraceLog;
/* loaded from: classes2.dex */
public final class TimingsTraceAndSlog extends TimingsTraceLog {
    public final String mTag;

    public static TimingsTraceAndSlog newAsyncLog() {
        return new TimingsTraceAndSlog("SystemServerTimingAsync", 524288L);
    }

    public TimingsTraceAndSlog() {
        this("SystemServerTiming");
    }

    public TimingsTraceAndSlog(String str) {
        this(str, 524288L);
    }

    public TimingsTraceAndSlog(String str, long j) {
        super(str, j);
        this.mTag = str;
    }

    public TimingsTraceAndSlog(TimingsTraceAndSlog timingsTraceAndSlog) {
        super(timingsTraceAndSlog);
        this.mTag = timingsTraceAndSlog.mTag;
    }

    public void traceBegin(String str) {
        Slog.d(this.mTag, str);
        super.traceBegin(str);
    }

    public void logDuration(String str, long j) {
        super.logDuration(str, j);
    }

    public String toString() {
        return "TimingsTraceAndSlog[" + this.mTag + "]";
    }
}
