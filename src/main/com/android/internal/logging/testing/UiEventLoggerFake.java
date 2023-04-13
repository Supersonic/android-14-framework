package com.android.internal.logging.testing;

import com.android.internal.logging.InstanceId;
import com.android.internal.logging.UiEventLogger;
import java.util.LinkedList;
import java.util.List;
/* loaded from: classes4.dex */
public class UiEventLoggerFake implements UiEventLogger {
    private List<FakeUiEvent> mLogs = new LinkedList();

    /* loaded from: classes4.dex */
    public static class FakeUiEvent {
        public final int eventId;
        public final InstanceId instanceId;
        public final String packageName;
        public final int position;
        public final int uid;

        FakeUiEvent(int eventId, int uid, String packageName) {
            this.eventId = eventId;
            this.uid = uid;
            this.packageName = packageName;
            this.instanceId = null;
            this.position = 0;
        }

        FakeUiEvent(int eventId, int uid, String packageName, InstanceId instanceId) {
            this.eventId = eventId;
            this.uid = uid;
            this.packageName = packageName;
            this.instanceId = instanceId;
            this.position = 0;
        }

        FakeUiEvent(int eventId, int uid, String packageName, InstanceId instanceId, int position) {
            this.eventId = eventId;
            this.uid = uid;
            this.packageName = packageName;
            this.instanceId = instanceId;
            this.position = position;
        }
    }

    public List<FakeUiEvent> getLogs() {
        return this.mLogs;
    }

    public int numLogs() {
        return this.mLogs.size();
    }

    public FakeUiEvent get(int index) {
        return this.mLogs.get(index);
    }

    public int eventId(int index) {
        return this.mLogs.get(index).eventId;
    }

    @Override // com.android.internal.logging.UiEventLogger
    public void log(UiEventLogger.UiEventEnum event) {
        log(event, 0, null);
    }

    @Override // com.android.internal.logging.UiEventLogger
    public void log(UiEventLogger.UiEventEnum event, InstanceId instance) {
        logWithInstanceId(event, 0, null, instance);
    }

    @Override // com.android.internal.logging.UiEventLogger
    public void log(UiEventLogger.UiEventEnum event, int uid, String packageName) {
        int eventId = event.getId();
        if (eventId > 0) {
            this.mLogs.add(new FakeUiEvent(eventId, uid, packageName));
        }
    }

    @Override // com.android.internal.logging.UiEventLogger
    public void logWithInstanceId(UiEventLogger.UiEventEnum event, int uid, String packageName, InstanceId instance) {
        int eventId = event.getId();
        if (eventId > 0) {
            this.mLogs.add(new FakeUiEvent(eventId, uid, packageName, instance));
        }
    }

    @Override // com.android.internal.logging.UiEventLogger
    public void logWithPosition(UiEventLogger.UiEventEnum event, int uid, String packageName, int position) {
        int eventId = event.getId();
        if (eventId > 0) {
            this.mLogs.add(new FakeUiEvent(eventId, uid, packageName, null, position));
        }
    }

    @Override // com.android.internal.logging.UiEventLogger
    public void logWithInstanceIdAndPosition(UiEventLogger.UiEventEnum event, int uid, String packageName, InstanceId instance, int position) {
        int eventId = event.getId();
        if (eventId > 0) {
            this.mLogs.add(new FakeUiEvent(eventId, uid, packageName, instance, position));
        }
    }
}
