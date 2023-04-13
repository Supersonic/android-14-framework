package com.android.server.location.contexthub;

import android.hardware.location.NanoAppMessage;
import android.util.Log;
import java.util.Iterator;
/* loaded from: classes.dex */
public class ContextHubEventLogger {
    public static ContextHubEventLogger sInstance;
    public final ConcurrentLinkedEvictingDeque<NanoappLoadEvent> mNanoappLoadEventQueue = new ConcurrentLinkedEvictingDeque<>(20);
    public final ConcurrentLinkedEvictingDeque<NanoappUnloadEvent> mNanoappUnloadEventQueue = new ConcurrentLinkedEvictingDeque<>(20);
    public final ConcurrentLinkedEvictingDeque<NanoappMessageEvent> mMessageFromNanoappQueue = new ConcurrentLinkedEvictingDeque<>(20);
    public final ConcurrentLinkedEvictingDeque<NanoappMessageEvent> mMessageToNanoappQueue = new ConcurrentLinkedEvictingDeque<>(20);
    public final ConcurrentLinkedEvictingDeque<ContextHubRestartEvent> mContextHubRestartEventQueue = new ConcurrentLinkedEvictingDeque<>(20);

    /* loaded from: classes.dex */
    public static class ContextHubEventBase {
        public final int contextHubId;
        public final long timeStampInMs;

        public ContextHubEventBase(long j, int i) {
            this.timeStampInMs = j;
            this.contextHubId = i;
        }
    }

    /* loaded from: classes.dex */
    public static class NanoappEventBase extends ContextHubEventBase {
        public final long nanoappId;
        public final boolean success;

        public NanoappEventBase(long j, int i, long j2, boolean z) {
            super(j, i);
            this.nanoappId = j2;
            this.success = z;
        }
    }

    /* loaded from: classes.dex */
    public static class NanoappLoadEvent extends NanoappEventBase {
        public final long nanoappSize;
        public final int nanoappVersion;

        public NanoappLoadEvent(long j, int i, long j2, int i2, long j3, boolean z) {
            super(j, i, j2, z);
            this.nanoappVersion = i2;
            this.nanoappSize = j3;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(ContextHubServiceUtil.formatDateFromTimestamp(this.timeStampInMs));
            sb.append(": NanoappLoadEvent[hubId = ");
            sb.append(this.contextHubId);
            sb.append(", appId = 0x");
            sb.append(Long.toHexString(this.nanoappId));
            sb.append(", appVersion = ");
            sb.append(this.nanoappVersion);
            sb.append(", appSize = ");
            sb.append(this.nanoappSize);
            sb.append(" bytes, success = ");
            sb.append(this.success ? "true" : "false");
            sb.append(']');
            return sb.toString();
        }
    }

    /* loaded from: classes.dex */
    public static class NanoappUnloadEvent extends NanoappEventBase {
        public NanoappUnloadEvent(long j, int i, long j2, boolean z) {
            super(j, i, j2, z);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(ContextHubServiceUtil.formatDateFromTimestamp(this.timeStampInMs));
            sb.append(": NanoappUnloadEvent[hubId = ");
            sb.append(this.contextHubId);
            sb.append(", appId = 0x");
            sb.append(Long.toHexString(this.nanoappId));
            sb.append(", success = ");
            sb.append(this.success ? "true" : "false");
            sb.append(']');
            return sb.toString();
        }
    }

    /* loaded from: classes.dex */
    public static class NanoappMessageEvent extends NanoappEventBase {
        public final NanoAppMessage message;

        public NanoappMessageEvent(long j, int i, NanoAppMessage nanoAppMessage, boolean z) {
            super(j, i, 0L, z);
            this.message = nanoAppMessage;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(ContextHubServiceUtil.formatDateFromTimestamp(this.timeStampInMs));
            sb.append(": NanoappMessageEvent[hubId = ");
            sb.append(this.contextHubId);
            sb.append(", ");
            sb.append(this.message.toString());
            sb.append(", success = ");
            sb.append(this.success ? "true" : "false");
            sb.append(']');
            return sb.toString();
        }
    }

    /* loaded from: classes.dex */
    public static class ContextHubRestartEvent extends ContextHubEventBase {
        public ContextHubRestartEvent(long j, int i) {
            super(j, i);
        }

