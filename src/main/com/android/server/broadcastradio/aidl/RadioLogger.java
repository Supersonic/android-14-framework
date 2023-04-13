package com.android.server.broadcastradio.aidl;

import android.text.TextUtils;
import android.util.IndentingPrintWriter;
import android.util.LocalLog;
import android.util.Log;
import com.android.server.utils.Slogf;
/* loaded from: classes.dex */
public final class RadioLogger {
    public final boolean mDebug;
    public final LocalLog mEventLogger;
    public final String mTag;

    public RadioLogger(String str, int i) {
        this.mTag = str;
        this.mDebug = Log.isLoggable(str, 3);
        this.mEventLogger = new LocalLog(i);
    }

    public void logRadioEvent(String str, Object... objArr) {
        this.mEventLogger.log(TextUtils.formatSimple(str, objArr));
        if (this.mDebug) {
            Slogf.m28d(this.mTag, str, objArr);
        }
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        this.mEventLogger.dump(indentingPrintWriter);
    }
}
