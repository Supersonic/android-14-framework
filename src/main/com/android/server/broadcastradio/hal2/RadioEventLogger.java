package com.android.server.broadcastradio.hal2;

import android.util.IndentingPrintWriter;
import android.util.LocalLog;
import android.util.Log;
import android.util.Slog;
/* loaded from: classes.dex */
public final class RadioEventLogger {
    public final LocalLog mEventLogger;
    public final String mTag;

    public RadioEventLogger(String str, int i) {
        this.mTag = str;
        this.mEventLogger = new LocalLog(i);
    }

    public void logRadioEvent(String str, Object... objArr) {
        String format = String.format(str, objArr);
        this.mEventLogger.log(format);
        if (Log.isLoggable(this.mTag, 3)) {
            Slog.d(this.mTag, format);
        }
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        this.mEventLogger.dump(indentingPrintWriter);
    }
}