        public String toString() {
            return ContextHubServiceUtil.formatDateFromTimestamp(this.timeStampInMs) + ": ContextHubRestartEvent[hubId = " + this.contextHubId + ']';
        }
    }

    public static synchronized ContextHubEventLogger getInstance() {
        ContextHubEventLogger contextHubEventLogger;
        synchronized (ContextHubEventLogger.class) {
            if (sInstance == null) {
                sInstance = new ContextHubEventLogger();
            }
            contextHubEventLogger = sInstance;
        }
        return contextHubEventLogger;
    }

    public synchronized void logNanoappLoad(int i, long j, int i2, long j2, boolean z) {
        NanoappLoadEvent nanoappLoadEvent = new NanoappLoadEvent(System.currentTimeMillis(), i, j, i2, j2, z);
        if (!this.mNanoappLoadEventQueue.add(nanoappLoadEvent)) {
            Log.e("ContextHubEventLogger", "Unable to add nanoapp load event to queue: " + nanoappLoadEvent);
        }
    }

    public synchronized void logNanoappUnload(int i, long j, boolean z) {
        NanoappUnloadEvent nanoappUnloadEvent = new NanoappUnloadEvent(System.currentTimeMillis(), i, j, z);
        if (!this.mNanoappUnloadEventQueue.add(nanoappUnloadEvent)) {
            Log.e("ContextHubEventLogger", "Unable to add nanoapp unload event to queue: " + nanoappUnloadEvent);
        }
    }

    public synchronized void logMessageFromNanoapp(int i, NanoAppMessage nanoAppMessage, boolean z) {
        if (nanoAppMessage == null) {
            return;
        }
        NanoappMessageEvent nanoappMessageEvent = new NanoappMessageEvent(System.currentTimeMillis(), i, nanoAppMessage, z);
        if (!this.mMessageFromNanoappQueue.add(nanoappMessageEvent)) {
            Log.e("ContextHubEventLogger", "Unable to add message from nanoapp event to queue: " + nanoappMessageEvent);
        }
    }

    public synchronized void logMessageToNanoapp(int i, NanoAppMessage nanoAppMessage, boolean z) {
        if (nanoAppMessage == null) {
            return;
        }
        NanoappMessageEvent nanoappMessageEvent = new NanoappMessageEvent(System.currentTimeMillis(), i, nanoAppMessage, z);
        if (!this.mMessageToNanoappQueue.add(nanoappMessageEvent)) {
            Log.e("ContextHubEventLogger", "Unable to add message to nanoapp event to queue: " + nanoappMessageEvent);
        }
    }

    public synchronized void logContextHubRestart(int i) {
        ContextHubRestartEvent contextHubRestartEvent = new ContextHubRestartEvent(System.currentTimeMillis(), i);
        if (!this.mContextHubRestartEventQueue.add(contextHubRestartEvent)) {
            Log.e("ContextHubEventLogger", "Unable to add Context Hub restart event to queue: " + contextHubRestartEvent);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nanoapp Loads:");
        sb.append(System.lineSeparator());
        Iterator<NanoappLoadEvent> it = this.mNanoappLoadEventQueue.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            sb.append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        sb.append("Nanoapp Unloads:");
        sb.append(System.lineSeparator());
        Iterator<NanoappUnloadEvent> it2 = this.mNanoappUnloadEventQueue.iterator();
        while (it2.hasNext()) {
            sb.append(it2.next());
            sb.append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        sb.append("Messages from Nanoapps:");
        sb.append(System.lineSeparator());
        Iterator<NanoappMessageEvent> it3 = this.mMessageFromNanoappQueue.iterator();
        while (it3.hasNext()) {
            sb.append(it3.next());
            sb.append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        sb.append("Messages to Nanoapps:");
        sb.append(System.lineSeparator());
        Iterator<NanoappMessageEvent> it4 = this.mMessageToNanoappQueue.iterator();
        while (it4.hasNext()) {
            sb.append(it4.next());
            sb.append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        sb.append("Context Hub Restarts:");
        sb.append(System.lineSeparator());
        Iterator<ContextHubRestartEvent> it5 = this.mContextHubRestartEventQueue.iterator();
        while (it5.hasNext()) {
            sb.append(it5.next());
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}
