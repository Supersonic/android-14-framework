package com.android.internal.p028os;

import android.util.Log;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: com.android.internal.os.AndroidPrintStream */
/* loaded from: classes4.dex */
public class AndroidPrintStream extends LoggingPrintStream {
    private final int priority;
    private final String tag;

    public AndroidPrintStream(int priority, String tag) {
        if (tag == null) {
            throw new NullPointerException("tag");
        }
        this.priority = priority;
        this.tag = tag;
    }

    @Override // com.android.internal.p028os.LoggingPrintStream
    protected void log(String line) {
        Log.println(this.priority, this.tag, line);
    }
}
