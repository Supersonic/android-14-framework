package com.android.server.usage;

import android.app.ActivityManager;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Slog;
import android.util.TimeUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.RingBuffer;
/* loaded from: classes2.dex */
public class BroadcastResponseStatsLogger {
    public static final int MAX_LOG_SIZE;
    @GuardedBy({"mLock"})
    public final LogBuffer mBroadcastEventsBuffer;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final LogBuffer mNotificationEventsBuffer;

    /* loaded from: classes2.dex */
    public interface Data {
        void reset();
    }

    public BroadcastResponseStatsLogger() {
        int i = MAX_LOG_SIZE;
        this.mBroadcastEventsBuffer = new LogBuffer(BroadcastEvent.class, i);
        this.mNotificationEventsBuffer = new LogBuffer(NotificationEvent.class, i);
    }

    static {
        MAX_LOG_SIZE = ActivityManager.isLowRamDeviceStatic() ? 20 : 50;
    }

    public void logBroadcastDispatchEvent(int i, String str, UserHandle userHandle, long j, long j2, int i2) {
        synchronized (this.mLock) {
            if (UsageStatsService.DEBUG_RESPONSE_STATS) {
                Slog.d("ResponseStatsTracker", getBroadcastDispatchEventLog(i, str, userHandle.getIdentifier(), j, j2, i2));
            }
            this.mBroadcastEventsBuffer.logBroadcastDispatchEvent(i, str, userHandle, j, j2, i2);
        }
    }

    public void logNotificationEvent(int i, String str, UserHandle userHandle, long j) {
        synchronized (this.mLock) {
            if (UsageStatsService.DEBUG_RESPONSE_STATS) {
                Slog.d("ResponseStatsTracker", getNotificationEventLog(i, str, userHandle.getIdentifier(), j));
            }
            this.mNotificationEventsBuffer.logNotificationEvent(i, str, userHandle, j);
        }
    }

    public void dumpLogs(IndentingPrintWriter indentingPrintWriter) {
        synchronized (this.mLock) {
            indentingPrintWriter.println("Broadcast events (most recent first):");
            indentingPrintWriter.increaseIndent();
            this.mBroadcastEventsBuffer.reverseDump(indentingPrintWriter);
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println();
            indentingPrintWriter.println("Notification events (most recent first):");
            indentingPrintWriter.increaseIndent();
            this.mNotificationEventsBuffer.reverseDump(indentingPrintWriter);
            indentingPrintWriter.decreaseIndent();
        }
    }

    /* loaded from: classes2.dex */
    public static final class LogBuffer<T extends Data> extends RingBuffer<T> {
        public LogBuffer(Class<T> cls, int i) {
            super(cls, i);
        }

        public void logBroadcastDispatchEvent(int i, String str, UserHandle userHandle, long j, long j2, int i2) {
            Data data = (Data) getNextSlot();
            if (data == null) {
                return;
            }
            data.reset();
            BroadcastEvent broadcastEvent = (BroadcastEvent) data;
            broadcastEvent.sourceUid = i;
            broadcastEvent.targetUserId = userHandle.getIdentifier();
            broadcastEvent.targetUidProcessState = i2;
            broadcastEvent.targetPackage = str;
            broadcastEvent.idForResponseEvent = j;
            broadcastEvent.timestampMs = j2;
        }

        public void logNotificationEvent(int i, String str, UserHandle userHandle, long j) {
            Data data = (Data) getNextSlot();
            if (data == null) {
                return;
            }
            data.reset();
            NotificationEvent notificationEvent = (NotificationEvent) data;
            notificationEvent.type = i;
            notificationEvent.packageName = str;
            notificationEvent.userId = userHandle.getIdentifier();
            notificationEvent.timestampMs = j;
        }

        public void reverseDump(IndentingPrintWriter indentingPrintWriter) {
            Data[] dataArr = (Data[]) toArray();
            for (int length = dataArr.length - 1; length >= 0; length--) {
                Data data = dataArr[length];
                if (data != null) {
                    indentingPrintWriter.println(getContent(data));
                }
            }
        }

        public String getContent(Data data) {
            return data.toString();
        }
    }

    public static String getBroadcastDispatchEventLog(int i, String str, int i2, long j, long j2, int i3) {
        return TextUtils.formatSimple("broadcast:%s; srcUid=%d, tgtPkg=%s, tgtUsr=%d, id=%d, state=%s", new Object[]{TimeUtils.formatDuration(j2), Integer.valueOf(i), str, Integer.valueOf(i2), Long.valueOf(j), ActivityManager.procStateToString(i3)});
    }

    public static String getNotificationEventLog(int i, String str, int i2, long j) {
        return TextUtils.formatSimple("notification:%s; event=<%s>, pkg=%s, usr=%d", new Object[]{TimeUtils.formatDuration(j), notificationEventToString(i), str, Integer.valueOf(i2)});
    }

    public static String notificationEventToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? String.valueOf(i) : "cancelled" : "updated" : "posted";
    }

    /* loaded from: classes2.dex */
    public static final class BroadcastEvent implements Data {
        public long idForResponseEvent;
        public int sourceUid;
        public String targetPackage;
        public int targetUidProcessState;
        public int targetUserId;
        public long timestampMs;

        @Override // com.android.server.usage.BroadcastResponseStatsLogger.Data
        public void reset() {
            this.targetPackage = null;
        }

        public String toString() {
            return BroadcastResponseStatsLogger.getBroadcastDispatchEventLog(this.sourceUid, this.targetPackage, this.targetUserId, this.idForResponseEvent, this.timestampMs, this.targetUidProcessState);
        }
    }

    /* loaded from: classes2.dex */
    public static final class NotificationEvent implements Data {
        public String packageName;
        public long timestampMs;
        public int type;
        public int userId;

        @Override // com.android.server.usage.BroadcastResponseStatsLogger.Data
        public void reset() {
            this.packageName = null;
        }

        public String toString() {
            return BroadcastResponseStatsLogger.getNotificationEventLog(this.type, this.packageName, this.userId, this.timestampMs);
        }
    }
}
