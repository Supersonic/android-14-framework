package com.android.server.usb;

import com.android.internal.util.dump.DualDumpOutputStream;
import com.android.server.utils.EventLogger;
import java.util.List;
/* loaded from: classes2.dex */
public final class DualOutputStreamDumpSink implements EventLogger.DumpSink {
    public final DualDumpOutputStream mDumpOutputStream;
    public final long mId;

    public DualOutputStreamDumpSink(DualDumpOutputStream dualDumpOutputStream, long j) {
        this.mDumpOutputStream = dualDumpOutputStream;
        this.mId = j;
    }

    @Override // com.android.server.utils.EventLogger.DumpSink
    public void sink(String str, List<EventLogger.Event> list) {
        this.mDumpOutputStream.write("USB Event Log", this.mId, str);
        for (EventLogger.Event event : list) {
            this.mDumpOutputStream.write("USB Event", this.mId, event.toString());
        }
    }
}
