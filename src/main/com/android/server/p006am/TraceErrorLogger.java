package com.android.server.p006am;

import android.os.Trace;
import java.util.UUID;
/* renamed from: com.android.server.am.TraceErrorLogger */
/* loaded from: classes.dex */
public class TraceErrorLogger {
    public boolean isAddErrorIdEnabled() {
        return true;
    }

    public UUID generateErrorId() {
        return UUID.randomUUID();
    }

    public void addErrorIdToTrace(String str, UUID uuid) {
        Trace.traceCounter(64L, "ErrorId:" + str + "#" + uuid.toString(), 1);
    }

    public void addSubjectToTrace(String str, UUID uuid) {
        Trace.traceCounter(64L, String.format("Subject(for ErrorId %s):%s", uuid.toString(), str), 1);
    }
}
